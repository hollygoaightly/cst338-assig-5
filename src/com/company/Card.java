package com.company;

/**
 * Card class represents a single card.
 */
public class Card {
   public enum Suit { // All possible suits of a card
      CLUBS,
      DIAMONDS,
      HEARTS,
      SPADES
   }

   private char value;
   private Suit suit;
   private boolean errorFlag; // Whether this Card has an error.

   public static char[] valuRanks = {'A', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'X'};

   /**
    * No argument constructor
    */
   Card() {
      value = 'A';
      suit = Suit.SPADES;
   }

   /**
    * Constructor with value and suit.
    *
    * @param value The value of the card.
    * @param suit  The suit of the card.
    */
   Card(char value, Suit suit) {
      set(value, suit);
   }

   /**
    * Copy constructor
    *
    * @param card Card to make copy of.
    */
   Card(Card card) {
      set(card.getValue(), card.getSuit());
   }

   private static int getRankIndex(char cardValue) {
      int i;

      for (i = 0; i < valuRanks.length; i++) {
         if (new Character(valuRanks[i]).equals(cardValue)) break;
      }

      return i;
   }

   public static void arraySort(Card[] cards, int arraySize) {
      Card temp;

      for (int i = 0; i < arraySize - 1; i++) {
         for (int j = 0; j < arraySize - i - 1; j++) {
            if (getRankIndex(cards[j].getValue()) > getRankIndex(cards[j + 1].getValue())) {
               // swap cards[j+1] and cards[i]
               temp = cards[j];
               cards[j] = cards[j + 1];
               cards[j + 1] = temp;
            }
         }
      }
   }

   /**
    * Check if the given Card object equals this one.
    *
    * @param card Card instance to check for equality to this.
    * @return Whether the Card objects are equal.
    */
   public boolean equals(Card card) {
      return card != null && card.getSuit() == suit && card.getValue() == value && card.getErrorFlag() == errorFlag;
   }

   /**
    * Check if this Card has an error.
    *
    * @return Whether the Card object has an error.
    */
   public boolean getErrorFlag() {
      return errorFlag;
   }

   /**
    * Get the suit of this card.
    *
    * @return Suit of card.
    */
   public Suit getSuit() {
      return suit;
   }

   /**
    * Get the value of this card.
    *
    * @return Value of this card.
    */
   public char getValue() {
      return value;
   }

   /**
    * Check if the parameters given are valid for a Card.
    *
    * @param value Value given.
    * @param suit  Suit given.
    * @return Whether the parameters are valid.
    */
   private boolean isValid(char value, Suit suit) {
      if (value >= '2' && value <= '9') {
         return true;
      }

      switch (value) {
         case 'T':
         case 'J':
         case 'Q':
         case 'K':
         case 'A':
         case 'X':
            return true;
         default:
            return false;
      }
   }

   /**
    * Set given parameters for Card.
    *
    * @param value Value to set for Card.
    * @param suit  Suit to set for Card.
    * @return Whether the parameters were able to be set.
    */
   public boolean set(char value, Suit suit) {
      errorFlag = !isValid(value, suit);

      if (!errorFlag) {
         this.value = value;
         this.suit = suit;
      }

      return !errorFlag;
   }

   /**
    * Display Card as a String.
    *
    * @return Card data as a String.
    */
   public String toString() {
      if (!errorFlag) {
         return value + " of " + suit;
      } else {
         return "** illegal **";
      }
   }
}
