package com.kharybin.prhclient.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyData {
    private String name;
    private String website;
    private Address address;
    private BusinessLine businessLine;
}

