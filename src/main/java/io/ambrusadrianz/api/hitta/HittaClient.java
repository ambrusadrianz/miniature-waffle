package io.ambrusadrianz.api.hitta;

import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.api.hitta.model.response.Company;
import io.reactivex.Flowable;

public interface HittaClient {
    Flowable<Company> search(SearchRequest searchRequest);
}
