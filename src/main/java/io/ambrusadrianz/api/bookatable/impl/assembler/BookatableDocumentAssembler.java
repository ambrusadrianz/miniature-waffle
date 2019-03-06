package io.ambrusadrianz.api.bookatable.impl.assembler;

import io.ambrusadrianz.api.bookatable.impl.IntermediateCity;
import io.ambrusadrianz.api.bookatable.model.City;
import io.ambrusadrianz.api.bookatable.model.Coordinate;
import io.ambrusadrianz.api.bookatable.model.ImmutableCity;
import io.ambrusadrianz.api.bookatable.model.ImmutableCoordinate;
import io.ambrusadrianz.api.bookatable.model.ImmutableRestaurant;
import io.ambrusadrianz.api.bookatable.model.ImmutableRestaurantDetails;
import io.ambrusadrianz.api.bookatable.model.ImmutableReview;
import io.ambrusadrianz.api.bookatable.model.Restaurant;
import io.ambrusadrianz.api.bookatable.model.RestaurantDetails;
import io.ambrusadrianz.api.bookatable.model.Review;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BookatableDocumentAssembler {

    private static final String ALL_RESTAURANT_COUNT_CSS_SELECTOR = "#filter-list > form > fieldset:nth-child(1) > ul > li.filter-list__li.all-restaurants.active > a > span > span";
    private static final Pattern POSTCODE_PATTERN = Pattern.compile("([0-9][\\s]*){5}");
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("\\/\\/(.*id=[^&]+)");

    private Restaurant listItemToRestaurant(String city, Element element) {
        var id = element.select(".block-link").attr("href");
        var name = element.select(".item > .detail > .fn").text();
        var latitudeLongitude = element.select(".item").attr("data-latlng");
        var completeAddress = element.select(".item > .detail > .adr").text();
        var cuisine = element.select(".item > .detail > .cuisine").text();
        var imageUrl = element.select("noscript > img").attr("src");

        Pair<String, Integer> addressAndPostalCode = textToAddressAndPostalCode(completeAddress);

        return ImmutableRestaurant.builder()
                .id(id)
                .name(name)
                .address(Optional.ofNullable(addressAndPostalCode.getLeft()))
                .postalCode(Optional.ofNullable(addressAndPostalCode.getRight()))
                .cuisine(cuisine)
                .imageUrl(normalizeImageUrl(imageUrl))
                .location(textCoordinateToCoordinate(latitudeLongitude))
                .city(city)
                .build();
    }

    public List<Restaurant> documentToRestaurants(String city, Document document) {
        var unorderedList = document.getElementsByTag("ul").first();

        if (unorderedList == null) {
            return Collections.emptyList();
        }

        return unorderedList
                .getElementsByTag("li")
                .stream()
                .map(li -> listItemToRestaurant(city, li))
                .collect(Collectors.toList());
    }

    public RestaurantDetails documentToRestaurantDetails(Document document) {
        Element element = document.select(".content-wrapper").first();

        if (element == null) {
            return null;
        }

        var description = element.select("#panel-overview > div:nth-child(6) > div > p").text();
        var openingHours = element.select("#panel-overview > div.col-2 > dl > dd:nth-child(5)").text();
        var contact = element.select("#panel-location > table > tbody > tr > td > table > tbody > tr:nth-child(1) > td > a").text();
        var websiteUrl = element.select("#panel-location > table > tbody > tr > td > table > tbody > tr:nth-child(2) > td > a").attr("href");
        var review = documentToReview(element.select("#panel-reviews > div.aggregate-review").first());
        var nearbyRestaurants = documentToNearbyRestaurants(element.select("div.secondary > div.restaurant-list.nearby").first());
        var imageUrls = documentToImageUrls(element.select("#slider").first());

        return ImmutableRestaurantDetails.builder()
                .description(description)
                .openingHours(openingHours)
                .contact(contact)
                .websiteUrl(websiteUrl)
                .review(Optional.ofNullable(review))
                .nearbyRestaurants(nearbyRestaurants)
                .imageUrls(imageUrls)
                .build();
    }

    public Review documentToReview(Element element) {
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

    public List<String> documentToNearbyRestaurants(Element element) {
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

    public List<String> documentToImageUrls(Element element) {
        if (element == null) {
            return Collections.emptyList();
        }

        return element.select("div > ul").first()
                .getElementsByTag("li")
                .stream()
                .map(li -> li.getElementsByTag("img").attr("src"))
                .map(this::normalizeImageUrl)
                .collect(Collectors.toList());
    }

    public City documentToCity(IntermediateCity intermediateCity, Document document) {
        String restaurantCountString = document.select(ALL_RESTAURANT_COUNT_CSS_SELECTOR)
                .text()
                .replaceAll("[()]", "");
        Integer restaurantCount = restaurantCountString.isEmpty() ? 0 : Integer.valueOf(restaurantCountString);

        return ImmutableCity.builder()
                .name(intermediateCity.getName())
                .city(intermediateCity.getCity())
                .country(intermediateCity.getCountry())
                .type(intermediateCity.getType())
                .url(intermediateCity.getUrl())
                .restaurantCount(restaurantCount)
                .build();
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

    private Pair<String, Integer> textToAddressAndPostalCode(String input) {
        Matcher matcher = POSTCODE_PATTERN.matcher(input);
        String address = null;
        Integer postalCode = null;
        if (matcher.find()) {
            String postalCodeString = matcher.group();
            postalCode = Integer.valueOf(postalCodeString.replaceAll(" ", ""));
            address = input.substring(0, input.indexOf(postalCodeString) - 2);
        }

        return Pair.of(address, postalCode);
    }

    private String normalizeImageUrl(String imageUrl) {
        Matcher imageUrlMatcher = IMAGE_URL_PATTERN.matcher(imageUrl);
        if (imageUrlMatcher.find()) {
            String group = imageUrlMatcher.group(1);
            return "https://" + group;
        }

        throw new IllegalArgumentException("ImageUrl not correct");
    }
}
