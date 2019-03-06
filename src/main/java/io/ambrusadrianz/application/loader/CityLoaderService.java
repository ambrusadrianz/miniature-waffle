package io.ambrusadrianz.application.loader;

import io.ambrusadrianz.api.city.CityClient;
import io.ambrusadrianz.data.city.model.CityRecord;
import io.ambrusadrianz.data.city.model.ImmutableCityRecord;
import io.ambrusadrianz.data.city.repository.CityRecordRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;

import java.util.UUID;

public class CityLoaderService {

    private final CityClient cityClient;
    private final CityRecordRepository cityRecordRepository;

    public CityLoaderService(CityClient cityClient, CityRecordRepository cityRecordRepository) {
        this.cityClient = cityClient;
        this.cityRecordRepository = cityRecordRepository;
    }

    public Completable run() {
        Flowable<CityRecord> flowable = cityClient.fetchCities()
                .map(city -> (CityRecord) ImmutableCityRecord.builder()
                        .id(UUID.randomUUID())
                        .name(city.getName())
                        .country(city.getCountry())
                        .build())
                .cache();

        Completable stats = flowable.map(CityRecord::getName).toList()
                .doOnSuccess(cities -> System.out.println("Cities fetched into the database:" + cities))
                .ignoreElement();

        return Completable.concatArray(stats, cityRecordRepository.save(flowable));
    }
}
