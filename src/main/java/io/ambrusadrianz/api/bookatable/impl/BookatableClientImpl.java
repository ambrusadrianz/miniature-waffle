package io.ambrusadrianz.api.bookatable.impl;

import io.ambrusadrianz.api.bookatable.BookatableClient;
import io.ambrusadrianz.api.bookatable.exception.RestaurantNotFoundException;
import io.ambrusadrianz.api.bookatable.impl.assembler.BookatableDocumentAssembler;
import io.ambrusadrianz.api.bookatable.model.City;
import io.ambrusadrianz.api.bookatable.model.CitySearchRequest;
import io.ambrusadrianz.api.bookatable.model.ImmutablePageFilter;
import io.ambrusadrianz.api.bookatable.model.ImmutableRestaurant;
import io.ambrusadrianz.api.bookatable.model.PageFilter;
import io.ambrusadrianz.api.bookatable.model.Restaurant;
import io.ambrusadrianz.api.bookatable.model.RestaurantDetails;
import io.ambrusadrianz.api.bookatable.model.RestaurantListingRequest;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.jsoup.Jsoup;

import java.time.Instant;
import java.util.concurrent.CancellationException;

public class BookatableClientImpl implements BookatableClient {
    private static final Logger log = LoggerFactory.getLogger(BookatableClientImpl.class);

    private static final String BOOKATABLE_SEARCH_SUGGESTION_ENDPOINT = "https://prod-batv2-milenasearch.cl.bookatable.com/Suggestion";
    private static final String BOOKATABLE_WEBSITE_URL = "https://www.bookatable.co.uk/";
    private static final String BOOKATABLE_WEBSITE_RESTAURANT_PAGINATION = BOOKATABLE_WEBSITE_URL + "Content/FilterRestaurants";

    private final WebClient webClient;
    private final BookatableProperties bookatableProperties;
    private final BookatableDocumentAssembler bookatableDocumentAssembler;

    public BookatableClientImpl(WebClient webClient,
                                BookatableProperties bookatableProperties,
                                BookatableDocumentAssembler bookatableDocumentAssembler) {
        this.webClient = webClient;
        this.bookatableProperties = bookatableProperties;
        this.bookatableDocumentAssembler = bookatableDocumentAssembler;
    }

    @Override
    public Flowable<City> searchCities(CitySearchRequest searchRequest) {
        var limit = searchRequest.getLimit().orElse(10);
        var dataCulture = searchRequest.getDataCulture().orElse(bookatableProperties.getDataCulture());
        var dataLanguage = searchRequest.getDataLanguage().orElse(bookatableProperties.getDataLanguage());

        Single<HttpResponse<Buffer>> responseSingle = webClient.getAbs(BOOKATABLE_SEARCH_SUGGESTION_ENDPOINT)
                .addQueryParam("q", searchRequest.getQuery())
                .addQueryParam("s", Integer.toString(limit))
                .addQueryParam("c", dataCulture)
                .addQueryParam("l", dataLanguage)
                .addQueryParam("id", "random-" + Instant.now().getEpochSecond())
                .ssl(true)
                .rxSend();

        return responseSingle
                .flattenAsFlowable(bufferHttpResponse -> () -> bufferHttpResponse.bodyAsJsonObject().getJsonArray("results").iterator())
                .map(JsonObject::mapFrom)
                .map(json -> json.mapTo(IntermediateCity.class))
                .flatMapSingle(this::fetchCity);
    }

    @Override
    public Flowable<Restaurant> fetchRestaurants(RestaurantListingRequest listingRequest) {
        var pageFilter = ImmutablePageFilter.builder()
                .boundary(listingRequest.getCity())
                .city(listingRequest.getCity())
                .largeArea(listingRequest.getCity())
                .country(listingRequest.getCountry())
                .build();

        return Flowable
                .generate(() -> 0, (BiFunction<Integer, Emitter<Integer>, Integer>) (integer, emitter) -> {
                    emitter.onNext(integer);
                    return integer + 15;
                })
                .concatMap(startFrom -> fetchRestaurants(startFrom, pageFilter))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof CancellationException) {
                        return Flowable.empty();
                    }

                    return Flowable.error(throwable);
                });
    }

    @Override
    public Flowable<Restaurant> fetchRestaurantsDetailed(RestaurantListingRequest listingRequest) {
        return fetchRestaurants(listingRequest)
                .flatMapSingle(r -> fetchRestaurantDetails(r.getId())
                        .map(details -> ImmutableRestaurant.copyOf(r).withDetails(details)));
    }

    private Single<City> fetchCity(IntermediateCity intermediateCity) {
        return webClient.getAbs(BOOKATABLE_WEBSITE_URL + intermediateCity.getUrl())
                .ssl(true)
                .rxSend()
                .map(bufferHttpResponse -> Jsoup.parse(bufferHttpResponse.bodyAsString()))
                .map(document -> bookatableDocumentAssembler.documentToCity(intermediateCity, document));
    }

    private Single<RestaurantDetails> fetchRestaurantDetails(String restaurantId) {
        return webClient.getAbs(BOOKATABLE_WEBSITE_URL + restaurantId)
                .ssl(true)
                .rxSend()
                .map(bufferHttpResponse -> Jsoup.parse(bufferHttpResponse.bodyAsString()))
                .flatMap(document -> {
                    RestaurantDetails restaurantDetails = bookatableDocumentAssembler.documentToRestaurantDetails(document);

                    return restaurantDetails == null
                            ? Single.error(() -> new RestaurantNotFoundException("Restaurant not found with id=" + restaurantId))
                            : Single.just(restaurantDetails);
                });
    }

    private Flowable<Restaurant> fetchRestaurants(int startFrom, PageFilter pageFilter) {
        return webClient
                .postAbs(BOOKATABLE_WEBSITE_RESTAURANT_PAGINATION)
                .ssl(true)
                .addQueryParam("startfrom", Integer.toString(startFrom))
                .rxSendJson(pageFilter)
                .map(htmlBody -> Jsoup.parse(htmlBody.bodyAsString()))
                .map(document -> bookatableDocumentAssembler.documentToRestaurants(pageFilter.getCity(), document))
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        return Single.error(CancellationException::new);
                    } else {
                        return Single.just(list);
                    }
                })
                .flattenAsFlowable(list -> list);
    }
}
