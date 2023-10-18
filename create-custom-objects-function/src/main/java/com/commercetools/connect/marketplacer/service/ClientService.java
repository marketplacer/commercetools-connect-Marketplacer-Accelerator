package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.commercetools.connect.marketplacer.utils.ConfigReader;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
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

    public CustomObject executeCreateCustomObject(CustomObjectDraft customObjectDraft) {
        return apiRoot.customObjects().post(customObjectDraft).executeBlocking().getBody();
    }
}