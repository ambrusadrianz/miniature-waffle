package io.ambrusadrianz.api.hitta.model.request;

import javax.annotation.Nullable;

public interface PaginatedHittaRequest {
    @Nullable
    Long getPageNumber();

    @Nullable
    Integer getPageSize();

    @Nullable
    String getRangeFrom();

    @Nullable
    String getRangeTo();
}
