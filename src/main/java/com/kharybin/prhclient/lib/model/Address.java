package com.kharybin.prhclient.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//POJO describing data structure, annotations are for
// setters/getters/constructors etc boilerplate code removing
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String street;
    private String city;
    private String postCode;
}
