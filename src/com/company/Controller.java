package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller class to drive game.
 */
public class Controller {
   /**
    * Constants to keep track of Human and Computer hand indexes.
    */
   static final int COMPUTER_HAND_INDEX = 0;
   static final int HUMAN_HAND_INDEX = 1;

   boolean cannotPlay; // a flag to check if we need to re-deal on stack

   /**
    * Integers to track how many times Computer and Human could not play any cards.
    */
   int COMPUTER_CANNOT_PLAY;
   int HUMAN_CANNOT_PLAY;

   /**
    * Temp space to store a selected Card before played
    */
   Card selectedCard;

   /**
    * Temp to store the hand index of a selected card before it's played
    */
   int selectedHandIndex;

   private Model model;
   private View view;

   Controller(Model m, View v) {
      model = m;
      view = v;

      selectedCard = null;
      selectedHandIndex = -1; // null state

      cannotPlay = false;

      COMPUTER_CANNOT_PLAY = 0;
      HUMAN_CANNOT_PLAY = 0;

      initView();
   }

   /**
    * Attach event listeners
    */
   public void initController() {
      for (int i = 0; i < model.getNumStacks(); i++) { // attach button listener to card stacks
         view.getPlayAreaButton(i).addActionListener(new SelectStackButtonListener());
      }

      for (int i = 0; i < model.getNumCardsPerHand(); i++) { // attach button listener to human cards
         view.getHumanCardButton(i).addActionListener(new CardButtonListener());
      }

      // attach 'I cannot play' listener
      view.getTurnActionButton().addActionListener(new CannotPlayButtonListener());
   }

   /**
    * Initialize the view with starting values
    */
   private void initView() {
      dealStacks();

      for (int i = 0; i < model.getNumStacks(); i++) { // Update UI with model data
         view.getPlayAreaButton(i).setIcon(GUICard.getIcon(model.cardsInPlay[i]));
         view.getPlayAreaButton(i).setActionCommand(String.valueOf(i));
      }

      for (int i = 0; i < model.getNumCardsPerHand(); i++) { // Update UI with model data
         view.getHumanCardButton(i).setIcon(GUICard.getIcon(model.getLowCardGame().getHand(HUMAN_HAND_INDEX).inspectCard(i)));
         view.getHumanCardButton(i).setActionCommand(String.valueOf(i));
      }

      renderHands();
      renderStacks();
   }

   /**
    * Deal a card to each stack in the middle of the table.
    */
   private void dealStacks() {
      System.out.println("dealStacks checkpoint");
      for (int i = 0; i < model.getNumCardsInPlay(); i++) {
         Card c = model.getLowCardGame().getCardFromDeck();
         if (!c.getErrorFlag()) {
            model.setCardInPlay(i, c);
         } else { // invalid card, deck is empty, end game
            handleEndGame();
            return;
         }
      }
   }

   /**
    * Render the cards currently in play
    */
   private void renderStacks() {
      System.out.println("render stacks checkpoint");
      for (int i = 0; i < model.getNumCardsInPlay(); i++) {
         Card c = model.getCardInPlay(i);
         if (null != c && !c.getErrorFlag()) {
            view.getPlayAreaButton(i).setIcon(GUICard.getIcon(c));
         }
      }
   }

   /**
    * Show a dialog with Game Results.
    */
   private void handleEndGame() {
      String resultText = "";
      if (COMPUTER_CANNOT_PLAY == HUMAN_CANNOT_PLAY) {
         resultText = "You tied!";
      } else if (COMPUTER_CANNOT_PLAY > HUMAN_CANNOT_PLAY) {
         resultText = "You win!";
      } else {
         resultText = "Computer wins!";
      }
      String displayText =
            "Game is Over. Final Scores: \n" + "Computer: " + COMPUTER_CANNOT_PLAY + " forfeits\n"
                  + "You: " + HUMAN_CANNOT_PLAY + " forfeits\n" + resultText;
      view.displayMessage(displayText, "Round Results");
   }

   /**
    * Play cards from each hand to playing area
    */
   private void playCards() {
      dealStacks();
   }

   /**
    * Reset for next round
    */
   private void resetForNewRound() {
      if (model.getLowCardGame().getNumCardsRemainingInDeck() == 0) { // end game on empty deck
         handleEndGame();
         return;
      }

      // Deal out new cards
      for (int i = 0; i < model.getNumPlayers(); i++) {
         model.getLowCardGame().takeCard(i); // replenish hand
      }

      renderHands();

      view.getCardTable().revalidate();
      view.getCardTable().repaint();
   }

   /**
    * Update player hands in the UI
    */
   private void renderHands() {
      // UPDATE COMP LABELS ----------------------------------------------------
      for (int i = 0; i < model.getNumCardsPerHand(); i++) {
         Card card = model.getLowCardGame().getHand(COMPUTER_HAND_INDEX).inspectCard(i);

         if (card == null || card.getErrorFlag()) {
            view.getComputerCardLabel(i).setIcon(null);
            view.getComputerCardLabel(i).setEnabled(false);
         }
      }

      // UPDATE HUMAN LABELS ----------------------------------------------------
      for (int i = 0; i < model.getNumCardsPerHand(); i++) {
         Card card = model.getLowCardGame().getHand(HUMAN_HAND_INDEX).inspectCard(i);

         if (card == null || card.getErrorFlag()) {
            view.getHumanCardButton(i).setIcon(null);
            view.getHumanCardButton(i).setEnabled(false);
         } else {
            view.getHumanCardButton(i).setIcon(GUICard.getIcon(card));
            view.getHumanCardButton(i).setEnabled(true);
         }
      }
   }

   /**
    * handles computer turn.
    */
   private void computerTurn() {
      System.out.println("start computer turn - current cannot play: " + cannotPlay);
      Card card = computerPlayCard(); // computer play card handles valid play scenario
      if (card == null ) { // handle cannot play scenario
         COMPUTER_CANNOT_PLAY += 1;
         if(cannotPlay)
         {
            dealStacks();
            renderStacks();
            cannotPlay = false;
         }
         else {
            cannotPlay = true;
         }
         System.out.println("computer could not play");
      }
      else {
         System.out.println("computer played");
      }
   }

   /**
    * Computer play card logic.
    *
    * @return The card the Computer will play.
    */
   private Card computerPlayCard() {
      Hand computerHand = model.getLowCardGame().getHand(COMPUTER_HAND_INDEX);
      for (int i = 0; i < model.getNumStacks(); i++) {
         for (int j = 0; j < computerHand.getNumCards(); j++ ) {
            Card card = computerHand.inspectCard(j);
            System.out.println("Computer Card: " + card.toString());
            if (validCardPlayed(i, card)) {
               cannotPlay = false; // play is valid, reset flag
               model.setCardInPlay(i, card);
               playCardToStack(i, card);
               System.out.print("Computer play card-- checkpoint:");
               checkpoint();
               return computerHand.playCard(j);
            }
         }
      }
      return null;
   }

   /**
    * Update stack with played card.
    * @param stackIndex
    * @param card
    */
   private void playCardToStack(int stackIndex, Card card) {
      view.getPlayAreaButton(stackIndex).setIcon(GUICard.getIcon(card));
   }

   /**
    * After Human plays card, handles updating the UI and returns the Card played.
    *
    * @param handIndex The index in the Human hand of the card to be played.
    * @return The Card to be played.
    */
   private Card humanPlayCard(int handIndex) {
      return model.getLowCardGame().playCard(HUMAN_HAND_INDEX, handIndex);
   }

   /**
    * Checks the value of card stored in selectedCard and compares it to the card at specified slot.
    *
    * @param stackIndex The index of the specified card slot.
    * @return The if the play is valid.
    */
   private boolean validHumanCardPlayed(int stackIndex, Card card) {
      if (selectedCard == null || selectedHandIndex == -1) return false; // human check not necessary for computer
      return validCardPlayed(stackIndex, card);
   }

   /**
    * Checks the value of card stored in selectedCard and compares it to the card at specified slot.
    *
    * @param stackIndex The index of the specified card slot.
    * @return The if the play is valid.
    */
   private boolean validCardPlayed(int stackIndex, Card card) {
      int selectedValue = GUICard.valueAsInt(card);
      int topStackValue = GUICard.valueAsInt(model.getCardInPlay(stackIndex));
      System.out.println("validCardPlayed sanity check: "+ String.valueOf(selectedValue) + " "+ String.valueOf(topStackValue));
      if (selectedValue == topStackValue + 1 || selectedValue == topStackValue - 1) {
         System.out.println("cards adjacent: "+ String.valueOf(selectedValue) + " "+ String.valueOf(topStackValue) +  " returning true");
         return true;
      }

      // handle K -> A or A -> K
      int highestRank = Card.valuRanks.length - 1;
      if (topStackValue == highestRank && selectedValue == 0 ||
            topStackValue == 0 && selectedValue == highestRank) {
         System.out.println("cards adjacent: "+ String.valueOf(selectedValue) + String.valueOf(topStackValue) +  " returning true");
         return true;
      }
      System.out.println(selectedHandIndex);
      System.out.println("card invalid, returning false");
      return false;
   }

   // util debug
   public void checkpoint()
   {
      System.out.println("\n\n CHECKPOINT-----");
      System.out.println("Selected hand index: " + selectedHandIndex);
      System.out.println("Cards in play:");
      printCardsInPlay();
      System.out.println("\n");
   }

   // util debug
   public void printCardsInPlay()
   {
      for (int i = 0; i < model.getNumStacks(); i++) {
         Card c = model.getCardInPlay(i);
         System.out.print(c.getValue() + " ");
         System.out.println(c.getSuit());
      }
   }

   /**
    * Inner button listener class
    */
   private class CardButtonListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         selectedHandIndex = Integer.valueOf(e.getActionCommand()); // store the hand index of the card desired to be played
         selectedCard = model.getLowCardGame().getHand(HUMAN_HAND_INDEX).inspectCard(selectedHandIndex); // store the card desired to be played
      }
   }

   /**
    * Inner select stack button listener class Allows the user to specify which stack they are trying to play their
    * selected card on
    */
   private class SelectStackButtonListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         System.out.println("Select stack-- checkpoint:");
         int stackIndex = Integer.valueOf(e.getActionCommand()); // get slot number played
         if (validHumanCardPlayed(stackIndex, selectedCard)) {
            cannotPlay = false; // play is valid, reset flag
            Card card = humanPlayCard(selectedHandIndex);
            model.setCardInPlay(stackIndex, card);
            playCardToStack(stackIndex, card);
            System.out.print("Human play card -- checkpoint:");
            checkpoint();

            view.getHumanCardButton(selectedHandIndex).setIcon(null);
            view.getHumanCardButton(selectedHandIndex).setEnabled(false);
            selectedCard = null;
            selectedHandIndex = -1;
            renderHands();
            computerTurn();
         }
      }
   }

   /**
    * Inner "cannot play" button listener class
    */
   private class CannotPlayButtonListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         System.out.println("start player cannot play - current cannot play: " + cannotPlay);
         HUMAN_CANNOT_PLAY += 1;
         if (cannotPlay) // second cannot play in sequence, re-deal to stacks
         {
            dealStacks();
            renderStacks();
            cannotPlay = false;
         } else {
            cannotPlay = true;
         }
         System.out.print("Cannot Play-- checkpoint:");
         checkpoint();
         computerTurn();
      }
   }
}