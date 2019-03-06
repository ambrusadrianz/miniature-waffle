package io.ambrusadrianz.data.restaurant.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonSerialize(as = ImmutableReviewRecord.class)
@JsonDeserialize(as = ImmutableReviewRecord.class)
public interface ReviewRecord {
    Double getRating();

    Long getReviewCount();

    Map<String, Double> getRatings();

    Double getRecommendationRate();
}
