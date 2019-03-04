package io.ambrusadrianz.api.bookatable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutablePageFilter.class)
public interface PageFilter {
    @JsonProperty("pageFilterString")
    default String getPageFilterString() {
        return "Boundary:" + getBoundary() + "|"
                + "City:" + getCity() + "|"
                + "Large Area:" + getLargeArea() + "|"
                + "Country:" + getCountry();
    }

    @JsonIgnore
    String getBoundary();

    @JsonIgnore
    String getCity();

    @JsonIgnore
    String getLargeArea();

    @JsonIgnore
    String getCountry();

    @JsonProperty("selectedFilters")
    default List<Object> getSelectedFilters() {
        return Collections.emptyList();
    }
}
