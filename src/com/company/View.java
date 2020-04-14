package com.company;

import javax.swing.*;

/**
 * View class to hold UI components
 */
public class View {
   /**
    * Game Card Table
    */
   static CardTable cardTable;  // CardTable instance

   View(String title, int numCardsPerHand, int numPlayers, int numStacks) {
      cardTable = new CardTable(title, numCardsPerHand, numPlayers);
      cardTable.setSize(900, 700);
      cardTable.setLocationRelativeTo(null);
      cardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // show everything to the user
      cardTable.setVisible(true);

      // Create and attach player UI elements
      for (int i = 0; i < numCardsPerHand; i++) {
         // create and attach UI for human and computer hands
         cardTable.getPnlComputerHand().add(new JLabel(GUICard.getBackCardIcon()));
         cardTable.getPnlHumanHand().add(new JButton());
      }

      // create and attach stack UI elements
      for (int i = 0; i < numStacks; i++) {
         cardTable.getPnlPlayArea().add(new JButton(new ImageIcon()));// put placeholder Buttons for stacks
      }

      // attach 'I cannot play' button
      cardTable.getPnlTurnActions().add(new JButton("I cannot play"), JButton.CENTER);
   }

   /**
    * Utility for displaying message dialogs
    *
    * @param message Message to display in dialog
    * @param title   Title of dialog box
    */
   public static void displayMessage(String message, String title) {
      JOptionPane.showMessageDialog(cardTable, message, title, JOptionPane.PLAIN_MESSAGE);
   }

   public CardTable getCardTable() {
      return cardTable;
   }

   public JLabel getComputerCardLabel(int index) {
      return (JLabel) cardTable.getPnlComputerHand().getComponent(index);
   }

   public JButton getHumanCardButton(int index) {
      return (JButton) cardTable.getPnlHumanHand().getComponent(index);
   }

   public JButton getPlayAreaButton(int index) {
      return (JButton) cardTable.getPnlPlayArea().getComponent(index);
   }

   public JButton getTurnActionButton() {
      return (JButton) cardTable.getPnlTurnActions().getComponent(0);
   }
}