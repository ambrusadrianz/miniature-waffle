package io.ambrusadrianz;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.ambrusadrianz.application.ApplicationProperties;
import io.ambrusadrianz.application.ApplicationPropertiesFactory;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        WebClient webClient = WebClient.create(vertx, new WebClientOptions().setKeepAlive(true));

        YAMLMapper yamlMapper = new YAMLMapper();
        ApplicationPropertiesFactory applicationPropertiesFactory = new ApplicationPropertiesFactory(yamlMapper);
        ApplicationProperties properties = applicationPropertiesFactory.getApplicationProperties();

//        HittaHeaderFactory hittaHeaderFactory = new HittaHeaderFactory(properties.getHitta());
//        HittaClientImpl hittaClient = new HittaClientImpl(webClient, hittaHeaderFactory);
//
//        SearchRequest searchRequest = ImmutableSearchRequest.builder()
//                .searchType(SearchType.COMPANIES)
//                .what("Voyage")
//                .where("Nyköping")
//                .pageNumber(1L)
//                .pageSize(10)
//                .build();
//        hittaClient.search(searchRequest).subscribe(System.out::println);

//        BookatableClientImpl bookatableClient = new BookatableClientImpl(webClient, properties.getBookatable());
//
//        Flowable.fromIterable(properties.getCitiesToScan())
//                .map(city -> ImmutableCitySearchRequest.builder().limit(1).query(city).build())
//                .flatMap(bookatableClient::searchCities)
//                .map(response -> ImmutableRestaurantListingRequest.builder()
//                        .city(response.getName())
//                        .country(response.getCountry())
//                        .build())
//                .concatMap(bookatableClient::fetchRestaurantsDetailed)
//                .limit(1000)
//                .forEach(System.out::println);
    }
}
