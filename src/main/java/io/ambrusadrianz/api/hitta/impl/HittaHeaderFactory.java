package io.ambrusadrianz.api.hitta.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.Instant;
import java.util.Map;

public class HittaHeaderFactory {

    private static final String HITTA_CALLER_ID_HEADER = "X-Hitta-CallerId";
    private static final String HITTA_TIME_HEADER = "X-Hitta-Time";
    private static final String HITTA_RANDOM_HEADER = "X-Hitta-Random";
    private static final String HITTA_HASH_HEADER = "X-Hitta-Hash";

    private final HittaProperties hittaProperties;

    public HittaHeaderFactory(HittaProperties hittaProperties) {
        this.hittaProperties = hittaProperties;
    }

    public Map<String, String> buildHeaders() {
        return buildHeaders(Instant.now().getEpochSecond());
    }

    public Map<String, String> buildHeaders(Long unixTimestamp) {
        if (unixTimestamp == null) {
            throw new IllegalArgumentException("Provided UNIX timestamp must not be null");
        }

        var randomString = RandomStringUtils.randomAlphanumeric(16);
        var hittaHash = getHittaHash(hittaProperties.getCallerId(),
                hittaProperties.getApiKey(),
                unixTimestamp,
                randomString);

        return Map.of(HITTA_CALLER_ID_HEADER, hittaProperties.getCallerId(),
                HITTA_TIME_HEADER, unixTimestamp.toString(),
                HITTA_RANDOM_HEADER, randomString,
                HITTA_HASH_HEADER, hittaHash);
    }

    private String getHittaHash(String callerId,
                                String apiKey,
                                Long unixTimestamp,
                                String randomString) {
        return DigestUtils.sha1Hex(callerId + unixTimestamp.toString() + apiKey + randomString);
    }
}
