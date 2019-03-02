package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableTrade.class)
@JsonDeserialize(as = ImmutableTrade.class)
public interface Trade {
    @JsonProperty("id")
    Long getId();

    @JsonProperty("name")
    String getName();
}
