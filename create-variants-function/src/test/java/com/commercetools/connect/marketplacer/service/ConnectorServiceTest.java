package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.models.product.Product;
import com.commercetools.connect.marketplacer.model.MarketplacerRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static com.commercetools.connect.marketplacer.utils.TestUtils.readObjectFromResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConnectorServiceTest {

    private final ClientService clientService = mock(ClientService.class);
    @InjectMocks
    private ConnectorService connectorService = new ConnectorService(clientService);

    @Test
    void shouldCreateProduct_WhenRequestParametersArePassed() throws Exception {
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerCreateRequest.json", MarketplacerRequest.class);

        when(clientService.executeCreateProduct(any())).thenReturn("connectTestProductId");
        when(clientService.getProductByKey(any())).thenReturn(Optional.empty());

        String response = connectorService.createVariants(request);

        assertEquals("{\"productId\":\"connectTestProductId\"}", response);
    }

    @Test
    void shouldUpdateProduct_WhenProductIsAlreadyAvailable() throws Exception {
        MarketplacerRequest request = readObjectFromResource("src/test/resources/marketplacerUpdateRequest.json", MarketplacerRequest.class);
        Product product = readObjectFromResource("src/test/resources/product.json", Product.class);

        when(clientService.executeUpdateProduct(any(), any())).thenReturn(product);
        when(clientService.getProductByKey(any())).thenReturn(Optional.ofNullable(product));

        String response = connectorService.createVariants(request);

        assertEquals("{\"updatedProduct\":\"connectTestProductUpdate1\"}", response);
    }
}
