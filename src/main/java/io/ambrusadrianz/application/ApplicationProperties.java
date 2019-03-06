package io.ambrusadrianz.application;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ambrusadrianz.api.bookatable.impl.BookatableProperties;
import io.ambrusadrianz.api.hitta.impl.HittaProperties;
import io.ambrusadrianz.data.DatabaseProperties;
import org.immutables.value.Value;

@JsonSerialize(as = ImmutableApplicationProperties.class)
@JsonDeserialize(as = ImmutableApplicationProperties.class)
@Value.Immutable
public interface ApplicationProperties {
    BookatableProperties getBookatable();

    HittaProperties getHitta();

    DatabaseProperties getDatabase();
}
