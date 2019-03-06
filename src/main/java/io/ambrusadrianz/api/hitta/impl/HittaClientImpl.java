package io.ambrusadrianz.api.hitta.impl;


import io.ambrusadrianz.api.hitta.HittaClient;
import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.api.hitta.model.request.SearchType;
import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.api.hitta.model.response.CompanyResponse;
import io.reactivex.Flowable;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.WebClient;

public class HittaClientImpl implements HittaClient {

    private static final String SEARCH_PERSON_COMPANY = "/publicsearch/v1/";
    private static final Integer PAGE_SIZE = 500;


    private final WebClient webClient;
    private final HittaHeaderFactory hittaHeaderFactory;

    public HittaClientImpl(WebClient webClient,
                           HittaHeaderFactory hittaHeaderFactory) {
        this.webClient = webClient;
        this.hittaHeaderFactory = hittaHeaderFactory;
    }

    @Override
    public Flowable<Company> search(SearchRequest searchRequest) {
        var searchType = SearchType.COMPANIES.toString().toLowerCase();
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

        httpRequest.addQueryParam("page.number", "1");
        httpRequest.addQueryParam("page.size", PAGE_SIZE.toString());

        httpRequest.headers().addAll(hittaHeaderFactory.buildHeaders());
        httpRequest.ssl(true);

        return httpRequest.rxSend()
                .map(response -> response.bodyAsJsonObject().getJsonObject("result").getJsonObject("companies").mapTo(CompanyResponse.class))
                .flatMapPublisher(companyResponse -> Flowable.fromIterable(companyResponse.getCompany()));
    }
}
