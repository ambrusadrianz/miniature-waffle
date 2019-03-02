package io.ambrusadrianz.api.hitta;

import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.api.hitta.model.response.HittaResponse;
import io.reactivex.Single;

public interface HittaClient {
    Single<HittaResponse> search(SearchRequest searchRequest);
}
