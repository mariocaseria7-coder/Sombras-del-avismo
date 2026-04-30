package com.sombrasdelavismo.model;

public enum CardRarity {
    SPELL("Hechizo"),
    COMMON("Comun"),
    RARE("Rara"),
    EPIC("Epica"),
    MYTHIC("Mitica");

    private final String displayName;

    CardRarity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
