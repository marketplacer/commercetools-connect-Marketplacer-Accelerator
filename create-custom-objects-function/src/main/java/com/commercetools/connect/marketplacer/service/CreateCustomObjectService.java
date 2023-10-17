package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.api.models.custom_object.CustomObjectDraft;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class CreateCustomObjectService {
    private static final Logger logger = Logger.getLogger(CreateCustomObjectService.class.getName());

    private final ClientService clientService;

    @Autowired
    CreateCustomObjectService(ClientService clientService) {
        this.clientService = clientService;
    }

    public String createCustomObjects(MarketplacerRequest marketplacerRequest) throws Exception {
        JsonObject jsonResponse = new JsonObject();
        try {
            CustomObject seller = createCustomObject(marketplacerRequest);
            jsonResponse.addProperty(marketplacerRequest.getPayload().getData().getNode().getTypename(), seller.getId());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            jsonResponse = new JsonObject();
            jsonResponse.addProperty("stackTrace", stacktrace);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new Exception(jsonResponse.toString());
        }
        return jsonResponse.toString();
    }

    public CustomObject createCustomObject(MarketplacerRequest request) {
        CustomObjectDraft customObjectDraft = CustomObjectDraft.builder()
                .container(request.getPayload().getData().getNode().getTypename())
                .key(request.getPayload().getData().getNode().getId().replace("=", ""))
                .value(request.getPayload().getData().getNode())
                .build();
        return clientService.executeCreateCustomObject(customObjectDraft);
    }

}

