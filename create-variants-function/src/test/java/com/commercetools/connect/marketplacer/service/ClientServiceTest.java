package com.commercetools.connect.marketplacer.service;

import com.commercetools.api.defaultconfig.ServiceRegion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientServiceTest {

    private final ClientService clientService = new ClientService();
    @Test
    void shouldReturnServiceRegionEurope_WhenRegionIsEurope() {
        ServiceRegion serviceRegion = clientService.getRegion("europe");

        assertEquals("GCP_EUROPE_WEST1", serviceRegion.name());
    }

    @Test
    void shouldReturnServiceRegionAustralia_WhenRegionIsAsia() {
        ServiceRegion serviceRegion = clientService.getRegion("asia");

        assertEquals("GCP_AUSTRALIA_SOUTHEAST1", serviceRegion.name());
    }

    @Test
    void shouldReturnServiceRegionAustralia_WhenRegionIsNull() {
        ServiceRegion serviceRegion = clientService.getRegion(null);

        assertEquals("GCP_AUSTRALIA_SOUTHEAST1", serviceRegion.name());
    }

    @Test
    void shouldReturnServiceRegionUS_WhenRegionIsUS() {
        ServiceRegion serviceRegion = clientService.getRegion("us");

        assertEquals("GCP_US_CENTRAL1", serviceRegion.name());
    }
}
