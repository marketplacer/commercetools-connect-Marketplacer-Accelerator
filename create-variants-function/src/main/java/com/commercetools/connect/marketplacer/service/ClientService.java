package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductDraft;
import com.commercetools.api.models.product.ProductUpdate;
import com.commercetools.connect.marketplacer.utils.ConfigReader;
import io.vrap.rmf.base.client.error.NotFoundException;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ClientService {

    private static final Logger logger = Logger.getLogger(ClientService.class.getName());

    private static final ConfigReader configReader = new ConfigReader();

    private ProjectApiRoot createApiClient() {
        return ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                                .withClientId(configReader.getClientId())
                                .withClientSecret(configReader.getClientSecret())
                                .build(),
                        getRegion(configReader.getRegion()))
                .build(configReader.getProjectKey());
    }

    protected ServiceRegion getRegion(String region) {
        if (null == region) {
            return ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1;
        }

        if (region.toLowerCase().contains("europe")) {
            return ServiceRegion.GCP_EUROPE_WEST1;
        } else if(region.contains("us")) {
            return ServiceRegion.GCP_US_CENTRAL1;
        } else {
            return ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1;
        }
    }

    private final ProjectApiRoot apiRoot = createApiClient();

    public String executeCreateProduct(ProductDraft newProductDetails) {
        final Product product = apiRoot
                .products()
                .post(newProductDetails)
                .executeBlocking()
                .getBody();

        return product.getId();
    }

    public Optional<Product> getProductByKey(String key) {
        Optional<Product> product = Optional.empty();
        try {
            product = Optional.of(apiRoot
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

    public Product executeUpdateProduct(Product productToUpdate, ProductUpdate productUpdate) {
        return apiRoot
                .products()
                .withId(productToUpdate.getId())
                .post(productUpdate)
                .executeBlocking()
                .getBody();
    }


}
