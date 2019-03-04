package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface Review {
    Double getRating();

    Long getReviewCount();

    Map<String, Double> getRatings();

    Double getRecommendationRate();
}
