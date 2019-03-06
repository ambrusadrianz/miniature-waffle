package io.ambrusadrianz.data.city.repository;

import io.ambrusadrianz.data.city.model.CityRecord;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.util.List;
import java.util.UUID;

public interface CityRecordRepository {
    Completable save(Flowable<CityRecord> cities);

    Flowable<CityRecord> findAll();

    Flowable<CityRecord> findAllById(List<UUID> ids);

    Flowable<CityRecord> findById(UUID id);

    Single<CityRecord> findByName(String name);
}
