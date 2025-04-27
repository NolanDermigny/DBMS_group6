package src;

import javax.swing.*;
import java.awt.*;

public class GUI_2 extends JFrame {

  public GUI_2() {
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Create a panel to hold the components
    JPanel panel = new JPanel(new GridLayout(2,1));
    add(panel);
    pack();
    setVisible(true);

    // Create a button for adding a player
    JButton addPlayerButton = new JButton("Add");
    add(addPlayerButton, BorderLayout.NORTH);

    //create a text box for player list
    JLabel playerList = new JLabel("Player List");
    add(playerList, BorderLayout.CENTER);

    //create buttons for each player


  }

  public static void main(String[] args) {
    GUI_2 gui = new GUI_2();

  }
}
