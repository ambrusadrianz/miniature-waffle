package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface RestaurantDetails {
    String getDescription();

    String getOpeningHours();

    String getContact();

    String getWebsiteUrl();

    Optional<Review> getReview();

    List<String> getNearbyRestaurants();

    List<String> getImageUrls();
}
