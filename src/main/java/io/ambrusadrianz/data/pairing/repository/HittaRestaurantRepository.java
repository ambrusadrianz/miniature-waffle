package io.ambrusadrianz.data.pairing.repository;

import io.ambrusadrianz.data.pairing.HittaRestaurant;
import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface HittaRestaurantRepository {
    Completable save(Flowable<HittaRestaurant> hittaRestaurantFlowable);
}
