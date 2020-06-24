package com.kharybin.prhclient.lib.util;

import com.kharybin.prhclient.lib.exception.HttpRequestRuntimeException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

public class PrhRestTemplateBuilder {

    private PrhRestTemplateBuilder() {
    }

    public static RestTemplate getConfiguredRestTemplate() {
        final String BASE_URL = "https://avoindata.prh.fi/bis/v1/";
        RestTemplate template = new RestTemplate();

        template.setUriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL));

        template.setErrorHandler(new ResponseErrorHandler() {

                                     @Override
                                     public boolean hasError(ClientHttpResponse response) throws IOException {
                                         return response.getStatusCode().series() == CLIENT_ERROR
                                                 || response.getStatusCode().series() == SERVER_ERROR;
                                     }

                                     @Override
                                     public void handleError(ClientHttpResponse response) throws IOException {
                                         if (response.getStatusCode().equals(NOT_FOUND)) {
                                             throw new HttpRequestRuntimeException("Page not found for this business id", NOT_FOUND);
                                         } else if (response.getStatusCode().equals(REQUEST_TIMEOUT)) {
                                             throw new HttpRequestRuntimeException("Request timed out, try again later", REQUEST_TIMEOUT);
                                         } else if (response.getStatusCode().series().equals(SERVER_ERROR)) {
                                             throw new HttpRequestRuntimeException("Something went wrong on server side, try again later", response.getStatusCode());
                                         } else if (response.getStatusCode().series().equals(CLIENT_ERROR)) {
                                             throw new HttpRequestRuntimeException("Error encountered during request", response.getStatusCode());
                                         }
                                     }
                                 }
        );
        return template;
    }
}
