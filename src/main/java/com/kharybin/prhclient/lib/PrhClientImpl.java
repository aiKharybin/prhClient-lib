package com.kharybin.prhclient.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kharybin.prhclient.lib.exception.JsonParsingRuntimeException;
import com.kharybin.prhclient.lib.exception.EmptyNodeRuntimeException;
import com.kharybin.prhclient.lib.model.Address;
import com.kharybin.prhclient.lib.model.CompanyData;
import com.kharybin.prhclient.lib.model.BusinessLine;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.StreamSupport;

import static com.kharybin.prhclient.lib.util.PrhRestTemplateBuilder.getConfiguredRestTemplate;

public class PrhClientImpl implements PrhClient {

    private final RestTemplate restTemplate;

    public PrhClientImpl() {
        this.restTemplate = getConfiguredRestTemplate();
    }

    @Override
    public Optional<CompanyData> getCompanyDataById(String businessId) {
        if (businessId == null || !businessId.matches("^\\d{7}-\\d$"))
            throw new IllegalArgumentException("Entered business Id parameter does not match the format (ddddddd-d)");
        JsonNode response = restTemplate.getForObject(businessId, JsonNode.class);
        if (isNodeEmpty(response)) throw new EmptyNodeRuntimeException("Response from PRH API is null or empty");
        return parseJsonForCompanyData(response);
    }

    Optional<CompanyData> parseJsonForCompanyData(JsonNode json) {
        JsonNode resultsNode = json.get("results");
        if (isNodeEmpty(resultsNode))
            throw new EmptyNodeRuntimeException("Node you are trying to parse is null or empty");
        JsonNode companyDataNode = resultsNode.get(0);
        if (isNodeEmpty(companyDataNode))
            throw new EmptyNodeRuntimeException("Node you are trying to parse is null or empty");
        CompanyData companyData = new CompanyData();
        parseForName(companyDataNode).ifPresent(companyData::setName);
        //setName(companyDataNode, companyData);
        setAddress(companyDataNode, companyData);
        setWebsite(companyDataNode, companyData);
        parseForWebsite(companyDataNode).ifPresent(companyData::setWebsite);
        parseForPrimaryBusinessLine(companyDataNode).ifPresent(companyData::setBusinessLine);
        //setPrimaryBusinessLine(companyDataNode, companyData);

        return Optional.of(companyData);
    }

    private boolean isNodeEmpty(JsonNode companyDataNode) {
        return companyDataNode == null || companyDataNode.isNull() || companyDataNode.isEmpty();
    }

    void setName(JsonNode companyDataNode, CompanyData companyData) {
        Optional.ofNullable(companyDataNode.get("name")).ifPresent(name -> companyData.setName(name.asText()));
    }

    Optional<String> parseForName(JsonNode companyDataNode) {
        JsonNode name = companyDataNode.get("name");
        if (isNodeEmpty(name)) return Optional.empty();
        return Optional.of(name.textValue());
    }

    void setPrimaryBusinessLine(JsonNode companyDataNode, CompanyData companyData) {
        JsonNode businessLineArrayNode = companyDataNode.get("businessLines");
        if (businessLineArrayNode != null && !businessLineArrayNode.isEmpty() && businessLineArrayNode.isArray()) {
            StreamSupport.stream(businessLineArrayNode.spliterator(), false)
                    .filter(businessLineNode -> (businessLineNode.get("name") != null && businessLineNode.get("code") != null)
                    ).findFirst().ifPresent(businessLineNode -> {
                BusinessLine businessLine = new BusinessLine();
                businessLine.setCode(businessLineNode.get("code").asText());
                businessLine.setDescription(businessLineNode.get("name").asText());
                companyData.setBusinessLine(businessLine);
            });
        }
    }

    Optional<BusinessLine> parseForPrimaryBusinessLine(JsonNode companyDataNode) {
        JsonNode businessLineArrayNode = companyDataNode.get("businessLines");
        if (businessLineArrayNode != null && !businessLineArrayNode.isEmpty() && businessLineArrayNode.isArray()) {
            Optional<JsonNode> optionalBusinessLineNode = StreamSupport.stream(businessLineArrayNode.spliterator(), false)
                    .filter(businessLineNode -> (
                                    isNodeEmpty(businessLineNode.get("name"))
                                            && isNodeEmpty(businessLineNode.get("code"))
                                            && isNodeEmpty(businessLineNode.get("order"))
                                            && businessLineNode.asInt() == 0
                            )
                    ).findFirst();

            if (optionalBusinessLineNode.isPresent()) {
                JsonNode businessLineNode = optionalBusinessLineNode.get();
                BusinessLine businessLine = new BusinessLine();
                businessLine.setCode(businessLineNode.get("code").asText());
                businessLine.setDescription(businessLineNode.get("name").asText());
                return Optional.of(businessLine);
            }
        }
        return Optional.empty();
    }

    void setWebsite(JsonNode companyDataNode, CompanyData companyData) {
        final Set<String> websiteTypes = Set.of("Kotisivun www-osoite", "www-adress", "Website address");
        JsonNode contactsListNode = companyDataNode.get("contactDetails");
        if (contactsListNode != null && !contactsListNode.isEmpty() && contactsListNode.isArray()) {
            StreamSupport.stream(contactsListNode.spliterator(), false)
                    .filter(contactNode -> {
                                String contactType = contactNode.get("type").textValue();
                                JsonNode valueNode = contactNode.get("value");
                                return (contactType != null
                                        && websiteTypes.contains(contactType)
                                        && valueNode != null
                                        && !valueNode.isNull());
                            }
                    ).findFirst().ifPresent(contactNode -> companyData.setWebsite(contactNode.get("value").asText())
            );
        }
    }

    Optional<String> parseForWebsite(JsonNode companyDataNode) {
        final Set<String> websiteTypes = Set.of("Kotisivun www-osoite", "www-adress", "Website address");
        JsonNode contactsListNode = companyDataNode.get("contactDetails");
        if (contactsListNode != null && !contactsListNode.isEmpty() && contactsListNode.isArray()) {
            Optional<JsonNode> optionalWebsiteNode = StreamSupport.stream(contactsListNode.spliterator(), false)
                    .filter(contactNode -> {
                                String contactType = contactNode.get("type").textValue();
                                JsonNode valueNode = contactNode.get("value");
                                return (contactType != null
                                        && websiteTypes.contains(contactType)
                                        && valueNode != null
                                        && !valueNode.isNull());
                            }
                    ).findFirst();
            if (optionalWebsiteNode.isPresent()) return Optional.of(optionalWebsiteNode.get().get("value").textValue());
        }
        return Optional.empty();
    }

    void setAddress(JsonNode companyDataNode, CompanyData companyData) {
        JsonNode addressesNode = companyDataNode.get("addresses");
        Optional<JsonNode> lastRegisteredAddressNode = StreamSupport.stream(addressesNode.spliterator(), false)
                .max(Comparator.comparing(a -> LocalDate.parse(a.get("registrationDate").asText())));
        lastRegisteredAddressNode.ifPresent(node -> {
            try {
                companyData.setAddress(new ObjectMapper().treeToValue(node, Address.class));
            } catch (JsonProcessingException e) {
                throw new JsonParsingRuntimeException("Error encountered during addresses branch parsing, please check API for updates", e);
            }
        });
    }
}
