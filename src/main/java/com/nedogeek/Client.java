package com.nedogeek;


import org.eclipse.jetty.websocket.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Client {
    private static final String userName = "Pesho";
    private static final String password = "somePassword";



    private static final String SERVER = "ws://10.22.40.137:8080/ws";

    private org.eclipse.jetty.websocket.WebSocket.Connection connection;

    private static List<Client> gameHistory;

    List<Card> deskCards;

    int pot;
    String gameRound;

    String dealer;
    String mover;
    List<String> event;
    List<Player> players;

    String cardCombination;

    enum Commands {
        Check, Call, Rise, Fold, AllIn
    }

    class Card {
        final String suit;
        final String value;

        Card(String suit, String value) {
            this.suit = suit;
            this.value = value;
        }

        public String getSuit() {
            return suit;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Card{" +
                    "suit='" + suit + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }


    private void con() {
        WebSocketClientFactory factory = new WebSocketClientFactory();
        try {
            factory.start();

            WebSocketClient client = factory.newWebSocketClient();

            connection = client.open(new URI(SERVER + "?user=" + userName + "&password=" + password), new WebSocket.OnTextMessage() {
                public void onOpen(Connection connection) {
                    System.out.println("Opened");
                }

                public void onClose(int closeCode, String message) {
                    System.out.println("Closed");
                }

                public void onMessage(String data) {
                    parseMessage(data);

                    //  System.out.println( "#############################################################################################################################################################################################################");
                    System.out.println(data+"\n");

                    if (userName.equals(mover)) {
                        try {
                            doAnswer(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).get(500, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        public String getName() {
            return name;
        }

        public List<Card> getCards() {
            return cards;
        }

        public int getBalance() {
            return balance;
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

    public Client() {
        this.gameHistory = new ArrayList<>();
        con();
    }

    public Client(List<Card> deskCards, int pot, String gameRound, String dealer, String mover, List<String> event, List<Player> players, String cardCombination) {
        this.deskCards = deskCards;
        this.pot = pot;
        this.gameRound = gameRound;
        this.dealer = dealer;
        this.mover = mover;
        this.event = event;
        this.players = players;
        this.cardCombination = cardCombination;
    }

    @Override
    public String toString() {
        return "Client{" +
                "deskCards=" + deskCards +
                ", pot=" + pot +
                ", gameRound='" + gameRound + '\'' +
                ", dealer='" + dealer + '\'' +
                ", mover='" + mover + '\'' +
                ", event=" + event +
                ", players=" + players +
                ", cardCombination='" + cardCombination + '\'' +
                '}';
    }

    public static void main(String[] args) {
        new Client();
    }

    private void parseMessage(String message) {
        JSONObject json = new JSONObject(message);

        if (json.has("deskPot")) {
            pot = json.getInt("deskPot");
        }
        if (json.has("mover")) {
            mover = json.getString("mover");
        }
        if (json.has("dealer")) {
            dealer = json.getString("dealer");
        }
        if (json.has("gameRound")) {
            gameRound = json.getString("gameRound");
        }
        if (json.has("event")) {
            event = parseEvent(json.getJSONArray("event"));
        }
        if (json.has("players")) {
            players = parsePlayers(json.getJSONArray("players"));
        }

        if (json.has("deskCards")) {
            deskCards = parseCards(((JSONArray) json.get("deskCards")));
        }

        if (json.has("combination")) {
            cardCombination = json.getString("combination");
        }
        if(!event.get(0).trim().equalsIgnoreCase("game ended")) {
            //does not save the last round where winner is pronounced
            gameHistory.add(new Client(deskCards, pot, gameRound, dealer, mover, event, players, cardCombination));
        }
        else {
            gameHistory = new ArrayList<>();
        }

    }

    private List<String> parseEvent(JSONArray eventJSON) {
        List<String> events = new ArrayList<>();

        for (int i = 0; i < eventJSON.length(); i++) {
            events.add(eventJSON.getString(i));
        }

        return events;
    }

    private List<Player> parsePlayers(JSONArray playersJSON) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playersJSON.length(); i++) {
            JSONObject playerJSON = (JSONObject) playersJSON.get(i);
            int balance = 0;
            int bet = 0;
            String status = "";
            String name = "";
            List<Card> cards = new ArrayList<>();

            if (playerJSON.has("balance")) {
                balance = playerJSON.getInt("balance");
            }
            if (playerJSON.has("pot")) {
                bet = playerJSON.getInt("pot");
            }
            if (playerJSON.has("status")) {
                status = playerJSON.getString("status");
            }
            if (playerJSON.has("name")) {
                name = playerJSON.getString("name");
            }
            if (playerJSON.has("cards")) {
                cards = parseCards((JSONArray) playerJSON.get("cards"));
            }

            players.add(new Player(name, balance, bet, status, cards));
        }

        return players;
    }

    private List<Card> parseCards(JSONArray cardsJSON) {
        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < cardsJSON.length(); i++) {
            String cardSuit = ((JSONObject) cardsJSON.get(i)).getString("cardSuit");
            String cardValue = ((JSONObject) cardsJSON.get(i)).getString("cardValue");

            cards.add(new Card(cardSuit, cardValue));
        }

        return cards;
    }

    private void doAnswer(String message) throws IOException {
//        connection.sendMessage(Commands.AllIn.toString());
//        JSONObject json = new JSONObject(message);
//        JSONArray players = json.getJSONArray("players");
//
//        JSONObject myPlayer = new JSONObject();
//        for (int i = 0; i < players.length(); i++){
//            JSONObject currentPlayer = new JSONObject(players.get(i).toString());
//            if(currentPlayer.get("name").equals(mover)){
//                myPlayer = currentPlayer;
//                break;
//            }
//        }
//        JSONArray myPlayerCards = new JSONArray(myPlayer.get("cards").toString());


        Player myPlayer = null;
        for(Player currentPlayer : players){
            if(currentPlayer.getName().equalsIgnoreCase(userName)){
                myPlayer = currentPlayer;
                break;
            }
        }

        Card card1 = myPlayer.getCards().get(0);
        Card card2 = myPlayer.getCards().get(1);

        System.out.println(cardCombination);

        if(gameRound.equalsIgnoreCase("blind")){

        }

        if(card1.getValue().equalsIgnoreCase(card2.getValue())){
//            connection.sendMessage(Commands.Rise.toString() + ",100");
            connection.sendMessage(Commands.AllIn.toString());
        }
        else if(card1.getSuit().equalsIgnoreCase(card2.getSuit())){
            connection.sendMessage(Commands.Rise.toString() + ",100");
        }
        else if(card1.getValue().charAt(0) > '9' || card2.getValue().charAt(0) > '9'){
            connection.sendMessage(Commands.Rise.toString() + ",50");
        }
        else if(card2.getValue().charAt(0) > '9' || card2.getValue().charAt(0) > '9'){
            connection.sendMessage(Commands.Rise.toString() + ",50");
        }
        else{
            connection.sendMessage(Commands.Check.toString());
        }

    }
}