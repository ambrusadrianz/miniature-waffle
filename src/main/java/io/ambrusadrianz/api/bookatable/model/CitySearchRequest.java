package io.ambrusadrianz.api.bookatable.model;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface CitySearchRequest {
    String getQuery();

    Optional<Integer> getLimit();

    Optional<String> getDataCulture();

    Optional<String> getDataLanguage();
}
