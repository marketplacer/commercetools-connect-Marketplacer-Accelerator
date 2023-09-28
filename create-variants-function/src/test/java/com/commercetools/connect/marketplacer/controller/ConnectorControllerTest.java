package com.commercetools.connect.marketplacer.controller;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.Product;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import com.commercetools.connect.marketplacer.service.ConnectorService;
import com.commercetools.connect.marketplacer.utils.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.commercetools.connect.marketplacer.service.ConnectorService.createApiClient;
import static com.commercetools.connect.marketplacer.utils.TestUtils.readObjectFromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectorControllerTest {
    @LocalServerPort
    private long port;
    private final String url = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    private static ProjectApiRoot apiClient;

    @BeforeAll
    static void prepare() {
        apiClient = createApiClient();
    }

    @Test
    void shouldReturnStatus400ErrorBadRequestParameters() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), "{}", String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Bad request parameters", response.getBody());
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.MINUTES)
    void shouldCreateProductAndReturnStatus200() throws Exception {
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerRequest.json", MarketplacerRequest.class);
        String productKey = request.getPayload().getData().getNode().getLegacyId();

        TestUtils.deleteProductIfPresent(apiClient, productKey);

        ResponseEntity<String> response = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), TestUtils.gson.toJson(request), String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Product> createdProduct = ConnectorService.getProductByKey(productKey);
        assertTrue(createdProduct.isPresent());
    }
}
