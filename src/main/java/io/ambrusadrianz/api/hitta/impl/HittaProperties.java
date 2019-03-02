package io.ambrusadrianz.api.hitta.impl;

import org.immutables.value.Value;

@Value.Immutable
public interface HittaProperties {
    String getCallerId();

    String getApiKey();
}
