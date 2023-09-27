package com.commercetools.connect.marketplacer;

import com.commercetools.connect.marketplacer.controller.ConnectorController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ConnectorApplicationTest {

    @Autowired
    private ConnectorController connectorController;

    @Test
    void contextLoads() {
        assertThat(connectorController).isNotNull();
    }
}
