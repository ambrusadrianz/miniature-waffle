package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

public class NameScoringStrategy implements ScoringStrategy {
    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        return ScoringHelper.getLevenshteinDistanceNormalized(restaurantRecord.getName(), company.getDisplayName());
    }
}
