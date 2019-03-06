package io.ambrusadrianz.data.pairing.repository.impl;

import io.ambrusadrianz.data.pairing.HittaRestaurant;
import io.ambrusadrianz.data.pairing.repository.HittaRestaurantRepository;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Flowable;


public class HittaRestaurantRepositoryImpl implements HittaRestaurantRepository {

    private final PgPool pgPool;

    public HittaRestaurantRepositoryImpl(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Completable save(Flowable<HittaRestaurant> hittaRestaurantFlowable) {
        return hittaRestaurantFlowable
                .map(hittaRestaurant -> Tuple.of(hittaRestaurant.getHittaId(), hittaRestaurant.getRestaurantId(), hittaRestaurant.getRelevance()))
                .toList()
                .flatMap(tuples -> pgPool.rxPreparedBatch("INSERT INTO hitta_restaurant (hitta_id, restaurant_id, relevance) VALUES ($1, $2, $3) " +
                        "ON CONFLICT (hitta_id, restaurant_id) DO NOTHING", tuples))
                .ignoreElement();
    }
}
