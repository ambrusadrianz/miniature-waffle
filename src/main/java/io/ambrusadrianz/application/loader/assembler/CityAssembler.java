package io.ambrusadrianz.application.loader.assembler;

import io.ambrusadrianz.api.bookatable.model.City;
import io.ambrusadrianz.application.loader.model.ImmutableLoaderCity;
import io.ambrusadrianz.application.loader.model.LoaderCity;
import io.ambrusadrianz.data.city.model.CityRecord;

public class CityAssembler {
    public LoaderCity createLoaderCity(CityRecord cityRecord, City city) {
        return ImmutableLoaderCity.builder()
                .id(cityRecord.getId())
                .city(cityRecord.getName())
                .country(city.getCountry())
                .restaurantCount(city.getRestaurantCount())
                .build();
    }
}
