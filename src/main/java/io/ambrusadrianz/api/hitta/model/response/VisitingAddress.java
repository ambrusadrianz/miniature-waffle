package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableVisitingAddress.class)
@JsonDeserialize(as = ImmutableVisitingAddress.class)
public interface VisitingAddress extends Address {

    default String getType() {
        return "visitingAddress";
    }

    @JsonProperty("city")
    String getCity();

    @JsonProperty("street")
    String getStreet();

    @Nullable
    @JsonProperty("number")
    String getNumber();

    @Nullable
    @JsonProperty("entrance")
    String getEntrance();

    @Nullable
    @JsonProperty("district")
    String getDistrict();

    @Nullable
    @JsonProperty("parish")
    String getParish();

    @Nullable
    @JsonProperty("community")
    String getCommunity();

    @Nullable
    @JsonProperty("county")
    String getCounty();

    @Nullable
    @JsonProperty("coordinate")
    Coordinate getCoordinate();

    @JsonProperty("streetview")
    List<StreetView> getStreetView();

    @JsonProperty("phone")
    List<Phone> getPhone();

    @JsonProperty("usageCode")
    String getUsageCode();

    @JsonProperty("isWorkAddress")
    Boolean getIsWorkAddress();

    @JsonProperty("cityPreposition")
    String getCityPreposition();

    @Nullable
    @JsonProperty("routeCoordinate")
    RouteCoordinate getRouteCoordinate();
}
