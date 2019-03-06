package io.ambrusadrianz.data.restaurant.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableDetailsRecord.class)
@JsonDeserialize(as = ImmutableDetailsRecord.class)
public interface DetailsRecord {
    CoordinateRecord getLocation();

    String getOpeningHours();

    String getCuisine();

    Optional<String> getAddress();

    Optional<Integer> getPostalCode();

    String getContact();

    String getWebsiteUrl();

    String getDescription();

    Optional<ReviewRecord> getReview();

    List<String> getNearbyRestaurants();

    List<String> getImageUrls();
}
