package io.ambrusadrianz.api.hitta.impl;


import io.ambrusadrianz.api.hitta.HittaClient;
import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.api.hitta.model.response.HittaResponse;
import io.reactivex.Single;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.WebClient;

public class HittaClientImpl implements HittaClient {

    private static final String SEARCH_PERSON_COMPANY = "/publicsearch/v1/";

    private final WebClient webClient;
    private final HittaHeaderFactory hittaHeaderFactory;

    public HittaClientImpl(WebClient webClient,
                           HittaHeaderFactory hittaHeaderFactory) {
        this.webClient = webClient;
        this.hittaHeaderFactory = hittaHeaderFactory;
    }

    @Override
    public Single<HittaResponse> search(SearchRequest searchRequest) {
        if (searchRequest.getSearchType() == null) {
            throw new IllegalArgumentException("SearchType must not be null");
        }

        var searchType = searchRequest.getSearchType().toString().toLowerCase();
        HttpRequest<Buffer> httpRequest = webClient.get(443, "api.hitta.se", SEARCH_PERSON_COMPANY + searchType);

        if (searchRequest.getWhat() != null) {
            httpRequest.addQueryParam("what", searchRequest.getWhat());
        }

        if (searchRequest.getWhere() != null) {
            httpRequest.addQueryParam("where", searchRequest.getWhere());
        }

        if (searchRequest.getGeoSystem() != null) {
            httpRequest.addQueryParam("geo.system", searchRequest.getGeoSystem().getValue());
        }

        if (searchRequest.getPageNumber() != null && searchRequest.getPageSize() != null) {
            httpRequest.addQueryParam("page.number", searchRequest.getPageNumber().toString());
            httpRequest.addQueryParam("page.size", searchRequest.getPageSize().toString());
        }

        httpRequest.headers().addAll(hittaHeaderFactory.buildHeaders());
        httpRequest.ssl(true);

        return httpRequest.rxSend()
                .map(response -> {
                    System.out.println(response.bodyAsJsonObject().getJsonObject("result").encodePrettily());
                    return response.bodyAsJsonObject().getJsonObject("result").mapTo(HittaResponse.class);
                });
    }
}
