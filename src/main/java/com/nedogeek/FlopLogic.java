package com.nedogeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlopLogic {


    private static List<String> cardsOrder = Arrays.asList("A","2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");


    public static int getCombinationPower(String combination){
        List<String> possibleCombinations = Client.getCardCombinations();

        for(int i = possibleCombinations.size() - 1; i >= 0; i--){
            combination = combination.toLowerCase();
            if(combination.contains(possibleCombinations.get(i))){
                return i + 1;
            }
        }

        return 0;
    }

    public static boolean hasStraightFlush(List<Client.Card> deskCards, List<Client.Card> playerCards){



          return true;
    }

    /**
     * check if on desk you have:
     *      3 cards same suit
     *      3 sequential cards
     * @return
     */
    public static boolean dangerousFlop(List<Client.Card> deskCards){
        List<String> convertedCards = new ArrayList<>();
        List<String> cardsSuit = new ArrayList<>();
        for(Client.Card card : deskCards){
            convertedCards.add(card.getValue());
            cardsSuit.add(card.suit);
        }
        //TODO finish the method
        return true;
    }

//    public static int sequentialCards(List<String> cards){
//        boolean isSequential = false;
//        int countConsequalCards = 0;
//        List<Integer> sequentialCardIndexes = new ArrayList<>();
//
////        if(sequentialCards > cards.size()){
////            return false;
////        }
//
//        for(int i = 0; i < cards.size()-1; i++){
//            int card1Index = cardsOrder.indexOf(cards.get(i));
//            for(int j = i+1; j < cards.size(); j++){
//                int card2Index = cardsOrder.indexOf(cards.get(j));
//                    if(card1Index == card2Index) {
//                        continue;
//                    }
//                    if(card1Index == 0 || card2Index == 0) {
//                        if (aceCalculation(card1Index, card2Index)) {
//
//                        }
//                    }
//                    if(Math.abs((card1Index-card1Index)) <=) {
//                        countConsequalCards++;
//                    }
//                }
//            }
//            return countConsequalCards;
//        }
//
//
//    //returns true if ace in the sequence
//    private static boolean aceCalculation(int card1, int card2){
//        int aceAtBeginning = Math.abs((card1 - card2));
//        if(card1 == 0 && card1 != card2) {
//            int aceAtEnd = Math.abs((cardsOrder.size() - card2));
//            if (aceAtBeginning > 1 || aceAtEnd > 1) {
//                return false;
//            }
//        }
//        if(card2 == 0 && card1 != card2) {
//            int aceAtEnd = Math.abs((cardsOrder.size() - card1));
//            if (aceAtBeginning > 1 || aceAtEnd > 1) {
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean sameSuit(List<String> cards){
        String suit = cards.get(0);
        for(String card : cards){
            if(!suit.equalsIgnoreCase(card)){
                return false;
            }
        }
        return true;
    }



}
