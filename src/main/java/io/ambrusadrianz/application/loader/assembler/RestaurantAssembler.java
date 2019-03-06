package io.ambrusadrianz.application.loader.assembler;

import io.ambrusadrianz.api.bookatable.model.Restaurant;
import io.ambrusadrianz.api.bookatable.model.RestaurantDetails;
import io.ambrusadrianz.api.bookatable.model.Review;
import io.ambrusadrianz.application.loader.model.LoaderCity;
import io.ambrusadrianz.data.restaurant.model.DetailsRecord;
import io.ambrusadrianz.data.restaurant.model.ImmutableCoordinateRecord;
import io.ambrusadrianz.data.restaurant.model.ImmutableDetailsRecord;
import io.ambrusadrianz.data.restaurant.model.ImmutableRestaurantRecord;
import io.ambrusadrianz.data.restaurant.model.ImmutableReviewRecord;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import io.ambrusadrianz.data.restaurant.model.ReviewRecord;

import java.util.Optional;

public class RestaurantAssembler {
    public RestaurantRecord createRestaurantDetailedRecord(LoaderCity city, Restaurant restaurant) {
        return ImmutableRestaurantRecord.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .details(createDetailsRecord(restaurant))
                .cityId(city.getId())
                .build();
    }

    public Optional<ReviewRecord> createReviewRecord(Optional<Review> reviewOptional) {
        if (!reviewOptional.isPresent()) {
            return Optional.empty();
        }

        return reviewOptional.map(review -> ImmutableReviewRecord.builder()
                .rating(review.getRating())
                .recommendationRate(review.getRecommendationRate())
                .reviewCount(review.getReviewCount())
                .ratings(review.getRatings())
                .build());
    }

    public Optional<DetailsRecord> createDetailsRecord(Restaurant restaurant) {
        Optional<RestaurantDetails> detailsOptional = restaurant.getDetails();
        if (detailsOptional.isEmpty()) {
            return Optional.empty();
        }

        var details = detailsOptional.get();
        var location = restaurant.getLocation();

        return Optional.of(ImmutableDetailsRecord.builder()
                .location(ImmutableCoordinateRecord.builder()
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .build())
                .openingHours(details.getOpeningHours())
                .cuisine(restaurant.getCuisine())
                .address(restaurant.getAddress())
                .postalCode(restaurant.getPostalCode())
                .contact(details.getContact())
                .websiteUrl(details.getWebsiteUrl())
                .review(createReviewRecord(details.getReview()))
                .nearbyRestaurants(details.getNearbyRestaurants())
                .imageUrls(details.getImageUrls())
                .description(details.getDescription())
                .build());
    }
}
