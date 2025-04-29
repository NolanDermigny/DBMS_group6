package src;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI_Player.java @author Nolan Dermigny
 * Used Amanda's code as a base for the GUI
 * This class creates a GUI for managing players.
 * It allows the user to add, edit, and view player information.
 *
 */

public class GUI_Player extends JFrame {
  private final JPanel panel;
  private final CardLayout cardLayout;
  private String selectedCharacter = "";
  private JPanel listPanel;

  /**
   * Constructor for GUI_Player
   * Initializes the JFrame and sets up the GUI components.
   */
  public GUI_Player() {
    setTitle("Player Manager");
    setSize(700, 700);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    panel = new JPanel(cardLayout);

    //create the 4 screens
    panel.add(buildPlayerList(), "PlayerList");
    panel.add(buildAddPlayer(), "AddPlayer");
    panel.add(buildPlayerInfo(), "PlayerInfo");
    panel.add(buildEditPlayer(), "EditPlayer");

    add(panel);
    showScreen("PlayerList");
    refreshScreen("PlayerList");
    setVisible(true);
  }

  /**
   * Creates the starting screen, which shows an ADD PLAYER button and a
   * Scrolling player list of each player's login.
   */
  private JPanel buildPlayerList() {
    JPanel panel = createBasePanel();
    panel.setName("PlayerList");

    JLabel title = createTitleLabel("Player List");
    panel.add(title, BorderLayout.NORTH);

    listPanel = createListPanel();
    panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

    JButton addPlayer = createButton("Add Player");

    addPlayer.addActionListener(e -> showScreen("AddPlayer"));
    panel.add(addPlayer, BorderLayout.SOUTH);

    panel.putClientProperty("listPanel", listPanel);
    refreshScreen("PlayerList");


    return panel;
  }

  /**
   * Creates the screen for adding a player.
   * Prompts the user for email, login, and password
   * hit confirm to add the player
   * back to do nothing and return to the player list
   */
  private JPanel buildAddPlayer() {
    JPanel panel = createBasePanel();
    panel.setName("AddPlayer");

    JLabel title = createTitleLabel("Add Player");
    panel.add(title, BorderLayout.NORTH);

    JPanel form = createFormPanel(3);

    JTextField emailField = new JTextField();
    JTextField loginField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    form.add(new JLabel("Email:"));
    form.add(emailField);
    form.add(new JLabel("Login:"));
    form.add(loginField);
    form.add(new JLabel("Password:"));
    form.add(passwordField);

    JButton add = createButton("Confirm");
    add.addActionListener(e -> {
      String email = emailField.getText();
      String login = loginField.getText();
      String password = new String(passwordField.getPassword());


      String sql = "INSERT INTO PERSON (Login, Creation_Date, Password, Email, Type_Of_Account, Above_Account_ID, Creature_ID, Character_Name) " +
        "VALUES ('" + login + "', '2000-01-01', '" + password + "', '" + email + "', 'Player', 1, 11, 'default');";

      sendQuery(sql);

      //wait before refreshing
      Timer timer = new Timer(100, event -> showScreen("PlayerList"));
      timer.setRepeats(false);
      timer.start();
    });

    JButton back = createExitButton("Back");
    back.addActionListener(e -> showScreen("PlayerList"));

    panel.add(form, BorderLayout.CENTER);
    panel.add(add, BorderLayout.SOUTH);
    panel.add(back, BorderLayout.EAST);

    return panel;
  }

  /**
   * Creates the screen for viewing a player's information.
   * EDIT button maps to the edit player screen
   * REMOVE button removes the player from the database and returns to the player list
   * BACK button returns to the player list
   * ALso displays the player's email, and login as well as the list
   * of characters they control.
   */
  private JPanel buildPlayerInfo() {
    JPanel panel = createBasePanel();
    panel.setName("PlayerInfo");

    JLabel title = createTitleLabel("Player Info");
    panel.add(title, BorderLayout.NORTH);

    JPanel center = new JPanel(new BorderLayout(10, 10));
    center.setOpaque(false);

    // Info labels
    JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
    infoPanel.setOpaque(false);
    JLabel loginLabel = new JLabel("Login: ");
    JLabel emailLabel = new JLabel("Email: ");
    infoPanel.add(loginLabel);
    infoPanel.add(emailLabel);

    center.add(infoPanel, BorderLayout.NORTH);

    // Characters list
    listPanel = createListPanel();
    center.add(new JScrollPane(listPanel), BorderLayout.CENTER);

    panel.add(center, BorderLayout.CENTER);

    // Buttons
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setOpaque(false);

    JButton removeButton = createButton("Remove");
    JButton editButton = createButton("Edit");
    JButton backButton = createExitButton("Back");

    removeButton.addActionListener(e -> {
      int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this player?", "Confirm", JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
        sendQuery("DELETE FROM PERSON WHERE Login='" + selectedCharacter + "'");

        // Wait before refreshing
        Timer timer = new Timer(100, event -> showScreen("PlayerList"));
        timer.setRepeats(false);
        timer.start();
      }
    });

    editButton.addActionListener(e -> {
      showScreen("EditPlayer");
    });

    backButton.addActionListener(e -> {
      showScreen("PlayerList");
    });

    buttonPanel.add(removeButton);
    buttonPanel.add(editButton);
    buttonPanel.add(backButton);

    panel.add(buttonPanel, BorderLayout.SOUTH);

    // Store labels for refreshScreen
    panel.putClientProperty("loginLabel", loginLabel);
    panel.putClientProperty("emailLabel", emailLabel);
    panel.putClientProperty("listPanel", listPanel);

    return panel;
  }

  /**
   * Creates the Edit Player screen.
   * Users are prompted to edit only the email
   * CONFIRM button updates and returns to the player list
   * BACK button returns to the player list
   */
  private JPanel buildEditPlayer() {
    JPanel panel = createBasePanel();
    panel.setName("EditPlayer");

    JLabel title = createTitleLabel("Edit Player");
    panel.add(title, BorderLayout.NORTH);

    JPanel form = createFormPanel(2);

    JTextField emailField = new JTextField();

    form.add(new JLabel("Email:"));
    form.add(emailField);

    JButton confirmButton = createButton("Confirm");
    confirmButton.addActionListener(e -> {
      String email = emailField.getText();
      String sql = "UPDATE PERSON SET Email='" + email + "' WHERE Login='" + selectedCharacter + "';";
      sendQuery(sql);

      // Wait before refreshing
      Timer timer = new Timer(100, event -> showScreen("PlayerList"));
      timer.setRepeats(false);
      timer.start();
    });

    JButton backButton = createExitButton("Back");
    backButton.addActionListener(e -> showScreen("PlayerList"));

    panel.add(form, BorderLayout.CENTER);
    panel.add(confirmButton, BorderLayout.SOUTH);
    panel.add(backButton, BorderLayout.EAST);

    return panel;
  }

  private void showScreen(String name) {
    refreshScreen(name);
    cardLayout.show(panel, name);

  }

  private void refreshScreen(String name) {

      JPanel screen = null;
      for (Component c : panel.getComponents()) {
        if (c instanceof JPanel && name.equals(((JPanel) c).getName())) {
          screen = (JPanel) c;
          break;
        }
      }
      if (screen == null) return;

      if (name.equals("PlayerList")) {
        listPanel = (JPanel) screen.getClientProperty("listPanel");
        if (listPanel == null) return;
        listPanel.removeAll();

        List<String> entries = fetchFromDatabase("SELECT Login FROM PERSON WHERE Type_of_Account = 'Player'");
        for (String entry : entries) {
          JButton button = createButton(entry);
          button.addActionListener(e -> {
            selectedCharacter = entry;
            showScreen("PlayerInfo");
          });
          listPanel.add(button);
        }

        listPanel.revalidate();
        listPanel.repaint();
      }
      else if (name.equals("PlayerInfo")) {
        JLabel loginLabel = (JLabel) screen.getClientProperty("loginLabel");
        JLabel emailLabel = (JLabel) screen.getClientProperty("emailLabel");
        listPanel = (JPanel) screen.getClientProperty("listPanel");

        // Set Login label
        loginLabel.setText("Login: " + selectedCharacter);

        // Fetch Email
        List<String> emailResult = fetchFromDatabase(
          "SELECT Email FROM PERSON WHERE Login='" + selectedCharacter + "'"
        );
        String email = emailResult.isEmpty() ? "Unknown" : emailResult.get(0);
        emailLabel.setText("Email: " + email);

        // Fetch Characters
        listPanel.removeAll();
        List<String> characters = fetchFromDatabase(
          "SELECT Character_Name FROM PERSON WHERE Login='" + selectedCharacter + "'"
        );
        for (String character : characters) {
          listPanel.add(new JLabel(character));
        }

        listPanel.revalidate();
        listPanel.repaint();
      }

  }


  private ArrayList<String> fetchFromDatabase(String query) {
    ArrayList<String> results = new ArrayList<>();
    try (Socket socket = new Socket("127.0.0.1", 4446);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      out.println(query);
      String line;
      boolean skipHeader = true;
      while ((line = in.readLine()) != null) {
        if (skipHeader) {
          skipHeader = false;
          continue;
        }
        results.add(line.trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }

  private void sendQuery(String query) {
    try (Socket socket = new Socket("127.0.0.1", 4446);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
      out.println(query);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private JPanel createBasePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(new Color(245, 245, 250));
    return panel;
  }

  private JLabel createTitleLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 26));
    label.setForeground(new Color(50, 50, 50));
    return label;
  }

  private JPanel createListPanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
    panel.setBackground(new Color(245, 245, 250));
    return panel;
  }

  private JPanel createFormPanel(int rows) {
    JPanel panel = new JPanel(new GridLayout(rows, 1, 10, 10));
    panel.setOpaque(false);
    return panel;
  }

  private JButton createButton(String text) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(new Color(230, 230, 250));
    button.setForeground(Color.BLACK);
    button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    return button;
  }

  private JButton createExitButton(String text) {
    JButton button = createButton(text);
    button.setBackground(new Color(255, 204, 204));
    return button;
  }

  public static void main(String[] args) {
    GUI_Player gui = new GUI_Player();


  }
}
