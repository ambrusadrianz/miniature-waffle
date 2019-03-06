package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.api.hitta.model.response.VisitingAddress;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.DetailsRecord;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class AddressScoringStrategy implements ScoringStrategy {
    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        if (restaurantRecord.getDetails().isEmpty() || restaurantRecord.getDetails().get().getAddress().isEmpty()) {
            return 0d;
        }
        DetailsRecord details = restaurantRecord.getDetails().get();
        String restaurantAddress = details.getAddress().get();

        Optional<VisitingAddress> visitingAddressOptional = company.getAddress().stream()
                .filter(address -> address instanceof VisitingAddress)
                .map(address -> (VisitingAddress) address)
                .findFirst();

        return visitingAddressOptional.map(visitingAddress -> {
            if (StringUtils.containsIgnoreCase(restaurantAddress, visitingAddress.getStreet()) &&
                    (visitingAddress.getNumber() == null || StringUtils.contains(restaurantAddress, visitingAddress.getNumber()))) {
                return 10d;
            } else {
                return ScoringHelper.getLevenshteinDistanceNormalized(restaurantAddress, visitingAddress.getStreet() + visitingAddress.getNumber());
            }
        }).orElse(0d);
    }
}
