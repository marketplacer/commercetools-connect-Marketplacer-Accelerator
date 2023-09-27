package com.commercetools.connect.marketplacer.utils;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product.Product;
import com.commercetools.connect.marketplacer.service.ConnectorService;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

import static io.vrap.rmf.base.client.utils.json.JsonUtils.fromInputStream;

public class TestUtils {

    private static final Logger logger = Logger.getLogger(TestUtils.class.getName());

    public static final Gson gson = new Gson();

    public static <T> T readObjectFromResource(final String resourcePath, final Class<T> objectType) throws IOException {
        final InputStream resourceAsStream = new FileInputStream(resourcePath);
        return fromInputStream(resourceAsStream, objectType);
    }

    public static void deleteProductIfPresent(ProjectApiRoot apiClient, String productKey) {
        Optional<Product> product = ConnectorService.getProductByKey(productKey);
        if (product.isPresent()) {
            Product deletedProduct = apiClient.products().withKey(productKey).delete().withVersion(product.get().getVersion()).executeBlocking().getBody();
            logger.info("Product deleted:" + deletedProduct.getId());
        }
    }
}
