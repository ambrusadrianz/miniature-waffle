package io.ambrusadrianz.application.pairing.impl;

import io.ambrusadrianz.api.hitta.HittaClient;
import io.ambrusadrianz.api.hitta.model.request.ImmutableSearchRequest;
import io.ambrusadrianz.api.hitta.model.request.SearchRequest;
import io.ambrusadrianz.application.pairing.ScoringStrategy;
import io.ambrusadrianz.data.city.model.CityRecord;
import io.ambrusadrianz.data.city.repository.CityRecordRepository;
import io.ambrusadrianz.data.pairing.HittaRestaurant;
import io.ambrusadrianz.data.pairing.ImmutableHittaRestaurant;
import io.ambrusadrianz.data.pairing.repository.HittaRestaurantRepository;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import io.ambrusadrianz.data.restaurant.repository.RestaurantRecordRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.UUID;

public class PairingService {

    private final HittaClient hittaClient;
    private final RestaurantRecordRepository restaurantRecordRepository;
    private final CityRecordRepository cityRecordRepository;
    private final HittaRestaurantRepository hittaRestaurantRepository;
    private final ScoringStrategy scoringStrategy;

    public PairingService(HittaClient hittaClient,
                          RestaurantRecordRepository restaurantRecordRepository,
                          CityRecordRepository cityRecordRepository,
                          HittaRestaurantRepository hittaRestaurantRepository,
                          ScoringStrategy scoringStrategy) {
        this.hittaClient = hittaClient;
        this.restaurantRecordRepository = restaurantRecordRepository;
        this.cityRecordRepository = cityRecordRepository;
        this.hittaRestaurantRepository = hittaRestaurantRepository;
        this.scoringStrategy = scoringStrategy;
    }

    public Completable run() {
        Flowable<RestaurantRecord> restaurantRecordFlowable = restaurantRecordRepository.findAll();
        Single<Map<UUID, CityRecord>> cityRecordByIdMap = restaurantRecordFlowable
                .map(RestaurantRecord::getCityId)
                .distinct()
                .toList()
                .flatMapPublisher(cityRecordRepository::findAllById)
                .toMap(CityRecord::getId, cityRecord -> cityRecord)
                .cache();

        return restaurantRecordFlowable
                .doOnSubscribe(c -> System.out.println("Started the pairing process"))
                .flatMapSingle(restaurantRecord -> cityRecordByIdMap.map(map -> Pair.of(restaurantRecord, map.get(restaurantRecord.getCityId()))))
                .map(pair -> {
                    SearchRequest searchRequest = ImmutableSearchRequest.builder()
                            .what(pair.getLeft().getName())
                            .where(pair.getRight().getName())
                            .build();

                    return Pair.of(pair.getLeft(), hittaClient.search(searchRequest));
                })
                .flatMap(pair -> pair.getRight().map(company -> {
                    Double score = scoringStrategy.score(pair.getLeft(), company);

                    return (HittaRestaurant) ImmutableHittaRestaurant.builder()
                            .hittaId(company.getId())
                            .restaurantId(pair.getLeft().getId())
                            .relevance(score)
                            .build();
                }))
                .filter(hittaRestaurant -> hittaRestaurant.getRelevance() > 10)
                .window(100)
                .map(hittaRestaurantRepository::save)
                .doOnNext(c -> System.out.println("Saved hitta id with restaurant id and relevance into database"))
                .flatMapCompletable(c -> c)
                .doOnTerminate(() -> System.out.println("Finished the pairing processs"));
    }
}
