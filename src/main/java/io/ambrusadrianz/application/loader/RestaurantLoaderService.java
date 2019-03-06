package io.ambrusadrianz.application.loader;

import io.ambrusadrianz.api.bookatable.BookatableClient;
import io.ambrusadrianz.api.bookatable.model.ImmutableCitySearchRequest;
import io.ambrusadrianz.api.bookatable.model.ImmutableRestaurantListingRequest;
import io.ambrusadrianz.application.loader.assembler.CityAssembler;
import io.ambrusadrianz.application.loader.assembler.RestaurantAssembler;
import io.ambrusadrianz.application.loader.model.LoaderCity;
import io.ambrusadrianz.data.city.repository.CityRecordRepository;
import io.ambrusadrianz.data.restaurant.repository.RestaurantRecordRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import org.apache.commons.lang3.tuple.Pair;

public class RestaurantLoaderService {

    private final BookatableClient bookatableClient;
    private final RestaurantRecordRepository restaurantRecordRepository;
    private final CityRecordRepository cityRecordRepository;
    private final RestaurantAssembler restaurantAssembler;
    private final CityAssembler cityAssembler;

    public RestaurantLoaderService(BookatableClient bookatableClient,
                                   RestaurantRecordRepository restaurantRecordRepository,
                                   CityRecordRepository cityRecordRepository,
                                   RestaurantAssembler restaurantAssembler,
                                   CityAssembler cityAssembler) {
        this.bookatableClient = bookatableClient;
        this.restaurantRecordRepository = restaurantRecordRepository;
        this.restaurantAssembler = restaurantAssembler;
        this.cityRecordRepository = cityRecordRepository;
        this.cityAssembler = cityAssembler;
    }

    public Completable run() {
        Flowable<LoaderCity> loaderCities = cityRecordRepository.findAll()
                .map(cityRecord -> {
                    var searchRequest = ImmutableCitySearchRequest.builder().query(cityRecord.getName()).limit(1).build();
                    return Pair.of(cityRecord, bookatableClient.searchCities(searchRequest));
                })
                .flatMap(pair -> pair.getRight().map(city -> cityAssembler.createLoaderCity(pair.getLeft(), city)))
                .filter(city -> city.getCountry().equals("Sweden"))
                .filter(city -> city.getRestaurantCount() != 0)
                .cache();

        Completable stats = loaderCities.toList()
                .flatMapPublisher(Flowable::fromIterable)
                .map(city -> Pair.of(city.getCity(), city.getRestaurantCount()))
                .toList()
                .doOnSuccess(cities -> System.out.println("Cities that contain Restaurants from Boookatable=" + cities))
                .ignoreElement();

        Completable save = loaderCities
                .doOnSubscribe(s -> System.out.println("Started saving Restaurants of selected cities..."))
                .map(city -> {
                    var listingRequest = ImmutableRestaurantListingRequest.builder()
                            .city(city.getCity())
                            .country(city.getCountry())
                            .build();

                    return Pair.of(city, listingRequest);
                })
                .map(pair -> Pair.of(pair.getLeft(), bookatableClient.fetchRestaurantsDetailed(pair.getRight())))
                .flatMap(pair -> pair.getRight().map(restaurant -> restaurantAssembler.createRestaurantDetailedRecord(pair.getLeft(), restaurant)))
                .window(100)
                .map(restaurantRecordRepository::save)
                .doOnEach(c -> System.out .println("Saved/Updated a batch of restaurants"))
                .flatMapCompletable(completable -> completable);

        return Completable.concatArray(stats, save);
    }
}
