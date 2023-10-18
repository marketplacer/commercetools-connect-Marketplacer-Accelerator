package com.commercetools.connect.marketplacer.controller;

import com.commercetools.connect.marketplacer.service.ConnectorService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectorControllerTest {
    private ConnectorService connectorService = mock(ConnectorService.class);
    @InjectMocks
    private ConnectorController connectorController = new ConnectorController(connectorService);

    @Test
    void shouldReturnStatus400Error_WhenBadRequestParametersArePassed() {
        ResponseEntity<String> response = connectorController.createVariants("{}");

        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Bad request parameters", response.getBody());
    }

    @Test
    void shouldReturnStatus200_WhenRequestParametersArePassed() throws Exception {
        when(connectorService.createVariants(any())).thenReturn("success");
        ResponseEntity<String> response = connectorController.createVariants("{\"id\":\"V2ViaG9va0V2ZW50LTE51\",\"event_name\":\"create\",\"payload\":{\"data\": {}}}");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals("success", response.getBody());
    }

    @Test
    void shouldReturnStatus500_WhenUnexpectedErrorOccurs() throws Exception {
        when(connectorService.createVariants(any())).thenThrow(new Exception("UnexpectedError occurred"));
        ResponseEntity<String> response = connectorController.createVariants("{\"id\":\"V2ViaG9va0V2ZW50LTE51\",\"event_name\":\"create\",\"payload\":{\"data\": {}}}");

        assertTrue(response.getStatusCode().is5xxServerError());
        assertEquals("create variants function error: UnexpectedError occurred", response.getBody());
    }
}
