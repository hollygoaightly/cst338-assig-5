package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Assig5PhaseThree {
   static int NUM_CARDS_PER_HAND = 7;
   static int NUM_PLAYERS = 2;

   /**
    * booleans to track who goes first each turn
    */
   static boolean computerWin;
   static boolean humanWin;
   /**
    * UI Labels
    */
   static JLabel[] computerLabels = new JLabel[NUM_CARDS_PER_HAND];
   static JButton[] humanLabels = new JButton[NUM_CARDS_PER_HAND];
   static JLabel[] playedCardLabels = new JLabel[NUM_PLAYERS];

   /**
    * Game Managers
    */
   static CardGameFramework LowCardGame; // CardGameFramework instance
   static CardTable myCardTable;  // CardTable instance

   /**
    * CardGameFramework config
    */
   static int numPacksPerDeck;
   static int numJokersPerPack;
   static int numUnusedCardsPerPack;
   static Card[] unusedCardsPerPack;

   /**
    * Constants to keep track of Human and Computer hand indexes in CardGameFramework
    */
   static int COMPUTER_HAND_INDEX = 0;
   static int HUMAN_HAND_INDEX = 1;

   /**
    * Keep track of winnings in 2D array
    */
   static Card[][] cardWinningsPerPlayer = new Card[NUM_PLAYERS][Deck.MAX_CARDS];
   static int[] numWinningsPerPlayer = new int[NUM_PLAYERS]; // so we know index to add cards for each win

   /**
    * Keep track of which cards are in play at any time
    */
   static Card[] cardsInPlay = new Card[NUM_PLAYERS];

   public static void main(String[] args) throws InterruptedException {
      myCardTable = new CardTable("CardTable", NUM_CARDS_PER_HAND, NUM_PLAYERS);
      myCardTable.setSize(800, 600);
      myCardTable.setLocationRelativeTo(null);
      myCardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      numPacksPerDeck = 1;
      numJokersPerPack = 2;
      numUnusedCardsPerPack = 0;
      unusedCardsPerPack = null;

      LowCardGame = new CardGameFramework(
            numPacksPerDeck, numJokersPerPack,
            numUnusedCardsPerPack, unusedCardsPerPack,
            NUM_PLAYERS, NUM_CARDS_PER_HAND);

      LowCardGame.deal(); // deal to players
      renderHands();
      // show everything to the user
      myCardTable.setVisible(true);

      Thread.sleep(500); // Wait a little bit

      // game will start with computer playing first
      computerWin = true;
      humanWin = false;
      /**
       * Start Playing Game
       */
      playCards();
      /*while (LowCardGame.getNumCardsRemainingInDeck() >= 0) {


         //Thread.sleep(500); // Wait a little bit

         //handleRoundResults();

         //Thread.sleep(500); // Wait a little bit

         System.out.println("Number of cards left in deck: " + LowCardGame.getNumCardsRemainingInDeck());

         if (LowCardGame.getNumCardsRemainingInDeck() < NUM_PLAYERS) {
            // end game because we won't be able to deal enough cards
            break;
         }

         resetForNewRound();
      }

      for (int i = 0; i < cardWinningsPerPlayer.length; i++) { // go through players
         System.out.println("Player hand index: " + i + " has won " + numWinningsPerPlayer[i] + " cards.");
      }

       */
   }

   /**
    * Show a dialog with Game Results.
    */
   private static void handleEndGame() {
      String resultText = "";

      if (numWinningsPerPlayer[HUMAN_HAND_INDEX] == numWinningsPerPlayer[COMPUTER_HAND_INDEX]) {
         resultText = "You tied!";
      }
      else if (numWinningsPerPlayer[HUMAN_HAND_INDEX] > numWinningsPerPlayer[COMPUTER_HAND_INDEX]) {
         resultText = "You win!";
      } else {
         resultText = "Computer wins!";
      }

      String displayText =
            "Game is Over. Final Scores: \n" + "Computer: " + numWinningsPerPlayer[COMPUTER_HAND_INDEX] + " Cards\n"
                  + "You: " + numWinningsPerPlayer[HUMAN_HAND_INDEX] + " Cards\n" + resultText;

      JOptionPane.showMessageDialog(myCardTable, displayText, "Round Results", JOptionPane.PLAIN_MESSAGE);
   }

   /**
    * Debugging Method:
    * Print out each player's Card winnings.
    */
   private static void printPlayerWinnings(int playerIndex) {
      Card[] cardWinnings = cardWinningsPerPlayer[playerIndex];

      for (int i = 0; i < numWinningsPerPlayer[playerIndex]; i++) {
         System.out.println(cardWinnings[i].toString());
      }
   }

   /**
    * Play cards from each hand to playing area
    */
   private static void playCards() {
      JPanel playArea = myCardTable.getPnlPlayArea();
       if(computerWin)
       {
          cardsInPlay[0] = computerPlayCard();
       }
      //cardsInPlay[1] = humanPlayCard();
      //playArea.add(new JLabel("Computer", JLabel.CENTER));
      //playArea.add(new JLabel("You", JLabel.CENTER));
      playArea.revalidate();
   }

   /**
    * Reset for next round
    */
   private static void resetForNewRound() {
      myCardTable.getPnlPlayArea().removeAll();

      // Deal out new cards
      for (int i = 0; i < NUM_PLAYERS; i++) {
         LowCardGame.takeCard(i); // replenish hand
      }

      myCardTable.getPnlHumanHand().removeAll();
      myCardTable.getPnlComputerHand().removeAll();

      if(LowCardGame.getNumCardsRemainingInDeck() == 0) NUM_CARDS_PER_HAND--;
      renderHands();

      myCardTable.revalidate();
      myCardTable.repaint();
      // End Game if either player is out of cards
      System.out.println(LowCardGame.getHand(COMPUTER_HAND_INDEX).getNumCards());
      System.out.println(LowCardGame.getHand(HUMAN_HAND_INDEX).getNumCards());
      System.out.println(LowCardGame.getNumCardsRemainingInDeck());
      System.out.println("-----");
      if(LowCardGame.getHand(COMPUTER_HAND_INDEX).getNumCards() == 0  || LowCardGame.getHand(HUMAN_HAND_INDEX).getNumCards() == 0) {
         handleEndGame();
      }
      // Otherwise check if computer starts new round
      else if(computerWin) {
         cardsInPlay[0] = computerPlayCard();
      }
   }

   /**
    * Calculate and Display Results
    */
   private static void handleRoundResults(){
      String resultText = "";
      int winnerIndex = 0; // start with index: 0 as winner
      for (int i = 1; i < cardsInPlay.length; i++) {
         int cardValue = Card.getRankIndex(cardsInPlay[i].getValue());
         int currentLowest = Card.getRankIndex(cardsInPlay[winnerIndex].getValue());
         if (cardValue < currentLowest) {
            winnerIndex = i;
         } else if (cardValue == currentLowest) {
            if (cardsInPlay[i].getSuit().ordinal() < cardsInPlay[winnerIndex].getSuit().ordinal()) { // if equal, order on suit
               winnerIndex = i;
            }
         }
      }

      // save winnings
      int numCardsWon = numWinningsPerPlayer[winnerIndex];
      cardWinningsPerPlayer[winnerIndex][numCardsWon] = cardsInPlay[0]; // actually place cards in winnings
      cardWinningsPerPlayer[winnerIndex][numCardsWon + 1] = cardsInPlay[1];
      numWinningsPerPlayer[winnerIndex] += 2; // increment num of cards won

      if (winnerIndex == HUMAN_HAND_INDEX) {
         humanWin = true;
         computerWin = false;
         resultText = "You Won";
      } else {
         humanWin = false;
         computerWin = true;
         resultText = "You Lost";
      }

      JOptionPane.showMessageDialog(myCardTable,
            resultText, "Round Results",  JOptionPane.PLAIN_MESSAGE); // Display dialog with results

      myCardTable.getPnlPlayArea().revalidate();
   }

   private static void renderHands() {
      computerLabels = new JLabel[NUM_CARDS_PER_HAND];
      humanLabels = new JButton[NUM_CARDS_PER_HAND];

      // CREATE COMP LABELS ----------------------------------------------------
      for (int i = 0; i < computerLabels.length; i++) {
         computerLabels[i] = new JLabel(GUICard.getBackCardIcon());
         // ADD COMP LABELS TO PANELS -----------------------------------------
         myCardTable.getPnlComputerHand().add(computerLabels[i]);
      }

      // CREATE HUMAN LABELS ----------------------------------------------------
      for (int i = 0; i < humanLabels.length; i++) {
         // player hand should be buttons
         JButton playCardButton = new JButton(GUICard.getIcon(LowCardGame.getHand(HUMAN_HAND_INDEX).inspectCard(i)));
         playCardButton.setActionCommand(String.valueOf(i));
         playCardButton.addActionListener(new CardButtonListener());
         humanLabels[i] = playCardButton;
         // ADD HUMAN LABELS TO PANELS -----------------------------------------
         myCardTable.getPnlHumanHand().add(humanLabels[i]);
      }
   }

   private static Card computerPlayCard() {
      Hand hand = LowCardGame.getHand(COMPUTER_HAND_INDEX);
      Card cardToPlay = null;
      hand.sort();  //want to sort out the hand to get 1 card lower than humanCardToPlay
      if (playedCardLabels[HUMAN_HAND_INDEX] == null) {  //if no display on humanCard, then play at index 0.
         cardToPlay = LowCardGame.playCard(COMPUTER_HAND_INDEX, 0); //which if is sorted then would be lowest card
      }
      else {
         for(int i = hand.getNumCards() - 1; i > 0; i--) { // find highest card that is sufficient to win round
            if(cardsInPlay[HUMAN_HAND_INDEX].getValue() < hand.inspectCard(i).getValue()) {
               cardToPlay = LowCardGame.playCard(COMPUTER_HAND_INDEX, i);
               break;
            }
         }
      }

      if (cardToPlay == null) { // if still null then computer can't win round, discard highest
         cardToPlay = hand.playCard(hand.getNumCards() - 1);
      }

      playedCardLabels[COMPUTER_HAND_INDEX] = new JLabel(GUICard.getIcon(cardToPlay));
      myCardTable.getPnlPlayArea().add(playedCardLabels[COMPUTER_HAND_INDEX]);

      return cardToPlay;
   }

   private static Card humanPlayCard(int handIndex) {
      Hand hand = LowCardGame.getHand(HUMAN_HAND_INDEX);
      Card cardToPlay = LowCardGame.playCard(HUMAN_HAND_INDEX, handIndex);
      playedCardLabels[HUMAN_HAND_INDEX] = new JLabel(GUICard.getIcon(cardToPlay));
      myCardTable.getPnlPlayArea().add(playedCardLabels[HUMAN_HAND_INDEX]);

      return cardToPlay;
   }

   /**
    * Inner button listener class
    */
   private static class CardButtonListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent e) {
         int slotNumber = Integer.valueOf(e.getActionCommand()); // get slot number played
         JButton button = (JButton)e.getSource();
         cardsInPlay[1] = humanPlayCard(slotNumber);
         button.setIcon(null);
         button.setEnabled(false);
         //human is playing first this round
         if(humanWin) {
            cardsInPlay[0] = computerPlayCard();
         }
         handleRoundResults();
         resetForNewRound();
      }
   }
}

//class CardGameFramework  ----------------------------------------------------
class CardGameFramework {
   private static final int MAX_PLAYERS = 50;

   private int numPlayers;
   private int numPacks;            // # standard 52-card packs per deck
   // ignoring jokers or unused cards
   private int numJokersPerPack;    // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack;  // # cards removed from each pack
   private int numCardsPerHand;        // # cards to deal each player
   private Deck deck;               // holds the initial full deck and gets
   // smaller (usually) during play
   private Hand[] hand;             // one Hand for each player
   private Card[] unusedCardsPerPack;   // an array holding the cards not used
   // in the game.  e.g. pinochle does not
   // use cards 2-8 of any suit

   public CardGameFramework(int numPacks, int numJokersPerPack,
                            int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
                            int numPlayers, int numCardsPerHand) {
      int k;

      // filter bad values
      if (numPacks < 1 || numPacks > 6)
         numPacks = 1;
      if (numJokersPerPack < 0 || numJokersPerPack > 4)
         numJokersPerPack = 0;
      if (numUnusedCardsPerPack < 0 || numUnusedCardsPerPack > 50) //  > 1 card
         numUnusedCardsPerPack = 0;
      if (numPlayers < 1 || numPlayers > MAX_PLAYERS)
         numPlayers = 4;
      // one of many ways to assure at least one full deal to all players
      if (numCardsPerHand < 1 ||
            numCardsPerHand > numPacks * (52 - numUnusedCardsPerPack)
                  / numPlayers)
         numCardsPerHand = numPacks * (52 - numUnusedCardsPerPack) / numPlayers;

      // allocate
      this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
      this.hand = new Hand[numPlayers];
      for (k = 0; k < numPlayers; k++)
         this.hand[k] = new Hand();
      deck = new Deck(numPacks);

      // assign to members
      this.numPacks = numPacks;
      this.numJokersPerPack = numJokersPerPack;
      this.numUnusedCardsPerPack = numUnusedCardsPerPack;
      this.numPlayers = numPlayers;
      this.numCardsPerHand = numCardsPerHand;
      for (k = 0; k < numUnusedCardsPerPack; k++)
         this.unusedCardsPerPack[k] = unusedCardsPerPack[k];

      // prepare deck and shuffle
      newGame();
   }

   // constructor overload/default for game like bridge
   public CardGameFramework() {
      this(1, 0, 0, null, 4, 13);
   }

   public Hand getHand(int k) {
      // hands start from 0 like arrays

      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers)
         return new Hand();

      return hand[k];
   }

   public Card getCardFromDeck() {
      return deck.dealCard();
   }

   public int getNumCardsRemainingInDeck() {
      return deck.getNumCards();
   }

   public void newGame() {
      int k, j;

      // clear the hands
      for (k = 0; k < numPlayers; k++)
         hand[k].resetHand();

      // restock the deck
      deck.init(numPacks);

      // remove unused cards
      for (k = 0; k < numUnusedCardsPerPack; k++)
         deck.removeCard(unusedCardsPerPack[k]);

      // add jokers
      for (k = 0; k < numPacks; k++)
         for (j = 0; j < numJokersPerPack; j++)
            deck.addCard(new Card('X', Card.Suit.values()[j]));

      // shuffle the cards
      deck.shuffle();
   }

   public boolean deal() {
      // returns false if not enough cards, but deals what it can
      int k, j;
      boolean enoughCards;

      // clear all hands
      for (j = 0; j < numPlayers; j++)
         hand[j].resetHand();

      enoughCards = true;
      for (k = 0; k < numCardsPerHand && enoughCards; k++) {
         for (j = 0; j < numPlayers; j++)
            if (deck.getNumCards() > 0)
               hand[j].takeCard(deck.dealCard());
            else {
               enoughCards = false;
               break;
            }
      }

      return enoughCards;
   }

   void sortHands() {
      int k;

      for (k = 0; k < numPlayers; k++)
         hand[k].sort();
   }

   Card playCard(int playerIndex, int cardIndex) {
      // returns bad card if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 ||
            cardIndex < 0 || cardIndex > numCardsPerHand - 1) {
         //Creates a card that does not work
         return new Card('M', Card.Suit.SPADES);
      }

      // return the card played
      return hand[playerIndex].playCard(cardIndex);

   }

   boolean takeCard(int playerIndex) {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0)
         return false;

      return hand[playerIndex].takeCard(deck.dealCard());
   }

}
