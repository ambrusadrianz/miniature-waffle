package io.ambrusadrianz.application.pairing.impl.scoring;

import io.ambrusadrianz.api.hitta.model.response.Company;
import io.ambrusadrianz.api.hitta.model.response.RouteCoordinate;
import io.ambrusadrianz.api.hitta.model.response.VisitingAddress;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.restaurant.model.CoordinateRecord;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;

public class LocationScoringStrategy implements ScoringStrategy {
    @Override
    public Double score(RestaurantRecord restaurantRecord, Company company) {
        if (restaurantRecord.getDetails().isEmpty()) {
            return 0d;
        }

        if (company.getAddress().isEmpty()) {
            return 0d;
        }

        if (!(company.getAddress().get(0) instanceof VisitingAddress)) {
            return 0d;
        }

        CoordinateRecord coordinateRecord = restaurantRecord.getDetails().get().getLocation();
        RouteCoordinate routeCoordinate = ((VisitingAddress) company.getAddress().get(0)).getRouteCoordinate();

        if (routeCoordinate == null) {
            return 0d;
        }

        double latitudeDifference = Math.abs(coordinateRecord.getLatitude() - routeCoordinate.getLatitude());
        double longitudeDifference = Math.abs(coordinateRecord.getLongitude() - routeCoordinate.getLongitude());
        if (latitudeDifference > 0.001d && longitudeDifference > 0.001d) {
            return 0d;
        } else {
            double normalizedLatitudeDifference = ((0.001d - latitudeDifference) / 0.001d) * 10d;
            double normalizedLongitudeDifference = ((0.001d - longitudeDifference) / 0.001d) * 10d;

            return (normalizedLatitudeDifference + normalizedLongitudeDifference) / 2.0d;
        }
    }
}
