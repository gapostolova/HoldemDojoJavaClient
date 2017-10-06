package com.nedogeek;

class Card {
    final String suit;
    final String value;

    Card(String suit, String value) {
        this.suit = suit;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Card{" +
                "suit='" + suit + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
