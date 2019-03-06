package io.ambrusadrianz.data.pairing;

import org.immutables.value.Value;

@Value.Immutable
public interface HittaRestaurant {
    String getHittaId();

    String getRestaurantId();

    Double getRelevance();
}
