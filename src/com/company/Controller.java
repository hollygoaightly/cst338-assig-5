package com.company;

import javax.swing.*;
import java.awt.*;
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

   static boolean cannotPlay; // a flag to check if we need to re-deal on stack

   /**
    * Integers to track how many times Computer and Human could not play any cards.
    */
   static int COMPUTER_CANNOT_PLAY;
   static int HUMAN_CANNOT_PLAY;

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
      selectedHandIndex = model.getNumStacks(); // invalid slot on stack(0-2) to start


      cannotPlay = false;

      COMPUTER_CANNOT_PLAY = 0;
      HUMAN_CANNOT_PLAY = 0;

      model.getLowCardGame().deal(); // deal to players
      initView();
   }

   /**
    * Attach event listeners
    */
   public void initController() {
      dealStacks();
      for (int i = 0; i < model.getNumStacks(); i++) { // attach button listener to card stacks
         JButton stackButton = new JButton(GUICard.getIcon(model.cardsInPlay[i]));
         stackButton.setActionCommand(String.valueOf(i));
         stackButton.addActionListener(new SelectStackButtonListener());
         view.setPlayedCardLabelsAtIndex(i, stackButton);
      }

      for (int i = 0; i < model.getNumCardsPerHand(); i++) { // attach button listener to human cards
         // player hand should be buttons
         JButton playCardButton =
               new JButton(GUICard.getIcon(model.getLowCardGame().getHand(HUMAN_HAND_INDEX).inspectCard(i)));
         playCardButton.setActionCommand(String.valueOf(i));
         playCardButton.addActionListener(new CardButtonListener());
         view.setHumanLabelAtIndex(i, playCardButton);

         // SET COMPUTER BACK CARD LABELS --------------------------------------
         view.setComputerLabelAtIndex(i, new JLabel(GUICard.getBackCardIcon()));

         // ADD LABELS TO PANELS -----------------------------------------
         view.getCardTable().getPnlComputerHand().add(view.getComputerLabelAtIndex(i));
         view.getCardTable().getPnlHumanHand().add(view.getHumanLabelAtIndex(i));
      }

      // attach I cannot play listener
      ((JButton) view.getCardTable().getPnlTurnActions().getComponent(0)).addActionListener(new CannotPlayButtonListener());

      renderHands();
      renderStacks();
      // show everything to the user
      view.getCardTable().setVisible(true);
   }

   /**
    * Initialize the view with starting values
    */
   private void initView() {
      view.setComputerLabels(new JLabel[model.getNumCardsPerHand()]);
      view.setHumanLabels(new JButton[model.getNumCardsPerHand()]);
      view.setPlayedCardLabels(new JButton[model.getNumStacks()]);

      for(int i = 0; i < model.getNumStacks(); i++) {
         view.getCardTable().getPnlPlayArea().add(new JButton(new ImageIcon())); // put placeholders in
      }

      view.getCardTable().getPnlTurnActions().add(new JButton("I cannot play"), JButton.CENTER);
   }

   /**
    * Deal a card to each stack in the middle of the table.
    */
   private void dealStacks() {
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

      for (int i = 0; i < model.getNumCardsInPlay(); i++) {
         JButton playArea = (JButton) view.getCardTable().getPnlPlayArea().getComponent(i);
         Card c = model.getCardInPlay(i);
         if (null != c && !c.getErrorFlag()) {
            playArea.setIcon(GUICard.getIcon(c));
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
      JOptionPane.showMessageDialog(view.getCardTable(), displayText, "Round Results", JOptionPane.PLAIN_MESSAGE);
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
      Component[] playAreaLabels = view.getCardTable().getPnlPlayArea().getComponents();
      for (int i = 0; i < playAreaLabels.length; i++) {
         ((JLabel) playAreaLabels[i]).setIcon(null); // set the play area icons to null
      }
      // Deal out new cards
      for (int i = 0; i < model.getNumPlayers(); i++) {
         model.getLowCardGame().takeCard(i); // replenish hand
      }
      view.getCardTable().getPnlHumanHand().removeAll();
      view.getCardTable().getPnlComputerHand().removeAll();

      renderHands();
      view.getCardTable().revalidate();
      view.getCardTable().repaint();
      // End Game if either player is out of cards
      if (model.getLowCardGame().getHand(COMPUTER_HAND_INDEX).getNumCards() == 0 && model.getLowCardGame().getHand(HUMAN_HAND_INDEX).getNumCards() == 0) {
         handleEndGame();
      }
      // Otherwise check if computer starts new round
      else if (model.isComputerWin()) {
         model.setCardInPlay(0, computerPlayCard());
      }
   }

   /**
    * Update player hands in the UI
    */
   private void renderHands() {
      // GENERATE COMP LABELS ----------------------------------------------------
      for (int i = 0; i < model.getNumCardsPerHand(); i++) {
         Card card = model.getLowCardGame().getHand(COMPUTER_HAND_INDEX).inspectCard(i);

         if (card == null || card.getErrorFlag()) {
            view.getComputerLabelAtIndex(i).setIcon(null);
            view.getComputerLabelAtIndex(i).setEnabled(false);
         }

         // ADD COMP LABELS TO PANELS -----------------------------------------
         view.getCardTable().getPnlComputerHand().add(view.getComputerLabelAtIndex(i));
      }

      // GENERATE HUMAN LABELS ----------------------------------------------------
      for (int i = 0; i < model.getNumCardsPerHand(); i++) {
         // player hand should be buttons
         Card card = model.getLowCardGame().getHand(HUMAN_HAND_INDEX).inspectCard(i);

         if (card == null || card.getErrorFlag()) {
            view.getHumanLabelAtIndex(i).setIcon(null);
            view.getHumanLabelAtIndex(i).setEnabled(false);
         } else {
            view.getHumanLabelAtIndex(i).setIcon(GUICard.getIcon(card));
            view.getHumanLabelAtIndex(i).setEnabled(true);
         }

         // ADD HUMAN LABELS TO PANELS -----------------------------------------
         view.getCardTable().getPnlHumanHand().add(view.getHumanLabelAtIndex(i));
      }
   }

   /**
    * Computer play card logic.
    *
    * @return The card the Computer will play.
    */
   private Card computerPlayCard() {
      Hand hand = model.getLowCardGame().getHand(COMPUTER_HAND_INDEX);
      Card cardToPlay = null;
      hand.sort();  //want to sort out the hand to get 1 card lower than humanCardToPlay
      if (view.getPlayedCardLabels()[HUMAN_HAND_INDEX] == null) {  //if no display on humanCard, then play at index 0.
         cardToPlay = model.getLowCardGame().playCard(COMPUTER_HAND_INDEX, 0); //which if is sorted then would be
         // lowest card
      } else {
         for (int i = hand.getNumCards() - 1; i > 0; i--) { // find highest card that is sufficient to win round
            if (model.getCardInPlay(HUMAN_HAND_INDEX).getValue() < hand.inspectCard(i).getValue()) {
               cardToPlay = model.getLowCardGame().playCard(COMPUTER_HAND_INDEX, i);
               break;
            }
         }
      }
      if (cardToPlay == null) { // if still null then computer can't win round, discard highest
         cardToPlay = hand.playCard(hand.getNumCards() - 1);
      }
      // update ui
      JLabel playArea = (JLabel) view.getCardTable().getPnlPlayArea().getComponent(COMPUTER_HAND_INDEX);
      playArea.setIcon(GUICard.getIcon(cardToPlay));
      return cardToPlay;
   }

   /**
    * After Human plays card, handles updating the UI and returns the Card played.
    *
    * @param handIndex The index in the Human hand of the card to be played.
    * @return The Card to be played.
    */
   private Card humanPlayCard(int handIndex) {
      Card cardToPlay = model.getLowCardGame().playCard(HUMAN_HAND_INDEX, handIndex);
      // update ui
      JLabel playArea = (JLabel) view.getCardTable().getPnlPlayArea().getComponent(HUMAN_HAND_INDEX);
      playArea.setIcon(GUICard.getIcon(cardToPlay));
      return cardToPlay;
   }

   /**
    * Checks the value of card stored in selectedCard and compares it to the card at specified slot.
    *
    * @param slotNumber The index of the specified card slot.
    * @return The if the play is valid.
    */
   private boolean validCardPlayed(int slotNumber) {
      boolean result = false;
      if (null != selectedCard && selectedHandIndex < model.getNumStacks()) {
         Card cardInSlot =  model.getCardInPlay(slotNumber);
         if (GUICard.valueAsInt(selectedCard) == GUICard.valueAsInt(cardInSlot)+1 || GUICard.valueAsInt(selectedCard) == GUICard.valueAsInt(cardInSlot)-1) {
            result = true;
         }
      }
      return result;
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
            int slotNumber = Integer.valueOf(e.getActionCommand()); // get slot number played
            if (validCardPlayed(slotNumber)) {
               JButton button = view.getHumanLabelAtIndex(selectedHandIndex);
               model.setCardInPlay(slotNumber, humanPlayCard(selectedHandIndex));
               button.setIcon(null);
               button.setEnabled(false);
               selectedCard = null;
               selectedHandIndex = model.getNumStacks();
               resetForNewRound();
         }
      }
   }

   /**
    * Inner "cannot play" button listener class
    */
   private class CannotPlayButtonListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
         HUMAN_CANNOT_PLAY += 1;
         if (cannotPlay) // second cannot play in sequence, re-deal to stacks
         {
            dealStacks();
            cannotPlay = false;
         } else {
            cannotPlay = true;
         }
         computerPlayCard();
      }
   }
}