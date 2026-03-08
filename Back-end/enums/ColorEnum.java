package br.com.AutoStock.enums;

public enum ColorEnum {
    PRETO("Preto"),
    BRANCO("Branco"),
    PRATA("Prata"),
    CINZA("Cinza"),
    AZUL("Azul"),
    VERMELHO("Vermelho"),
    VERDE("Verde"),
    AMARELO("Amarelo"),
    MARROM("Marrom"),
    BEGE("Bege"),
    ROXO("Roxo"),
    LARANJA("Laranja");

    private final String label;
    ColorEnum(String label){ this.label = label; }
    public String getLabel(){ return label; }
}
