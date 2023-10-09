package com.commercetools.connect.marketplacer.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.vrap.rmf.base.client.utils.json.JsonUtils.fromInputStream;

public class TestUtils {
    public static <T> T readObjectFromResource(final String resourcePath, final Class<T> objectType) throws IOException {
        final InputStream resourceAsStream = new FileInputStream(resourcePath);
        return fromInputStream(resourceAsStream, objectType);
    }
}
