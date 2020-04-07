package com.company;

import javax.swing.*;
import java.util.HashMap;

public class Assig5PhaseThree {
   static int NUM_CARDS_PER_HAND = 7;
   static int NUM_PLAYERS = 2;

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
    * Keep track of winnings
    */
   static int[] winningsByHandArray = new int[NUM_PLAYERS];

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

      /**
       * Start Playing Game
       */
      while (LowCardGame.getNumCardsRemainingInDeck() >= 0) {
         playCards();

         Thread.sleep(500); // Wait a little bit

         handleRoundResults();

         Thread.sleep(500); // Wait a little bit

         System.out.println("Number of cards left in deck: " + LowCardGame.getNumCardsRemainingInDeck());

         if (LowCardGame.getNumCardsRemainingInDeck() < NUM_PLAYERS) {
            // end game because we won't be able to deal enough cards
            break;
         }

         resetForNewRound();
      }

      for (int i = 0; i < winningsByHandArray.length; i++) {
         System.out.println("Player hand index: " + i + " has won " + winningsByHandArray[i] + " times.");
      }
   }

   /**
    * Play cards from each hand to playing area
    */
   private static void playCards() {
      JPanel playArea = myCardTable.getPnlPlayArea();

      cardsInPlay[0] = computerPlayCard();
      cardsInPlay[1] = humanPlayCard();

      playArea.add(new JLabel("Computer", JLabel.CENTER));
      playArea.add(new JLabel("You", JLabel.CENTER));
      playArea.revalidate();
   }

   /**
    * Reset for next round
    */
   private static void resetForNewRound() {
      myCardTable.getPnlPlayArea().removeAll();

      // Deal out new cards
      for (int i = 0; i < NUM_PLAYERS; i++) {
         Card cardDealt = LowCardGame.getCardFromDeck();
         LowCardGame.getHand(i).takeCard(cardDealt); // replenish hands
      }

      myCardTable.getPnlHumanHand().removeAll();
      myCardTable.getPnlComputerHand().removeAll();

      renderHands();

      myCardTable.revalidate();
      myCardTable.repaint();
   }

   /**
    * Calculate and Display Results
    */
   private static void handleRoundResults(){
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
      winningsByHandArray[winnerIndex] += 2; // increment 2 for winning card
      if (winnerIndex == HUMAN_HAND_INDEX) {
         myCardTable.getPnlPlayArea().add(new JLabel("You Won"), JLabel.CENTER);
      } else {
         myCardTable.getPnlPlayArea().add(new JLabel("You Lost"), JLabel.CENTER);
      }

      myCardTable.getPnlPlayArea().revalidate();
   }

   private static void renderHands() {
      computerLabels = new JLabel[NUM_CARDS_PER_HAND];
      humanLabels = new JButton[NUM_CARDS_PER_HAND];

      // CREATE LABELS ----------------------------------------------------
      HashMap<String,JButton> buttonMap= new HashMap<String,JButton>();
      for (int i = 0; i < NUM_CARDS_PER_HAND; i++) {
         computerLabels[i] = new JLabel(GUICard.getBackCardIcon());
         // player hand should be buttons
         JButton playCardButton = new JButton(GUICard.getIcon(LowCardGame.getHand(HUMAN_HAND_INDEX).inspectCard(i)));
         playCardButton.setActionCommand(String.valueOf(i));
         buttonMap.put(String.valueOf(i), playCardButton);
         playCardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
               // get slot number played
               String slotNumber = e.getActionCommand();
               System.out.println(slotNumber);
               JButton temp = buttonMap.get(slotNumber);
               // TODO: create new JLabel with temp.getIcon() and set in human play area
               buttonMap.get(slotNumber).setIcon(null);
               buttonMap.get(slotNumber).setEnabled(false);
               //System.exit(0);
            }
         });
         humanLabels[i] = playCardButton;
      }

      // ADD LABELS TO PANELS -----------------------------------------
      for (int i = 0; i < LowCardGame.getHand(0).getNumCards(); i++) {
         myCardTable.getPnlComputerHand().add(computerLabels[i]);
         myCardTable.getPnlHumanHand().add(humanLabels[i]);
      }
   }

   private static Card computerPlayCard() {
      Hand hand = LowCardGame.getHand(COMPUTER_HAND_INDEX);

      // Computer always play first for now and chooses random card from hand.
      int randomCardIndex = (int) (Math.random() * (hand.getNumCards() - 1));
      Card cardToPlay = hand.playCard(randomCardIndex);
      playedCardLabels[COMPUTER_HAND_INDEX] = new JLabel(GUICard.getIcon(cardToPlay));
      myCardTable.getPnlPlayArea().add(playedCardLabels[COMPUTER_HAND_INDEX]);

      return cardToPlay;
   }

   private static Card humanPlayCard() {
      Hand hand = LowCardGame.getHand(HUMAN_HAND_INDEX);

      int randomCardIndex = (int) (Math.random() * (hand.getNumCards() - 1));
      Card cardToPlay = hand.playCard(randomCardIndex);
      playedCardLabels[HUMAN_HAND_INDEX] = new JLabel(GUICard.getIcon(cardToPlay));
      myCardTable.getPnlPlayArea().add(playedCardLabels[HUMAN_HAND_INDEX]);

      return cardToPlay;
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
