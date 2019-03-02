package io.ambrusadrianz;

import io.ambrusadrianz.api.hitta.impl.HittaClientImpl;
import io.ambrusadrianz.api.hitta.impl.HittaHeaderFactory;
import io.ambrusadrianz.api.hitta.impl.HittaProperties;
import io.ambrusadrianz.api.hitta.model.request.ImmutableSearchRequest;
import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.api.hitta.model.request.SearchType;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        WebClient webClient = WebClient.create(vertx);

        var hittaProperties = new HittaProperties() {
            @Override
            public String getCallerId() {
                return "ambrusadrianz";
            }

            @Override
            public String getApiKey() {
                return "p5MEZUjJJrAPNKGYUBxz9Wk1uAwGSCBBFInKzlIE";
            }
        };

        HittaHeaderFactory hittaHeaderFactory = new HittaHeaderFactory(hittaProperties);
        HittaClientImpl hittaClient = new HittaClientImpl(webClient, hittaHeaderFactory);

        SearchRequest searchRequest = ImmutableSearchRequest.builder()
                .searchType(SearchType.COMPANIES)
                .what("Voyage")
                .where("Nyköping")
                .pageNumber(1L)
                .pageSize(10)
                .build();
        hittaClient.search(searchRequest)
                .subscribe(response -> {
                    System.out.println(response);
                });
    }
}
