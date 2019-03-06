package io.ambrusadrianz.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDatabaseProperties.class)
@JsonDeserialize(as = ImmutableDatabaseProperties.class)
public interface DatabaseProperties {
    Integer getPort();

    String getHost();

    String getDatabase();

    String getUser();

    String getPassword();

    Integer getPoolMaxSize();
}
