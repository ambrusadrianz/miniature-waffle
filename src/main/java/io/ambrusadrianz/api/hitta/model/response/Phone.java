package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutablePhone.class)
@JsonDeserialize(as = ImmutablePhone.class)
public interface Phone {
    @JsonProperty("label")
    String getLabel();

    @JsonProperty("callTo")
    String getCallTo();

    @JsonProperty("displayAs")
    String getDisplayAs();
}
