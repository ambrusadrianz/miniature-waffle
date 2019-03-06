package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

public class PhoneNumberScoringStrategy implements ScoringStrategy {
    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        if (restaurantRecord.getDetails().isEmpty()) {
            return 0d;
        }

        if (company.getPhone().isEmpty()) {
            return 0d;
        }

        String restaurantContact = restaurantRecord.getDetails().get().getContact().replaceAll(" ", "");
        String companyContact = company.getPhone().get(0).getCallTo().replaceAll(" ", "");

        if (restaurantContact.compareTo(companyContact) == 0) {
            return 10d;
        } else {
            return 0d;
        }
    }
}
