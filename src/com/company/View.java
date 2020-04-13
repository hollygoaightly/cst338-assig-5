package com.company;

import javax.swing.*;

/**
 * View class to hold UI components
 */
public class View {
   /**
    * UI Labels
    */
   JLabel[] computerLabels;
   JButton[] humanLabels;
   JButton[] playedCardLabels;

   /**
    * Game Card Table
    */
   static CardTable cardTable;  // CardTable instance

   View(String title, int numCardsPerHand, int numPlayers, int numStacks) {
      cardTable = new CardTable(title, numCardsPerHand, numPlayers);
      cardTable.setSize(900, 700);
      cardTable.setLocationRelativeTo(null);
      cardTable.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      setComputerLabels(new JLabel[numCardsPerHand]);
      setHumanLabels(new JButton[numCardsPerHand]);
      setPlayedCardLabels(new JButton[numStacks]);
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

   public JLabel getComputerLabelAtIndex(int index) {
      return computerLabels[index];
   }

   public JLabel setComputerLabelAtIndex(int index, JLabel label) {
      return computerLabels[index] = label;
   }

   public JButton getHumanLabelAtIndex(int index) {
      return humanLabels[index];
   }

   public void setHumanLabelAtIndex(int index, JButton button) {
      this.humanLabels[index] = button;
   }

   public void setComputerLabels(JLabel[] computerLabels) {
      this.computerLabels = computerLabels;
   }

   public void setHumanLabels(JButton[] humanLabels) {
      this.humanLabels = humanLabels;
   }

   public JButton[] getPlayedCardLabels() {
      return playedCardLabels;
   }

   public JButton getPlayedCardLabelsAtIndex(int index) { return playedCardLabels[index]; }

   public void setPlayedCardLabels(JButton[] playedCardLabels) {
      this.playedCardLabels = playedCardLabels;
   }

   public void setPlayedCardLabelsAtIndex(int index, JButton button) {
      this.playedCardLabels[index] = button;
   }

   public CardTable getCardTable() {
      return cardTable;
   }
}
