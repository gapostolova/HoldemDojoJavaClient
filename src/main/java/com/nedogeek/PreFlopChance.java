package com.nedogeek;

import java.util.Arrays;
import java.util.List;

public class PreFlopChance {


    private List<String> cardsOrder = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

    public static int calculateChance(Client.Card card1, Client.Card card2) {
        if(card1.getValue().equalsIgnoreCase(card2.getValue())){
            return 1;
        }

        return 1;
    }



}
