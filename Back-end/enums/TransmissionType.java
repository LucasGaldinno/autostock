package br.com.AutoStock.enums;

public enum TransmissionType {
    MANUAL("Manual"),
    AUTOMATICO("Automático"),
    CVT("CVT"),
    AUTOMATIZADO("Automatizado");

    private final String label;
    TransmissionType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
