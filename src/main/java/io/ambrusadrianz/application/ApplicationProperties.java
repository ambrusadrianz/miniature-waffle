package io.ambrusadrianz.application;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ambrusadrianz.api.bookatable.impl.BookatableProperties;
import io.ambrusadrianz.api.hitta.impl.HittaProperties;
import org.immutables.value.Value;

import java.util.List;

@JsonSerialize(as = ImmutableApplicationProperties.class)
@JsonDeserialize(as = ImmutableApplicationProperties.class)
@Value.Immutable
public interface ApplicationProperties {
    BookatableProperties getBookatable();

    HittaProperties getHitta();

    List<String> getCitiesToScan();
}
