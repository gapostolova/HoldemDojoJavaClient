package com.nedogeek;

import java.util.List;

class Player {

    final String name;
    final int balance;
    final int bet;
    final String status;
    final List<Card> cards;
    Player(String name, int balance, int bet, String status, List<Card> cards) {
        this.name = name;
        this.balance = balance;
        this.bet = bet;
        this.status = status;
        this.cards = cards;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                ", bet=" + bet +
                ", status='" + status + '\'' +
                ", cards=" + cards +
                '}';
    }
}