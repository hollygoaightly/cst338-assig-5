package com.company;

/**
 * Project Members: Ericka Koyama, Holly Stephens, Ngoc Tran Do
 * CST 338 Software Design Assignment 6 - Timed Build Game
 */
// test
public class Assig6 {
   static int NUM_CARDS_PER_HAND = 7;
   static int NUM_PLAYERS = 2;
   static final int NUM_STACKS = 3;
   /**
    * CardGameFramework config
    */
   static int numPacksPerDeck;
   static int numJokersPerPack;
   static int numUnusedCardsPerPack;
   static Card[] unusedCardsPerPack;

   public static void main(String[] args) {
      numPacksPerDeck = 1;
      numJokersPerPack = 2;
      numUnusedCardsPerPack = 0;
      unusedCardsPerPack = null;

      // Assemble all the pieces of the MVC
      Model m = new Model(numPacksPerDeck, numJokersPerPack, numUnusedCardsPerPack, unusedCardsPerPack, NUM_PLAYERS, NUM_CARDS_PER_HAND, NUM_STACKS);
      View v = new View("Build Game", NUM_CARDS_PER_HAND, NUM_PLAYERS, NUM_STACKS);
      Controller c = new Controller(m, v);
      c.initController();
   }

}