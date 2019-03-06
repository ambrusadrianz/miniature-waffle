package io.ambrusadrianz;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.ambrusadrianz.api.bookatable.impl.BookatableClientImpl;
import io.ambrusadrianz.api.bookatable.impl.assembler.BookatableDocumentAssembler;
import io.ambrusadrianz.api.city.CityClient;
import io.ambrusadrianz.api.city.impl.CityClientImpl;
import io.ambrusadrianz.api.city.impl.assembler.CityDocumentAssembler;
import io.ambrusadrianz.api.hitta.impl.HittaClientImpl;
import io.ambrusadrianz.api.hitta.impl.HittaHeaderFactory;
import io.ambrusadrianz.application.ApplicationProperties;
import io.ambrusadrianz.application.ApplicationPropertiesFactory;
import io.ambrusadrianz.application.loader.ApplicationDataLoader;
import io.ambrusadrianz.application.loader.CityLoaderService;
import io.ambrusadrianz.application.loader.RestaurantLoaderService;
import io.ambrusadrianz.application.loader.assembler.CityAssembler;
import io.ambrusadrianz.application.loader.assembler.RestaurantAssembler;
import io.ambrusadrianz.application.pairing.impl.PairingService;
import io.ambrusadrianz.application.pairing.impl.scoring.CompositeScoringStrategy;
import io.ambrusadrianz.data.DatabaseProperties;
import io.ambrusadrianz.data.city.repository.CityRecordRepository;
import io.ambrusadrianz.data.city.repository.impl.CityRecordRepositoryImpl;
import io.ambrusadrianz.data.pairing.repository.HittaRestaurantRepository;
import io.ambrusadrianz.data.pairing.repository.impl.HittaRestaurantRepositoryImpl;
import io.ambrusadrianz.data.restaurant.repository.RestaurantRecordRepository;
import io.ambrusadrianz.data.restaurant.repository.impl.RestaurantRecordRepositoryImpl;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.reactivex.pgclient.PgClient;
import io.reactiverse.reactivex.pgclient.PgPool;
import io.reactivex.Completable;
import io.vertx.core.json.Json;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

public class Main {
    public static void main(String[] args) {
        Json.mapper.registerModules(new Jdk8Module());

        Vertx vertx = Vertx.vertx();
        WebClient webClient = WebClient.create(vertx, new WebClientOptions().setKeepAlive(true));

        YAMLMapper yamlMapper = new YAMLMapper();
        ApplicationPropertiesFactory applicationPropertiesFactory = new ApplicationPropertiesFactory(yamlMapper);
        ApplicationProperties properties = applicationPropertiesFactory.getApplicationProperties();
        DatabaseProperties database = properties.getDatabase();

        PgPoolOptions options = new PgPoolOptions()
                .setPort(database.getPort())
                .setHost(database.getHost())
                .setDatabase(database.getDatabase())
                .setMaxSize(database.getPoolMaxSize())
                .setUser(database.getUser())
                .setPassword(database.getPassword());

        PgPool pool = PgClient.pool(vertx, options);

        RestaurantRecordRepository repository = new RestaurantRecordRepositoryImpl(pool);

        HittaHeaderFactory hittaHeaderFactory = new HittaHeaderFactory(properties.getHitta());
        HittaClientImpl hittaClient = new HittaClientImpl(webClient, hittaHeaderFactory);

        BookatableDocumentAssembler bookatableDocumentAssembler = new BookatableDocumentAssembler();
        BookatableClientImpl bookatableClient = new BookatableClientImpl(webClient, properties.getBookatable(), bookatableDocumentAssembler);

        CityDocumentAssembler cityDocumentAssembler = new CityDocumentAssembler();
        CityClient cityClient = new CityClientImpl(webClient, cityDocumentAssembler);
        CityRecordRepository cityRecordRepository = new CityRecordRepositoryImpl(pool);

        CityLoaderService cityLoaderService = new CityLoaderService(cityClient, cityRecordRepository);
        RestaurantLoaderService restaurantLoaderService = new RestaurantLoaderService(bookatableClient, repository,
                cityRecordRepository, new RestaurantAssembler(), new CityAssembler());
        ApplicationDataLoader applicationDataLoader = new ApplicationDataLoader(cityLoaderService, restaurantLoaderService);

        HittaRestaurantRepository hittaRestaurantRepository = new HittaRestaurantRepositoryImpl(pool);
        PairingService pairingService = new PairingService(hittaClient, repository, cityRecordRepository, hittaRestaurantRepository, new CompositeScoringStrategy());
        Completable.concatArray(applicationDataLoader.run(), pairingService.run()).subscribe();
    }
}
