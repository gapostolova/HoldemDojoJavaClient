package com.nedogeek;

import java.util.List;

public class FlopLogic {


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


}
