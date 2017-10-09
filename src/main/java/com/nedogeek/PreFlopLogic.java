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

    public static boolean hasAce(Client.Card card1, Client.Card card2){
        if(card1.getValue().equalsIgnoreCase("A") || card2.getValue().equalsIgnoreCase("A")){
            return true;
        }
        return false;
    }

    //card1 and card 2 same value
    public static boolean hasHandStrongerThan(Client.Card card1, Client.Card card2, String range){
        int playerCardPowerIndex = cardsOrder.indexOf(card1.getValue());
        int rangePowerIndex = cardsOrder.indexOf(range);
        return hasPairSuit(card1, card2) && (playerCardPowerIndex >= rangePowerIndex);
    }

    public static boolean hasAceAndStrongCard(Client.Card card1, Client.Card card2, String strongCardRange){
        String card1Value = card1.getValue();
        String card2Value = card2.getValue();


        return card1Value.equalsIgnoreCase("A") && (cardsOrder.indexOf(card2Value) >= cardsOrder.indexOf(strongCardRange)) ||
                (cardsOrder.indexOf(card1Value) >= cardsOrder.indexOf(strongCardRange)) && card2Value.equalsIgnoreCase("A");
    }




}
