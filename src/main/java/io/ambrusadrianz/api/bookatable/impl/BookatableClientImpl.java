package io.ambrusadrianz.api.bookatable.impl;

import io.ambrusadrianz.api.bookatable.BookatableClient;
import io.ambrusadrianz.api.bookatable.exception.RestaurantNotFoundException;
import io.ambrusadrianz.api.bookatable.model.*;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.BiFunction;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;

public class BookatableClientImpl implements BookatableClient {
    private static final String SEARCH_SUGGESTION_ENDPOINT = "https://prod-batv2-milenasearch.cl.bookatable.com/Suggestion";

    private final WebClient webClient;
    private final BookatableProperties bookatableProperties;

    public BookatableClientImpl(WebClient webClient,
                                BookatableProperties bookatableProperties) {
        this.webClient = webClient;
        this.bookatableProperties = bookatableProperties;
    }

    @Override
    public Flowable<City> searchCities(CitySearchRequest searchRequest) {
        var limit = searchRequest.getLimit().orElse(10);
        var dataCulture = searchRequest.getDataCulture().orElse(bookatableProperties.getDataCulture());
        var dataLanguage = searchRequest.getDataLanguage().orElse(bookatableProperties.getDataLanguage());

        Single<HttpResponse<Buffer>> responseSingle = webClient.getAbs(SEARCH_SUGGESTION_ENDPOINT)
                .addQueryParam("q", searchRequest.getQuery())
                .addQueryParam("s", Integer.toString(limit))
                .addQueryParam("c", dataCulture)
                .addQueryParam("l", dataLanguage)
                .addQueryParam("id", "random-" + Instant.now().getEpochSecond())
                .ssl(true)
                .rxSend();

        return responseSingle
                .flattenAsFlowable(bufferHttpResponse ->
                        () -> bufferHttpResponse.bodyAsJsonObject().getJsonArray("results").iterator())
                .map(JsonObject::mapFrom)
                .map(json -> json.mapTo(City.class));
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
    public Flowable<RestaurantDetailed> fetchRestaurantsDetailed(RestaurantListingRequest listingRequest) {
        return fetchRestaurants(listingRequest)
                .flatMapSingle(this::fetchRestaurantDetailed);
    }

    private Single<RestaurantDetailed> fetchRestaurantDetailed(Restaurant restaurant) {
        return webClient.getAbs("https://www.bookatable.co.uk/" + restaurant.getId())
                .ssl(true)
                .rxSend()
                .map(bufferHttpResponse -> Jsoup.parse(bufferHttpResponse.bodyAsString()))
                .flatMap(document -> {
                    Element element = document.select(".content-wrapper").first();

                    if (element == null) {
                        return Single.error(() -> new RestaurantNotFoundException("Restaurant not found with id=" + restaurant.getId()));
                    } else {
                        return Single.just(element);
                    }
                })
                .map(element -> elementToRestaurantDetailed(restaurant.getId(), element));
    }

    private Flowable<Restaurant> fetchRestaurants(int startFrom, PageFilter pageFilter) {
        return webClient
                .postAbs("https://www.bookatable.co.uk/Content/FilterRestaurants")
                .ssl(true)
                .addQueryParam("startfrom", Integer.toString(startFrom))
                .rxSendJson(pageFilter)
                .map(htmlBody -> Jsoup.parse(htmlBody.bodyAsString()))
                .flatMap(document -> {
                    Element element = document.getElementsByTag("ul").first();

                    if (element == null) {
                        return Single.error(CancellationException::new);
                    }

                    return Single.just(element);
                })
                .map(element -> element.getElementsByTag("li"))
                .flattenAsFlowable(elements -> elements)
                .map(this::elementToRestaurant);
    }

    private Restaurant elementToRestaurant(Element element) {
        var id = element.select(".block-link").attr("href");
        var name = element.select(".item > .detail > .fn").text();
        var latitudeLongitude = element.select(".item").attr("data-latlng");
        var address = element.select(".item > .detail > .adr").text();
        var rating = textToDouble(element.select(".item > .detail > .rating").attr("data-value"));
        var reviewCount = Long.valueOf(element.select(".item > .detail > .rating").attr("data-count"));
        var cuisine = element.select(".item > .detail > .cuisine").html();
        var imageUrl = element.select("noscript > img").attr("src"); // TODO improve this, by replacing to https and stripping cropping info

        return ImmutableRestaurant.builder()
                .id(id)
                .name(name)
                .address(address)
                .rating(rating)
                .reviewCount(reviewCount)
                .cuisine(cuisine)
                .imageUrl(imageUrl)
                .location(textCoordinateToCoordinate(latitudeLongitude))
                .build();
    }

    private RestaurantDetailed elementToRestaurantDetailed(String restaurantId, Element element) {
        var name = element.select(".primary > #hero > .restaurant-details > h1").text();
        var description = element.select("#panel-overview > div:nth-child(6) > div > p").text();
        var openingHours = element.select("#panel-overview > div.col-2 > dl > dd:nth-child(5)").text();
        var cuisine = element.select("#hero > div > div > span.cuisine").text();
        var address = element.select("#panel-location > p.adr").text();
        var latitudeLongitude = element.select("#map-static").attr("data-markers");
        var contact = element.select("#panel-location > table > tbody > tr > td > table > tbody > tr:nth-child(1) > td > a").text();
        var websiteUrl = element.select("#panel-location > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > a").attr("href");
        var review = elementToReview(element.select("#panel-reviews > div.aggregate-review").first());
        var nearbyRestaurants = elementToNearbyRestaurants(element.select("div.secondary > div.restaurant-list.nearby").first());
        var imageUrls = elementToImageUrls(element.select("#slider").first());

        return ImmutableRestaurantDetailed.builder()
                .id(restaurantId)
                .name(name)
                .description(description)
                .openingHours(openingHours)
                .cuisine(cuisine)
                .address(address)
                .location(textCoordinateToCoordinate(latitudeLongitude))
                .contact(contact)
                .websiteUrl(websiteUrl)
                .review(Optional.ofNullable(review))
                .nearbyRestaurants(nearbyRestaurants)
                .imageUrls(imageUrls)
                .build();
    }

    private Review elementToReview(Element element) {
        if (element == null) {
            return null;
        }

        var rating = textToDouble(element.select("div.aggregate-top > div.col-1 > span.rating-number").text());
        var reviewCount = Long.valueOf(element.select("div.aggregate-top > div.col-1 > span.review-count > span").text());
        var recommendationRate = textPercentToDouble(element.select("div.aggregate-top > div.col-2 > span > span").text());
        var ratingList = element.select("div.aggregate-bottom > div.col-1.feature-list > ul").first();

        Map<String, Double> ratings = new HashMap<>();
        for (Element e : ratingList.getElementsByTag("li")) {
            var category = e.getElementsByTag("span").first().text().toLowerCase();
            var categoryRating = getRating(e.getElementsByClass("rating").first().attr("title"));

            ratings.put(category, categoryRating);
        }

        return ImmutableReview.builder()
                .rating(rating)
                .reviewCount(reviewCount)
                .ratings(ratings)
                .recommendationRate(recommendationRate)
                .build();
    }

    private List<String> elementToNearbyRestaurants(Element element) {
        if (element == null) {
            return Collections.emptyList();
        }

        return element
                .getElementsByTag("ul")
                .first()
                .getElementsByTag("li")
                .stream()
                .map(li -> li.getElementsByTag("a").attr("href").replace("/", ""))
                .collect(Collectors.toList());
    }

    private List<String> elementToImageUrls(Element element) {
        if (element == null) {
            return Collections.emptyList();
        }

        return element.select("div > ul").first()
                .getElementsByTag("li")
                .stream()
                .map(li -> li.getElementsByTag("img").attr("src"))
                .collect(Collectors.toList());
    }

    private Double getRating(String rating) { // TODO handle different cases
        return textToDouble(rating.split("/")[0]);
    }

    private Double textPercentToDouble(String textPercent) {
        String percent = textPercent.replace("%", "");

        return textToDouble(percent) / 100;
    }

    private Coordinate textCoordinateToCoordinate(String textCoordinate) {
        var latitude = Double.valueOf(textCoordinate.split(",")[0]);
        var longitude = Double.valueOf(textCoordinate.split(",")[1]);

        return ImmutableCoordinate.builder().latitude(latitude).longitude(longitude).build();
    }

    private Double textToDouble(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null");
        }

        return Double.valueOf(input.replaceAll(",", "."));
    }
}
