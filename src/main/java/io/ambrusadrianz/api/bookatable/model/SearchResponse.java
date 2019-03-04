package io.ambrusadrianz.api.bookatable.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableSearchResponse.class)
@JsonDeserialize(as = ImmutableSearchResponse.class)
public interface SearchResponse {
    @Nullable
    @JsonProperty("id")
    String getId();

    @JsonProperty("results")
    List<City> getResults();
}
