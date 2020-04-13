package com.company;

/**
 * Model class to store game data
 */
public class Model {
   /**
    * booleans to track who goes first each turn
    */
   boolean computerWin;
   boolean humanWin;

   int numPlayers;
   int numCardsPerHand;
   int numStacks; // Stacks of Cards in Play

   CardGameFramework LowCardGame; // CardGameFramework instance

   /**
    * Keep track of which cards are in play at any time
    */
   Card[] cardsInPlay;

   Model(int numPacks, int numJokersPerPack,
         int numUnusedCardsPerPack, Card[] unusedCardsPerPack,
         int numPlayers, int numCardsPerHand, int numStacks) {

      setNumPlayers(numPlayers);
      setNumCardsPerHand(numCardsPerHand);
      setNumStacks(numStacks);

      LowCardGame = new CardGameFramework(
            numPacks, numJokersPerPack,
            numUnusedCardsPerPack, unusedCardsPerPack,
            numPlayers, numCardsPerHand);

      cardsInPlay = new Card[this.numStacks];

      LowCardGame.deal();
   }

   public CardGameFramework getLowCardGame() {
      return LowCardGame;
   }

   public int getNumPlayers() {
      return numPlayers;
   }

   public void setNumPlayers(int numPlayers) {
      this.numPlayers = numPlayers;
   }

   public int getNumStacks() {
      return numStacks;
   }

   public void setNumStacks(int numStacks) {
      this.numStacks = numStacks;
   }

   public int getNumCardsPerHand() {
      return numCardsPerHand;
   }

   public void setNumCardsPerHand(int numCardsPerHand) {
      this.numCardsPerHand = numCardsPerHand;
   }

   public boolean isComputerWin() {
      return computerWin;
   }

   public void setComputerWin(boolean computerWin) {
      this.computerWin = computerWin;
   }

   public boolean isHumanWin() {
      return humanWin;
   }

   public void setHumanWin(boolean humanWin) {
      this.humanWin = humanWin;
   }

   public int getNumCardsInPlay() {
      return cardsInPlay.length;
   }

   public Card getCardInPlay(int index) {
      return cardsInPlay[index];
   }

   public void setCardInPlay(int index, Card card) {
      this.cardsInPlay[index] = card;
   }
}
