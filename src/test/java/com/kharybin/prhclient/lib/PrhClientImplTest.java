package com.kharybin.prhclient.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kharybin.prhclient.lib.exception.HttpRequestRuntimeException;
import com.kharybin.prhclient.lib.model.Address;
import com.kharybin.prhclient.lib.model.CompanyData;
import com.kharybin.prhclient.lib.model.BusinessLine;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PrhClientImplTest {

    static JsonNode responseNode;

    PrhClientImpl prhClient = new PrhClientImpl();


    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        File resource = new File(Objects.requireNonNull(
                PrhClientImplTest.class.getClassLoader().getResource("response.json")).getFile());
        responseNode = new ObjectMapper().readValue(resource, JsonNode.class);
    }

    @Test
    void getCompanyDataById() {
        CompanyData companyData = prhClient.getCompanyDataById("2627773-7");
        assertEquals(prhClient.parseJsonForCompanyData(responseNode), companyData);
    }

    @Test
    void getCompanyDataByWrongId() {
        assertThrows(HttpRequestRuntimeException.class, () -> prhClient.getCompanyDataById("2617455-2"));
    }


    @ParameterizedTest()
    @ValueSource(strings = {"", "12345-1", "abcdefg-1", "12345678"})
    @NullSource
    void getCompanyDataByIdIllegalArgException(String businessId) {
        assertThrows(IllegalArgumentException.class, () -> prhClient.getCompanyDataById(businessId));
    }

    @Test
    void parseJson() {
        CompanyData companyData = prhClient.parseJsonForCompanyData(responseNode);
        assertEquals("Silmäasema Oyj", companyData.getName());
        assertEquals("www.silmaasema.fi", companyData.getWebsite());
        assertEquals(new Address("Radiokatu 3", "HELSINKI", "00240"), companyData.getAddress());
        assertEquals(new BusinessLine("64990", "Other financial service activities, except insurance and pension funding n.e.c."), companyData.getBusinessLine());
    }


    @Test
    void parseForName() {
        assertFalse(prhClient.parseForName(JsonNodeFactory.instance.objectNode()).isPresent());
    }
}