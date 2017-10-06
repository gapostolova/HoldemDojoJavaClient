package com.nedogeek;


import org.eclipse.jetty.websocket.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Client {
    private static final String userName = "Vanko1";
    private static final String password = "somePassword";

   // private static final String SERVER = "ws://10.22.41.132:8080/ws";
    private static final String SERVER = "ws://10.22.40.137:8080/ws";

    private org.eclipse.jetty.websocket.WebSocket.Connection connection;

    private static List<Client> gameHistory;
    private ArrayList<GameSimple> simpleGameHistory;

    List<Card> deskCards;

    int pot;
    String gameRound;

    String dealer;
    String mover;
    List<String> event;
    List<Player> players;

    String cardCombination;

    private List<String> getEvent(){
        return event;
    }

    private String getMover(){
        return  mover;
    }


    enum Commands {
        Check, Call, Rise, Fold, AllIn
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
                        doAnswer();
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




    public Client() {
        this.gameHistory = new ArrayList<>();
        this.simpleGameHistory = new ArrayList<>();
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
//            System.out.println("#############################################################################################################################################################################################################");
//            System.out.println(simpleGameHistory.toString());
//            System.out.println("#############################################################################################################################################################################################################");

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
         //   simpleGameHistory.add(new GameSimple(name, status, bet));
        }

        return players;
    }

    //contains all of the data for every player in every turn
    class GameSimple{

        private String player;
        private String status;
        private int rase;

        GameSimple(String player, String status, int rase){
            this.player = player;
            this.rase = rase;
            this.status = status;

        }

        @Override
        public String toString() {
            return "GameSimple{" +
                    "player='" + player + '\'' +
                    ", status='" + status + '\'' +
                    ", rase=" + rase +
                    '}';
        }

    }
//    private HashMap previousPlayersBehaviour(){
//        HashMap<String, GameSimple> game = new HashMap<>();
//        for(Client client : gameHistory){
//            String playerName = client.getMover();
//        //    GameSimple gameSimple = new GameSimple(client.getMover(), client.)
//        }
//    }

    private List<Card> parseCards(JSONArray cardsJSON) {
        List<Card> cards = new ArrayList<>();

        for (int i = 0; i < cardsJSON.length(); i++) {
            String cardSuit = ((JSONObject) cardsJSON.get(i)).getString("cardSuit");
            String cardValue = ((JSONObject) cardsJSON.get(i)).getString("cardValue");

            cards.add(new Card(cardSuit, cardValue));
        }

        return cards;
    }

    private void doAnswer() throws IOException {

        if(cardCombination.toLowerCase().contains("Straight flash".toLowerCase())){
            connection.sendMessage(Commands.AllIn.toString());
        }

        double a = Math.random();
        if(a>5 && a<10){
            connection.sendMessage(Commands.Fold.toString());
        }
        else
            if(a>10 && a <30){
                connection.sendMessage(Commands.Call.toString());
            }
            else if(a>30 && a<90) {
                connection.sendMessage(Commands.Check.toString());
            }

        else
                if(a<90 && a > 50 ){
        connection.sendMessage(Commands.Rise.toString());
    }

    }
}
