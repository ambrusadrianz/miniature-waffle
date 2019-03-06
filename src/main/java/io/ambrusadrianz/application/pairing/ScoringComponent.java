package io.ambrusadrianz.application.pairing;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ScoringComponent {

    private final ScoringStrategy scoringStrategy;

    public ScoringComponent(ScoringStrategy scoringStrategy) {
        this.scoringStrategy = scoringStrategy;
    }

    public SortedSet<Pair<Company, Double>> assignScore(RestaurantRecord restaurantRecord, List<Company> companies) {
        Comparator<Pair<Company, Double>> comparator = Comparator.comparingDouble(Pair::getRight);
        SortedSet<Pair<Company, Double>> result = new TreeSet<>(comparator.reversed());

        for (Company company : companies) {
            result.add(Pair.of(company, scoringStrategy.score(restaurantRecord, company)));
        }

        return result;
    }

}
