package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableBoxAddress.class)
@JsonDeserialize(as = ImmutableBoxAddress.class)
public interface BoxAddress extends Address {

    default String getType() {
        return "boxAddress";
    }

    @JsonProperty("usageCode")
    String getUsageCode();

    @JsonProperty("isWorkAddress")
    Boolean getIsWorkAddress();

    @JsonProperty("city")
    String getCity();

    @JsonProperty("cityPreposition")
    String getCityPreposition();

    @JsonProperty("zipcode")
    Long getZipcode();

    @Nullable
    @JsonProperty("box")
    Long getBox();
}
