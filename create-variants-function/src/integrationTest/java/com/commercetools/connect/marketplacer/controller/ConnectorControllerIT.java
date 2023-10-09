package com.commercetools.connect.marketplacer.controller;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.Product;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import com.commercetools.connect.marketplacer.utils.TestUtilsIT;
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

import static com.commercetools.connect.marketplacer.utils.TestUtilsIT.getProductByKey;
import static com.commercetools.connect.marketplacer.utils.TestUtilsIT.readObjectFromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConnectorControllerIT {
    @LocalServerPort
    private long port;
    private final String url = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    private final ProjectApiRoot apiClient = TestUtilsIT.createApiClient();

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
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerCreateRequest.json", MarketplacerRequest.class);
        String productKey = request.getPayload().getData().getNode().getLegacyId();

        TestUtilsIT.deleteProductIfPresent(apiClient, productKey);

        ResponseEntity<String> response = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), TestUtilsIT.gson.toJson(request), String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Product> createdProduct = getProductByKey(apiClient, productKey);
        assertTrue(createdProduct.isPresent());
    }

    @Test
    @Timeout(value = 4, unit = TimeUnit.MINUTES)
    void shouldUpdateProductAndReturnStatus200() throws Exception {
        // create product
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerUpdateRequest.json", MarketplacerRequest.class);
        String productKey = request.getPayload().getData().getNode().getLegacyId();

        TestUtilsIT.deleteProductIfPresent(apiClient, productKey);

        ResponseEntity<String> response = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), TestUtilsIT.gson.toJson(request), String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());

        Optional<Product> createdProduct = getProductByKey(apiClient, productKey);
        assertTrue(createdProduct.isPresent());

        // update request

        MarketplacerRequest updateRequest = readObjectFromResource("src/test/resources/updateRequestJson.json", MarketplacerRequest.class);
        String updateProductKey = updateRequest.getPayload().getData().getNode().getLegacyId();

        ResponseEntity<String> updateResponse = restTemplate.postForEntity(
                new URL(url + port + "/").toString(), TestUtilsIT.gson.toJson(updateRequest), String.class);
        assertTrue(updateResponse.getStatusCode().is2xxSuccessful());

        Optional<Product> updatedProduct = getProductByKey(apiClient, updateProductKey);
        assertTrue(updatedProduct.isPresent());
        assertEquals(updatedProduct.get().getMasterData().getStaged().getName().values().get("en"), "Product updated");
    }
}
