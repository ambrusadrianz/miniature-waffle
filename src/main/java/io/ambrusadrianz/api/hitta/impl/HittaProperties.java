package io.ambrusadrianz.api.hitta.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableHittaProperties.class)
@JsonDeserialize(as = ImmutableHittaProperties.class)
@Value.Immutable
public interface HittaProperties {
    String getCallerId();

    String getApiKey();
}
