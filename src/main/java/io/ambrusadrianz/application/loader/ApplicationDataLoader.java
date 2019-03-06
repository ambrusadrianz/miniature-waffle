package io.ambrusadrianz.application.loader;

import io.reactivex.Completable;

public class ApplicationDataLoader {
    private final CityLoaderService cityLoaderService;
    private final RestaurantLoaderService restaurantLoaderService;

    public ApplicationDataLoader(CityLoaderService cityLoaderService, RestaurantLoaderService restaurantLoaderService) {
        this.cityLoaderService = cityLoaderService;
        this.restaurantLoaderService = restaurantLoaderService;
    }

    public Completable run() {
        return Completable.concatArray(cityLoaderService.run(), restaurantLoaderService.run());
    }
}
