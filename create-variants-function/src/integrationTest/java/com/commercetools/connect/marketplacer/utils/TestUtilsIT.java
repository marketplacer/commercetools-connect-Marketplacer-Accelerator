package com.commercetools.connect.marketplacer.utils;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.product.Product;
import com.google.gson.Gson;
import io.vrap.rmf.base.client.error.NotFoundException;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

import static io.vrap.rmf.base.client.utils.json.JsonUtils.fromInputStream;

public class TestUtilsIT {

    private static final Logger logger = Logger.getLogger(TestUtilsIT.class.getName());

    public static final Gson gson = new Gson();

    private static final ConfigReader configReader = new ConfigReader();

    public static ProjectApiRoot createApiClient() {
        return ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                                .withClientId(configReader.getClientId())
                                .withClientSecret(configReader.getClientSecret())
                                .build(),
                        ServiceRegion.GCP_EUROPE_WEST1)
                .build(configReader.getProjectKey());
    }

    public static <T> T readObjectFromResource(final String resourcePath, final Class<T> objectType) throws IOException {
        final InputStream resourceAsStream = new FileInputStream(resourcePath);
        return fromInputStream(resourceAsStream, objectType);
    }

    public static void deleteProductIfPresent(ProjectApiRoot apiClient, String productKey) {
        Optional<Product> product = getProductByKey(apiClient, productKey);
        if (product.isPresent()) {
            Product deletedProduct = apiClient.products().withKey(productKey).delete().withVersion(product.get().getVersion()).executeBlocking().getBody();
            logger.info("Product deleted:" + deletedProduct.getId());
        }
    }

    public static Optional<Product> getProductByKey(ProjectApiRoot apiClient, String key) {
        Optional<Product> product = Optional.empty();
        try {
            product = Optional.of(apiClient
                    .products()
                    .withKey(key)
                    .get()
                    .executeBlocking()
                    .getBody());
            logger.info("Product found: " + product.get().getId());
        } catch (NotFoundException e) {
            logger.info("Product " + key + " does not exist yet.");
        }
        return product;
    }
}
