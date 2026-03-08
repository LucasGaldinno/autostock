package br.com.AutoStock.enums;

public enum FuelType {
    GASOLINA("Gasolina"),
    ETANOL("Etanol"),
    FLEX("Flex"),
    DIESEL("Diesel"),
    GNV("GNV"),
    ELETRICO("Elétrico"),
    HIBRIDO("Híbrido");

    private final String label;
    FuelType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
