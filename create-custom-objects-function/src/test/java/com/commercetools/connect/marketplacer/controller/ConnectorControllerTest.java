package com.commercetools.connect.marketplacer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectorControllerTest {
    @LocalServerPort
    private long port;
    private final String url = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnStatus400ErrorBadRequestParameters() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), "{}", String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Bad request parameters", response.getBody());
    }
}

