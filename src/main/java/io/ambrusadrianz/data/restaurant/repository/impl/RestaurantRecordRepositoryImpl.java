package io.ambrusadrianz.data.restaurant.repository.impl;


import io.ambrusadrianz.data.restaurant.model.DetailsRecord;
import io.ambrusadrianz.data.restaurant.model.ImmutableRestaurantRecord;
import io.ambrusadrianz.data.restaurant.model.RestaurantRecord;
import io.ambrusadrianz.data.restaurant.repository.RestaurantRecordRepository;
import io.reactiverse.pgclient.Row;
import io.reactiverse.pgclient.data.Json;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactiverse.reactivex.pgclient.Tuple;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.vertx.core.json.JsonObject;

public class RestaurantRecordRepositoryImpl implements RestaurantRecordRepository {

    private final PgPool pgPool;
    private final RestaurantRecordRowMapper restaurantRecordRowMapper;

    public RestaurantRecordRepositoryImpl(PgPool pgPool) {
        this.pgPool = pgPool;
        this.restaurantRecordRowMapper = new RestaurantRecordRowMapper();
    }

    @Override
    public Completable save(Flowable<RestaurantRecord> records) {
        return records
                .map(record -> Tuple.of(record.getId(),
                        record.getName(),
                        record.getCityId(),
                        Json.create(JsonObject.mapFrom(record.getDetails()))))
                .toList()
                .flatMap(tuples -> pgPool.rxPreparedBatch("INSERT INTO restaurant (id, name, city_id, details) VALUES ($1, $2, $3, $4)" +
                        " ON CONFLICT (id) DO NOTHING", tuples))
                .ignoreElement();
    }

    @Override
    public Flowable<RestaurantRecord> findAll() {
        return pgPool.rxPreparedQuery("SELECT * FROM restaurant")
                .flatMapPublisher(pgRowSet -> Flowable.fromIterable(() -> pgRowSet.iterator().getDelegate()))
                .map(restaurantRecordRowMapper);
    }

    private class RestaurantRecordRowMapper implements Function<Row, RestaurantRecord> {
        @Override
        public RestaurantRecord apply(Row row) {
            return ImmutableRestaurantRecord.builder()
                    .id(row.getString("id"))
                    .cityId(row.getUUID("city_id"))
                    .name(row.getString("name"))
                    .details(io.vertx.core.json.Json.decodeValue(io.vertx.core.json.Json.encode(row.getJson("details").value()), DetailsRecord.class))
                    .build();
        }
    }
}
