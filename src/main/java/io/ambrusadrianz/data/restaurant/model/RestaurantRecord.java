package io.ambrusadrianz.data.restaurant.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableRestaurantRecord.class)
@JsonDeserialize(as = ImmutableRestaurantRecord.class)
public interface RestaurantRecord {
    String getId();

    String getName();

    UUID getCityId();

    Optional<DetailsRecord> getDetails();
}
