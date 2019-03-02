package io.ambrusadrianz.api.hitta.model.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "visitingAddress", value = VisitingAddress.class),
        @JsonSubTypes.Type(name = "boxAddress", value = BoxAddress.class)
})
public interface Address {
    String getType();
}
