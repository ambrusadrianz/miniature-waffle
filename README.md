# miniature-waffle
Hitta Interview Project



## Requirements

* docker
* docker-compose
* maven
* java

## How to

The project contains a `docker-compose.yml` which will start a PostgreSQL database with a `flyway` docker image, to run the database migration.

The `build_and_run.sh` script will start the docker environment, build the Java artifact with `maven` and run the artifact.

## Process

The project will fetch the top 60 cities of Sweden from Wikipedia. Afterwards it will fetch the `Bookatable` restaurants for each city and save it into the database. After saving the data, it will retrieve it from the database to pair it with the Hitta Company API. It will save a triple of (hitta_id, restaurant_id, relevance) into the `hitta_restaurant` table. 

In case the Hitta API limits are reached, the credentials should be changed in the `src/main/java/resources/application.yml` file.

## Nice to know

The database created with docker can be reach at `localhost:5432` with username `waffle` and password `password` with database name `miniature_waffle`.

`psql -h localhost -p 5432 -U waffle miniature_waffle`

## TODO

- [ ] Add proper logging framework
- [ ] Since the Bookatable API is not stable (meaning it will skip over some restaurants), add retry on fetching to make sure we fetch everything
- [ ] Clean up ScoringStrategies
- [ ] Save Hitta companies to DB
- [ ] Separate entities more cleanly, for each layer different domain entities
- [ ] Use Dependency Injection framework
- [ ] Write Unit tests for ScoringStrategies
- [ ] Write Unit tests for Assemblers