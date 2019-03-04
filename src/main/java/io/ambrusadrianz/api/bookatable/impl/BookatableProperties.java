package io.ambrusadrianz.api.bookatable.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableBookatableProperties.class)
@JsonDeserialize(as = ImmutableBookatableProperties.class)
@Value.Immutable
public interface BookatableProperties {
    String getDataCulture();

    String getDataLanguage();
}
