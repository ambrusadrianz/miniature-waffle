package io.ambrusadrianz.api.city.impl;

import io.ambrusadrianz.api.city.CityClient;
import io.ambrusadrianz.api.city.impl.assembler.CityDocumentAssembler;
import io.ambrusadrianz.api.city.model.City;
import io.reactivex.Flowable;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.jsoup.Jsoup;

public class CityClientImpl implements CityClient {

    private final WebClient webClient;
    private final CityDocumentAssembler cityDocumentAssembler;

    public CityClientImpl(WebClient webClient, CityDocumentAssembler cityDocumentAssembler) {
        this.webClient = webClient;
        this.cityDocumentAssembler = cityDocumentAssembler;
    }

    @Override
    public Flowable<City> fetchCities() {
        return webClient.getAbs("https://en.wikipedia.org/wiki/List_of_urban_areas_in_Sweden_by_population")
                .ssl(true)
                .rxSend()
                .map(respone -> Jsoup.parse(respone.bodyAsString()))
                .flattenAsFlowable(cityDocumentAssembler::documentToCities);
    }
}
