package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.models.custom_object.CustomObject;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static com.commercetools.connect.marketplacer.utils.TestUtils.readObjectFromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectorServiceTest {
    private final ClientService clientService = mock(ClientService.class);
    @InjectMocks
    private CreateCustomObjectService createCustomObjectService = new CreateCustomObjectService(clientService);

    @Test
    void shouldCreateCustomObject_WhenRequestParametersArePassed() throws Exception {
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerSellerCreateRequest.json", MarketplacerRequest.class);
        CustomObject customObject = readObjectFromResource("src/test/resources/customObject.json", CustomObject.class);

        when(clientService.executeCreateCustomObject(any())).thenReturn(customObject);

        String response = createCustomObjectService.createCustomObjects(request);

        assertEquals("{\"Seller\":\"connectTestProductUpdate1\"}", response);
    }

    @Test
    void shouldThrowException_WhenUnexpectedErrorOccurred() throws Exception {
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerSellerCreateRequest.json", MarketplacerRequest.class);

        request.getPayload().getData().setNode(null);

        Assertions.assertThrows(Exception.class, () -> createCustomObjectService.createCustomObjects(request));
    }
}
