package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableAttribute.class)
@JsonDeserialize(as = ImmutableAttribute.class)
public interface Attribute {
    @JsonProperty("name")
    String getName();

    @JsonProperty("value")
    String getValue();
}
