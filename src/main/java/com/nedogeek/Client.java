package com.nedogeek;


import org.eclipse.jetty.websocket.*;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.plugin.com.event.COMEventHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Client {


    private static List<String> cardCombinations = Arrays.asList(
            "pair",
            "two pairs",
            "set",
            "straight",
            "flash",
            "full house",
            "four of",
            "straight flash",
            "royal flash"
    );

    public static List<String> getCardCombinations() {
        return cardCombinations;
    }

    private static final String userName = "RonaldoCR7";
    private static final String password = "ronaldo2";


    private static final String SERVER = "ws://10.22.40.111:8080/ws";
    private org.eclipse.jetty.websocket.WebSocket.Connection connection;

    private static List<Client> gameHistory;
    private static boolean iHaveRaised = false;
    private static String currRound = "";

    List<Card> deskCards;

    int pot;
    String gameRound;

    String dealer;
    String mover;
    List<String> event;
    List<Player> players;
    Player myPlayer;

    String cardCombination;
    private static int roundCount;
    private static String round = "";

    private List<Player> getPlayers() {
        return players;
    }

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
//                System.out.println("Opened");
                }

                public void onClose(int closeCode, String message) {
//                System.out.println("Closed");
                }

                public void onMessage(String data) {
                    parseMessage(data);

                System.out.println( "#############################################################################################################################################################################################################");
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

        private final String name;
        private final int balance;
        private final int bet;
        private final String status;
        private final List<Card> cards;

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

        private int getBet() {
            return bet;
        }

        private String getStatus() {
            return status;
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

//        if(round.equalsIgnoreCase("") || !round.equalsIgnoreCase(gameRound)){
//            roundCount = 0;
//            round = gameRound;
//        }


//        if(!event.get(0).trim().equalsIgnoreCase("game ended")) {
//            //does not save the last round where winner is pronounced
//            roundCount++;
//            gameHistory.add(new Client(deskCards, pot, gameRound, dealer, mover, event, players, cardCombination));
//        }
//        else {
//            gameHistory = new ArrayList<>();
//        }

        if(myPlayer.getStatus().equalsIgnoreCase("rise")){
            iHaveRaised = true;
        }
        if(currRound.equalsIgnoreCase("") || !currRound.equalsIgnoreCase(gameRound)){
            currRound = gameRound;
            iHaveRaised = false;
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

            Player player = new Player(name, balance, bet, status, cards);
            players.add(player);

            if (name.equalsIgnoreCase(userName)) {
                myPlayer = player;
            }
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
        Card card1 = myPlayer.getCards().get(0);
        Card card2 = myPlayer.getCards().get(1);

        System.out.println(card1.getValue() + card1.getSuit() + " " + card2.getValue() + card2.getSuit());
        System.out.println();

//        System.out.println(hasMadeBetPreviousRound(""));

        if (gameRound.equalsIgnoreCase("blind")) {
            //if there is no bet before us play 22+, Ax+, Strait chance, flush chance and bet 100
            int riseAmount = 0;
            boolean someoneAllIn = false;
            for (Player player : players) {
                if (player.getBet() > riseAmount && player.status.equalsIgnoreCase("rise")) {
                    riseAmount = player.getBet();
                }
                if (player.getStatus().equalsIgnoreCase("allin") || player.getBalance() < 100) {
                    someoneAllIn = true;
                }
            }
            //if there is no raise before us, we bet Ax+, KJ+, 22+, two from one suit and two strong sequential cards (QJ+)
            if (riseAmount == 0 && (PreFlopLogic.hasAce(card1, card2) || PreFlopLogic.hasKingAndStrongCard(card1, card2, "J") || PreFlopLogic.hasPair(card1, card2) ||
                    (PreFlopLogic.hasChanceForStraight(card1, card2) && PreFlopLogic.hasCardsStrongerThan(card1, card2, "J")) ||
                    PreFlopLogic.hasPairSuit(card1, card2))) {
                rise(100);
            }
            //if there is bet check if it's a "all-in" play "all-in" also with QQ+, AK
            else if (someoneAllIn && PreFlopLogic.hasHandStrongerThan(card1, card2, "Q")) {
                allIn();
            }
            //get bet value, if bet value is less than 20% from your balance raise AQ+, TT+
            else if (myPlayer.getBalance() / 5 >= riseAmount && (PreFlopLogic.hasHandStrongerThan(card1, card2, "10") || PreFlopLogic.hasAceAndStrongCard(card1, card2, "Q"))) {
                Random r = new Random();
                int randomRise = r.nextInt(3) + 1;
               rise(riseAmount * randomRise);
            }
            else if(myPlayer.getBalance() / 5 >= riseAmount && (PreFlopLogic.hasAceAndStrongCard(card1, card2, "J") || PreFlopLogic.hasKingAndStrongCard(card1, card2, "Q") ||
                    PreFlopLogic.hasHandStrongerThan(card1, card2, "9") || (PreFlopLogic.hasCardsStrongerThan(card1, card2, "8") && PreFlopLogic.hasPairSuit(card1, card2)))){
                call();
            }
            // get bet value, if bet value is more than 20% from your balance play "all-in" with QQ+, AK
            else if (myPlayer.getBalance() / 5 < riseAmount && (PreFlopLogic.hasHandStrongerThan(card1, card2, "Q") || PreFlopLogic.hasAceAndStrongCard(card1, card2, "K"))) {
               allIn();
            }
        }

        else if (gameRound.equalsIgnoreCase("three_cards")) {
            int riseAmount = 0;
            boolean someoneHasRaised = false;
            for (Player player : players) {
                if (player.getBet() - 20 > riseAmount && player.status.equalsIgnoreCase("rise")) {
                    riseAmount = player.getBet();
                    someoneHasRaised = true;
                }
            }
            boolean hasPairOnBoard = hasPairOnBoard();

            int handPower = FlopLogic.getCombinationPower(cardCombination);

            //TODO: if hand is set or stronger, check if there is raise before us call, if there is no raise bet half pot
            if (handPower > 2) {
                //TODO: extract method from this code: check if there is raise before us call, if there is no raise bet half pot
                if(someoneHasRaised){
                    call();
                }
                else{
                    rise(pot * 0.5);;
                }
            }

            //TODO: if there is pair on board and we have handPower = 2 then play hand like we have only one pair, if we have handPower = 1 then check/fold
            else if(handPower == 2 && hasPairOnBoard){
                playPair(hasPairOnBoard);
            }

            //TODO: if there is no pair on the board, handPower = 2 plays like handPower>2; handPower = 1 then play it like it ;D
            else if(handPower == 2 && !hasPairOnBoard){
                if(someoneHasRaised){
                    call();
                }
                else{
                    rise(pot * 0.5);
                }
            }

            else if(handPower == 1 && hasPairOnBoard){
                check();
            }

            else if(handPower == 1 && !hasPairOnBoard){
                playPair(hasPairOnBoard);
            }


            //TODO: if has a pair (handPower =1) check if it's with the strongest card on the flop or above(pocket pair) and flop is not dangerous*(3 cards same suit, 3 sequential cards) , if true -> rise (if there isn't rise before you, call if there is rise already and rise is smaller than 1/2 pot)
            //TODO: if cards on the flop are dangerous then check/fold
            //TODO: if pair is lower than high card on the board check or fold

            else {
                //TODO check straight and flush chance
                check();
            }

            //TODO dangerous*
            //TODO: 3 cards same suit - check
            //TODO: if straight or flush chance, if there is raise, check if raise is < 2/3 of the pot call , else fold, if there isn't raise -> check
            //TODO:
            //TODO:
        }
        else if(gameRound.equalsIgnoreCase("four_cards")) {
//            System.out.println("TURN");
            this.makeTurnMove();
        }

        else if(gameRound.equalsIgnoreCase("five_cards")) {

            riverLogic();

        }

    }





    private void riverLogic() throws IOException {
        // Check if handpower > 2, if true -> check if there is raiser before us if true -> check if flop is dangerous(4 cards same suit, 4 sequential cards), if is dangerous then call, else if not dangerous then raise 1/2 * pot, (*)
        // (*) if nobody has raised then raise 1/2 * pot,

        int handPower = FlopLogic.getCombinationPower(cardCombination);
        if(handPower > 2){
            //TODO dont know if rise amount should be half of the pot or > 0
            if(getRiseAmount() > 0){
                if(FlopLogic.dangerousFlop(deskCards, 4)){
                    call();
                }
                else{
                    rise(0.5*pot);
                }
            }
            else{
               rise(0.5*pot);
            }
        }
    }




    private int getRiseAmount(){
        int riseAmount = 0;
        for (Player player : players) {
            if (player.getBet() - 20 > riseAmount && player.status.equalsIgnoreCase("rise")) {
                riseAmount = player.getBet();
            }
        }

        return riseAmount;
    }

    private boolean hasPairOnBoard(){
        for(int i = 0; i < deskCards.size() - 1; i++){
            Card card1 = deskCards.get(i);
            for(int j = i + 1; j < deskCards.size(); j++){
                Card card2 = deskCards.get(j);
                if(card1.getValue().equalsIgnoreCase(card2.getValue())){
                    return true;
                }
            }
        }
        return false;
    }

    private String getMyPlayerPairValue(){
        Card myPlayerCard1 = myPlayer.getCards().get(0);
        Card myPlayerCard2 = myPlayer.getCards().get(1);

        String myPlayerPairValue = "";
        if(myPlayerCard1.getValue().equalsIgnoreCase(myPlayerCard2.getValue())){
            myPlayerPairValue = myPlayerCard1.getValue();
        }
        else{
            for(Card currentDeskCard : deskCards){
                if(myPlayerCard1.getValue().equalsIgnoreCase(currentDeskCard.getValue())){
                    myPlayerPairValue = myPlayerCard1.getValue();
                    break;
                }
                if(myPlayerCard2.getValue().equalsIgnoreCase(currentDeskCard.getValue())){
                    myPlayerPairValue = myPlayerCard1.getValue();
                    break;
                }
            }
        }

        return myPlayerPairValue;
    }

    private void playPair(boolean hasPairOnBoard) throws IOException {
        String myPlayerPairValue = getMyPlayerPairValue();

        int foldCounter = 0;
        for(Player currentPlayer : players){
            if(currentPlayer.getStatus().equalsIgnoreCase("fold")){
                foldCounter++;
            }
        }
        int notFoldedPlayers = players.size() - foldCounter;

        Map<String, Integer> risersAndCallers = countRaisersAndCallers();
        if(hasPairOnBoard){
            if(risersAndCallers.get("risers") == 0 && risersAndCallers.get("allIn") == 0){
                rise((pot * 0.5));;

            }
            else{
               check();
            }
        }
        else{
            if(hasStrongestPairOnBoard(myPlayerPairValue)){
                if(risersAndCallers.get("risers") == 0 && risersAndCallers.get("allIn") == 0){
                    rise(pot * 0.5);
                }
                else if(risersAndCallers.get("risers") > 1
                        || risersAndCallers.get("allIn") > 1
                        || (risersAndCallers.get("risers") == 1) && (risersAndCallers.get("callers") > 0)){
                    fold();
                }
                else if(getRiseAmount() > pot || FlopLogic.dangerousFlop(deskCards, 3)){
                    fold();
                }
                else if(getRiseAmount() <= pot && !FlopLogic.dangerousFlop(deskCards, 3)){
                    call();
                }
                else{
                   fold();
                }
            }
        }
    }



    private boolean hasStrongestPairOnBoard(String myPlayerPairValue){
        int myPlayerPairValueIndex = PreFlopLogic.cardsOrder.indexOf(myPlayerPairValue);

        int strongestCardOnBoardIndex = 0;
        for(Card currentCard : deskCards){
            if(PreFlopLogic.cardsOrder.indexOf(currentCard.getValue()) > strongestCardOnBoardIndex){
                strongestCardOnBoardIndex = PreFlopLogic.cardsOrder.indexOf(currentCard.getValue());
            }
        }

        return myPlayerPairValueIndex >= strongestCardOnBoardIndex;
    }

    private boolean checkForPairOnBoard(){
        for(int i=0; i<this.deskCards.size()-1; i++)
        {
            Card currentCard = this.deskCards.get(i);
            for(int j=1; j<this.deskCards.size(); j++)
            if(currentCard.value == this.deskCards.get(j).value){
                return true;
            }
        }

        return false;
    }

    //count people that have been played before us
    private Map<String,Integer> countRaisersAndCallers(){
        Map<String,Integer> results = new HashMap<String, Integer>();

        int risers = 0;
        int callers = 0; //people that made call after rise of another person
        int allIn = 0;
        for (Player player : this.players) {
            if (player.getStatus().equalsIgnoreCase("rise") && player.getBet() > 0) {
                risers++;
            }
            else if(player.status.equalsIgnoreCase("call") && risers>0){
                callers++;
            }
            else if (player.getStatus().equalsIgnoreCase("allin")){
                allIn++;
            }
        }

        results.put("risers", risers);
        results.put("callers", callers);
        results.put("allIn", allIn);

        return results;
    }

    private void makeTurnMove() throws IOException{
        int handPower = FlopLogic.getCombinationPower(cardCombination);
        Map<String, Integer> actions = this.countRaisersAndCallers();
        int risers = actions.get("risers");
        int callers = actions.get("callers");
        int allIn = actions.get("allIn");
        String command = Commands.Fold.toString();

//        System.out.println("Hand " + handPower);
//        System.out.println("Is there pair on the board " +  this.checkForPairOnBoard());
//        System.out.println("risers " + risers + ", callers " + risers + ", allIn " + risers);
//        System.out.println("Board " + deskCards);

        if(handPower == 0 || (handPower == 1 && this.checkForPairOnBoard())){
            if(risers == 0 && callers == 0 && allIn == 0 ){
                command =Commands.Check.toString();
            }
        }
        //TODO: add check if pair is with the biggest card on the board or stronger
        else if ((handPower == 1 && !this.checkForPairOnBoard()) || (handPower == 2 && this.checkForPairOnBoard())){
            if(risers == 0 && callers == 0 && allIn == 0 ) {
                command = Commands.Rise.toString()+ "," + (0.5*pot);
            }
            else if(risers == 1 && callers == 0 && allIn == 0) {
                command = Commands.Call.toString();
            }
        }
        else if(handPower > 1) {
            if(risers == 0 && callers == 0 && allIn == 0 ){
                command =Commands.Rise.toString()+ "," + (0.5*pot);
            }
            else{
                command =Commands.AllIn.toString();
            }
        }

//        System.out.println("Command: " + command);
        connection.sendMessage(command);
    }

    private void doAnswer2(String message) throws IOException {
        //check if someone has already rose
        //if true: check balance rise ratio
    }


    //returns true if any player in the previous 2 orbits has had status "rise" or "all in" and the money he bet were more than 0
    //any of the players including me
    //if username is equal to "" then the method works for all users
    //if username is of a player then it returns if that player has had status "rise" or "all in"
    public Boolean hasMadeBetPreviousRound(String username) {
        int numberOfPlayers = players.size();
        //round count is the number of saves made in gameHistory list for the current round
        for (int i = 0; i < roundCount; i++) {
            int indexOfSave = gameHistory.size() - 1 - i;

            //if this index exists in the list
            if (gameHistory.size() > 0 && indexOfSave > 0) {
                //check if he has made a bet
                if (hasRose(gameHistory.get(indexOfSave).getPlayers(), username)) {
                    return true;
                }
            }
        }
        return false;
    }

    //TODO all in and rise in diff methods
    private boolean hasRose(List<Player> players, String username) {
//        System.out.println("\n has rose \n");
        for (Player player : players) {
            if (username.equalsIgnoreCase("")) {
                if (player.getBet() > 0 && player.status.equalsIgnoreCase("rise")) {
//                    System.out.println(player);
                    return true;
                } else {
                    if (player.name.equalsIgnoreCase(username) && player.getBet() > 0 && player.status.equalsIgnoreCase("rise")) {
//                        System.out.println(player);
                        return true;
                    }
                }

            }
        }
        return false;
    }



    public void call() throws IOException {
        connection.sendMessage(Commands.Call.toString());
    }

    public void rise(double amount) throws IOException {
        connection.sendMessage(Commands.Rise.toString() + "," + amount);
    }

    public void allIn() throws IOException {
        connection.sendMessage(Commands.AllIn.toString());
    }

    public void check() throws IOException {
        connection.sendMessage(Commands.Check.toString());
    }

    public void fold() throws IOException {
        connection.sendMessage(Commands.Fold.toString());
    }

}
