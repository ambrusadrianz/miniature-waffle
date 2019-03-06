package io.ambrusadrianz.application.pairing;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

public interface ScoringStrategy {
    Double score(RestaurantRecord restaurantRecord, Company company);
}
