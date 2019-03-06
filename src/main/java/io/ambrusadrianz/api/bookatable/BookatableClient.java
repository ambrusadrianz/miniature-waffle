package io.ambrusadrianz.api.bookatable;

import io.ambrusadrianz.api.bookatable.model.City;
import io.ambrusadrianz.api.bookatable.model.CitySearchRequest;
import io.ambrusadrianz.api.bookatable.model.Restaurant;
import io.ambrusadrianz.api.bookatable.model.RestaurantListingRequest;
import io.reactivex.Flowable;

public interface BookatableClient {
    Flowable<City> searchCities(CitySearchRequest searchRequest);

    Flowable<Restaurant> fetchRestaurants(RestaurantListingRequest listingRequest);

    Flowable<Restaurant> fetchRestaurantsDetailed(RestaurantListingRequest listingRequest);
}
