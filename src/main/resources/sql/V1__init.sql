CREATE EXTENSION unaccent;

CREATE TABLE city (
    id UUID PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE,
    country VARCHAR NOT NULL
);

CREATE TABLE restaurant (
    id VARCHAR NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    city_id UUID NOT NULL REFERENCES city(id),
    details JSONB NOT NULL
);

CREATE TABLE hitta_restaurant (
    hitta_id VARCHAR NOT NULL,
    restaurant_id VARCHAR NOT NULL REFERENCES restaurant(id),
    relevance DOUBLE PRECISION NOT NULL,
    PRIMARY KEY(hitta_id, restaurant_id)
);

