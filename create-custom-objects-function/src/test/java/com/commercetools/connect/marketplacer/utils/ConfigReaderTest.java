package com.commercetools.connect.marketplacer.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigReaderTest {
    private final ConfigReader configReader = new ConfigReader();
    @Test
    void shouldReturnConfigValues_WhenConfigFileIsAvailable() {
        assertEquals("clientId", configReader.getClientId());
        assertEquals("clientSecret", configReader.getClientSecret());
        assertEquals("projectKey", configReader.getProjectKey());
    }
}
