package com.commercetools.connect.marketplacer.controller;

import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import com.commercetools.connect.marketplacer.service.CreateCustomObjectService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Logger;

@RestController
public class ConnectorController {

    private static final Logger logger = Logger.getLogger(ConnectorController.class.getName());
    private static final Gson gson = new Gson();
    final CreateCustomObjectService createCustomObjectService;

    @Autowired
    public ConnectorController(CreateCustomObjectService createCustomObjectService) {
        this.createCustomObjectService = createCustomObjectService;
    }

    @PostMapping("/")
    public ResponseEntity<String> createCustomObjects(@RequestBody String requestBody) {
        logger.info("Request : " + requestBody);
        MarketplacerRequest marketplacerRequest = gson.fromJson(requestBody, MarketplacerRequest.class);

        logger.info(gson.toJson(marketplacerRequest));
        if(null == marketplacerRequest.getPayload()) {
            return ResponseEntity.badRequest().body("Bad request parameters");
        }

        try {
            String response = createCustomObjectService.createCustomObjects(marketplacerRequest);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
