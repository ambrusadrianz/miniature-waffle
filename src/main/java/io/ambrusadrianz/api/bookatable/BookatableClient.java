package io.ambrusadrianz.api.bookatable;

import io.ambrusadrianz.api.bookatable.model.*;
import io.reactivex.Flowable;

public interface BookatableClient {
    Flowable<City> searchCities(CitySearchRequest searchRequest);

    Flowable<Restaurant> fetchRestaurants(RestaurantListingRequest listingRequest);

    Flowable<RestaurantDetailed> fetchRestaurantsDetailed(RestaurantListingRequest listingRequest);
}
