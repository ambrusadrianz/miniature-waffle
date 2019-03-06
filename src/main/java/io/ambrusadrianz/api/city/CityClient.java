package io.ambrusadrianz.api.city;

import io.ambrusadrianz.api.city.model.City;
import io.reactivex.Flowable;

public interface CityClient {
    Flowable<City> fetchCities();
}
