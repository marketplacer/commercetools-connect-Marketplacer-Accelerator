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

    public ProjectApiRoot createApiClient() {
        return ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                                .withClientId(configReader.getClientId())
                                .withClientSecret(configReader.getClientSecret())
                                .build(),
                        ServiceRegion.GCP_EUROPE_WEST1)
                .build(configReader.getProjectKey());
    }

    private final ProjectApiRoot apiRoot = createApiClient();

    public CustomObject executeCreateCustomObject(CustomObjectDraft customObjectDraft) {
        return apiRoot.customObjects().post(customObjectDraft).executeBlocking().getBody();
    }
}