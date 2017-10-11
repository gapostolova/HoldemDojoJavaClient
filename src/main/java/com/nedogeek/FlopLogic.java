package com.nedogeek;

import java.util.*;

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
    public static boolean dangerousFlop(List<Client.Card> deskCards, int numberOfsequentialCards){
        List<String> cardValue = new ArrayList<>();
        List<String> cardsSuit = new ArrayList<>();
        for(Client.Card card : deskCards){
            cardValue.add(card.getValue());
            cardsSuit.add(card.suit);
        }
        if(cardValue.size() > numberOfsequentialCards){
            return false;
        }

        if(sameSuit(cardsSuit) || sequentialCards(cardValue) == numberOfsequentialCards){
            return false;
        }
        return true;
    }

    //returns sorted list of sequential card indexes
    public static int sequentialCards(List<String> cards){
        boolean isSequential = false;
        int countConsequalCards = 0;
        ArrayList<Integer> sequentialCardIndexes = new ArrayList<>();

        for(int i = 0; i < cards.size()-1; i++){
            int card1Index = cardsOrder.indexOf(cards.get(i));

            // System.out.println("i: "+i+"\ncard 1: " + card1Index);

            for(int j = i+1; j < cards.size(); j++){
                int card2Index = cardsOrder.indexOf(cards.get(j));

                //    System.out.println("card 2: " + card2Index);

                    if(card1Index == card2Index) {
                        continue;
                    }

                    else if(card1Index == 0 || card2Index == 0) {
                        if (card1Index == 0) {
                            card1Index = aceCalculation(card1Index, card2Index);
                        }
                        else if(card2Index == 0) {
                            card2Index = aceCalculation(card1Index, card2Index);
                        }
                        if(!sequentialCardIndexes.contains(card1Index) && card1Index != -1) {
                            sequentialCardIndexes.add(card1Index);
                        }
                        if(!sequentialCardIndexes.contains(card2Index) && card2Index != -1) {
                            sequentialCardIndexes.add(card2Index);
                        }
                    }

                  else  if(card1Index-card1Index == 1 || card1Index-card2Index == -1) {
                         if(!sequentialCardIndexes.contains(card1Index)) {
                            sequentialCardIndexes.add(card1Index);
                        }
                        if(!sequentialCardIndexes.contains(card2Index)) {
                            sequentialCardIndexes.add(card2Index);
                        }

                    }

                }
            }


        Collections.sort(sequentialCardIndexes);
        System.out.println("Sequential Card Indexes: ");
        System.out.println(sequentialCardIndexes);
            return sequentialListOfIndexes(sequentialCardIndexes).size();
        }

        // parameter of this method is list of card indexes, it returns the longest sequence of card indexes, as an arrayList
    public static ArrayList<Integer> longestSequentialCardIndexes(ArrayList<Integer> cardIntexes){
        ArrayList<ArrayList<Integer>> matrixOfIndexes = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;
        for(int i = 0; i < cardIntexes.size()-1; i ++){

             if(i == cardIntexes.size()-2 || cardIntexes.get(i+1) - cardIntexes.get(i) != 1){
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(startIndex);
                if(i == cardIntexes.size()-2){
                    endIndex= cardIntexes.size()-1;
                }
                temp.add(endIndex);
                temp.add(endIndex-startIndex+1);

                matrixOfIndexes.add(temp);
                startIndex = i+1;
            }
            else  if(cardIntexes.get(i+1) - cardIntexes.get(i) == 1){
                endIndex = i+1;
            }


        }

        ArrayList<Integer> biggestSequel = new ArrayList<>();
        ArrayList<Integer> biggestSequelIndexes = biggestSequence(matrixOfIndexes);
        if(!biggestSequelIndexes.isEmpty()) {
            for (int i = biggestSequelIndexes.get(0); i <= biggestSequelIndexes.get(1); i++) {
                biggestSequel.add(cardIntexes.get(i));
            }
        }
        return biggestSequel;
    }


    //calculates on which indexes the cards have greater sequence  { (1,3,2) } -> {(startIdx, endIdx, startIdx-endIdx+1)}
    private static ArrayList<Integer> biggestSequence(ArrayList<ArrayList<Integer>> values){
        int maxElement = 0;
        int index = -1;


        for(int i = 0; i < values.size(); i ++){
            int startEndCalculation = values.get(i).get(2);
            if(startEndCalculation == 1){
                continue;
            }
            if(startEndCalculation > maxElement){
                maxElement = startEndCalculation;
                index = i;
            }
        }

        if(index == -1){
            return new ArrayList<>();
        }
        return values.get(index);
    }


    private static ArrayList<Integer> sequentialListOfIndexes(ArrayList<Integer> cardIndexes){
        Collections.sort(cardIndexes);
        ArrayList<Integer> newList = new ArrayList<>();

        for(int i = 0; i < cardIndexes.size()-1; i++){
            int cardIndexCalculation = cardIndexes.get(i) - cardIndexes.get(i+1);
            if(cardIndexCalculation == 1 || cardIndexCalculation == -1){
                if(!newList.contains(cardIndexes.get(i))) {
                    newList.add(cardIndexes.get(i));
                }
                if(!newList.contains(cardIndexes.get(i+1))) {
                    newList.add(cardIndexes.get(i + 1));
                }
            }
        }

        System.out.println("****************************");
        System.out.println(longestSequentialCardIndexes(newList));
        return longestSequentialCardIndexes(newList);

    }

    //returns true if ace in the sequence
    private static int aceCalculation(int card1, int card2){
        int aceAtBeginning = card1 - card2;
        if(card1 == 0 ) {
            int aceAtEnd = cardsOrder.size()-1 - card2;
            if (aceAtBeginning == 1 || aceAtBeginning == -1) {
                return 0;
            }
            else if(aceAtEnd == 1){
                return cardsOrder.size()-1;
            }
        }
        if(card2 == 0 ) {
            int aceAtEnd = cardsOrder.size()-1 - card1;
            if (aceAtBeginning == 1 || aceAtBeginning == -1) {
                return 0;
            }
            else if(aceAtEnd == 1 || aceAtEnd == -1){
                return cardsOrder.size()-1;
            }
        }
        return -1;
    }

    public static boolean sameSuit(List<String> cards){
        String suit = cards.get(0);
        for(String card : cards){
            if(!suit.equalsIgnoreCase(card)){
                return false;
            }
        }
        return true;
    }


}
