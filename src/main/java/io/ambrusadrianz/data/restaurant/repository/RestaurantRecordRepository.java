package io.ambrusadrianz.data.restaurant.repository;

import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface RestaurantRecordRepository {
    Completable save(Flowable<RestaurantRecord> records);

    Flowable<RestaurantRecord> findAll();
}
