package com.nedogeek;

import java.util.Arrays;
import java.util.List;

public class PreFlopLogic {


    private static List<String> cardsOrder = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");

    public static boolean hasPair(Client.Card card1, Client.Card card2) {
        if(card1.getValue().equalsIgnoreCase(card2.getValue())){
            return true;
        }
        return false;
    }

    public static boolean hasPairSuit(Client.Card card1, Client.Card card2) {
        if(card1.getSuit().equalsIgnoreCase(card2.getSuit())){
            return true;
        }
        return false;
    }

    public static boolean hasChanceForStraight(Client.Card card1, Client.Card card2) {

        int card1Index = cardsOrder.indexOf(card1.getValue());
        int card2Index = cardsOrder.indexOf(card2.getValue());

        if(Math.abs(card1Index - card2Index) <= 4 || Math.abs(card1Index - card2Index) == 12){
            return true;
        }

        return false;
    }

}
