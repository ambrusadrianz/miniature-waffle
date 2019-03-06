package io.ambrusadrianz.api.city.impl.assembler;

import io.ambrusadrianz.api.city.model.City;
import io.ambrusadrianz.api.city.model.ImmutableCity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CityDocumentAssembler {
    public List<City> documentToCities(Document document) {
        var orderedList = document.select("#mw-content-text > div > ol").first();

        if (orderedList == null) {
            return Collections.emptyList();
        }

        return orderedList.getElementsByTag("li").stream()
                .map(this::listItemToCity)
                .collect(Collectors.toList());
    }

    public City listItemToCity(Element listItem) {
        return ImmutableCity.builder()
                .name(listItem.getElementsByTag("a").first().text())
                .country("Sweden") // TODO make it more generic
                .build();
    }
}
