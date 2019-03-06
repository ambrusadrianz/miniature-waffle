package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

import java.util.List;

public class CompositeScoringStrategy implements ScoringStrategy {

    private final List<ScoringStrategy> scoringStrategies;

    public CompositeScoringStrategy() {
        this.scoringStrategies = List.of(new AddressScoringStrategy(),
                new NameScoringStrategy(),
                new ZipcodeScoringStrategy(),
                new PhoneNumberScoringStrategy(),
                new LocationScoringStrategy());
    }

    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        return scoringStrategies.stream()
                .map(scoringStrategy -> scoringStrategy.score(restaurantRecord, company))
                .reduce(0d, (a, b) -> a + b);
    }
}
