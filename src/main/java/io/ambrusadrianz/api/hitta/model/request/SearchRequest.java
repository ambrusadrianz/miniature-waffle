package io.ambrusadrianz.api.hitta.model.request;

import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
public interface SearchRequest {
    @Nullable
    String getWhat();

    @Nullable
    String getWhere();

    @Nullable
    GeoSystem getGeoSystem();
}
