package io.ambrusadrianz.data.city.repository.impl;

import io.ambrusadrianz.data.city.model.CityRecord;
import io.ambrusadrianz.data.city.model.ImmutableCityRecord;
import io.ambrusadrianz.data.city.repository.CityRecordRepository;
import io.reactiverse.pgclient.Row;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import java.util.List;
import java.util.UUID;

public class CityRecordRepositoryImpl implements CityRecordRepository {

    private static final Function<Row, CityRecord> ROW_CITY_RECORD_MAPPER = row -> ImmutableCityRecord.builder()
            .id(row.getUUID("id"))
            .name(row.getString("name"))
            .country(row.getString("country"))
            .build();

    private final PgPool pgPool;

    public CityRecordRepositoryImpl(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Completable save(Flowable<CityRecord> cities) {
        return cities
                .map(city -> Tuple.of(city.getId(), city.getName(), city.getCountry()))
                .buffer(100)
                .flatMapSingle(tuples -> pgPool.rxPreparedBatch("INSERT INTO city (id, name, country) VALUES ($1, $2, $3)" +
                        " ON CONFLICT (name) DO NOTHING", tuples))
                .ignoreElements();
    }

    @Override
    public Flowable<CityRecord> findAll() {
        return pgPool.rxPreparedQuery("SELECT * FROM city")
                .flatMapPublisher(pgRowSet -> Flowable.fromIterable(() -> pgRowSet.iterator().getDelegate()))
                .map(ROW_CITY_RECORD_MAPPER);
    }

    @Override
    public Flowable<CityRecord> findAllById(List<UUID> ids) { // TODO report on github batch select support not working, or try to fix it and make a PR
        return Flowable.fromIterable(ids)
                .flatMap(this::findById);
    }

    @Override
    public Flowable<CityRecord> findById(UUID id) {
        return pgPool.rxPreparedQuery("SELECT * FROM city WHERE id = $1", Tuple.of(id))
                .flatMapPublisher(pgRowSet -> Flowable.fromIterable(() -> pgRowSet.iterator().getDelegate()))
                .map(ROW_CITY_RECORD_MAPPER);
    }

    @Override
    public Single<CityRecord> findByName(String name) {
        return pgPool.rxPreparedQuery("SELECT * FROM city WHERE unaccent(name) = unaccent($1)", Tuple.of(name))
                .flatMapPublisher(pgRowSet -> Flowable.fromIterable(() -> pgRowSet.iterator().getDelegate()))
                .firstOrError()
                .map(ROW_CITY_RECORD_MAPPER);
    }
}
