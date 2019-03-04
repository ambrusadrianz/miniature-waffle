package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

@Value.Immutable
public interface Restaurant {
    String getId();

    String getName();

    String getCuisine();

    String getAddress();

    Double getRating();

    Long getReviewCount();

    Coordinate getLocation();

    String getImageUrl();
}
