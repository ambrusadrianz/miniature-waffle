package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface RestaurantDetailed {
    String getId();

    String getName();

    String getDescription();

    String getOpeningHours();

    String getCuisine();

    String getAddress();

    Coordinate getLocation();

    String getContact();

    String getWebsiteUrl();

    Optional<Review> getReview();

    List<String> getNearbyRestaurants();

    List<String> getImageUrls();
}
