package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Address;
import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

public class ZipcodeScoringStrategy implements ScoringStrategy {
    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        Address address = company.getAddress().get(0);
        if (address == null) {
            return 0d;
        } else {
            if (restaurantRecord.getDetails().get().getPostalCode().equals(address.getZipcode())) {
                return 10d;
            } else {
                return 0d;
            }
        }
    }
}
