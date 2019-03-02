package io.ambrusadrianz.api.hitta.model.request;

public enum GeoSystem {
    RT90("RT90"), WGS84("WGS84"), SWEREF99TM("SWEREF99TM"), PSEUDO_MERCATOR("PseudoMercator");

    String value;

    GeoSystem(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
