package src;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class GUI_Character extends JFrame {
  private JPanel mainPanel;
  private CardLayout cardLayout;
  private String selectedCharacter = ""; // track selected character

  public GUI_Character() {
    setTitle("Character Manager");
    setSize(600, 600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

    // Add all screens
    mainPanel.add(buildMainMenuScreen(), "MainMenu");
    mainPanel.add(buildSelectCharacterScreen(), "SelectCharacter");
    mainPanel.add(buildSelectPlayerScreen(), "SelectPlayer");
    mainPanel.add(buildEditCharacterScreen(), "EditCharacter");
    mainPanel.add(buildAddCharacterScreen(), "AddCharacter");
    mainPanel.add(buildDeleteConfirmationScreen(), "DeleteConfirm");

    add(mainPanel);
    showScreen("MainMenu");
    setVisible(true);
  }

  //Main Menu Screen
  private JPanel buildMainMenuScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));

    JLabel title = new JLabel("Character Settings", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel buttons = new JPanel(new GridLayout(3,1,10,10));
    JButton selectChar = new JButton("Select Character to Edit");
    JButton addChar = new JButton("Add New Character");
    JButton exit = new JButton("Exit");

    selectChar.setBackground(new Color(255,255,204));
    addChar.setBackground(new Color(255,255,204));
    exit.setBackground(new Color(255,204,204));

    selectChar.addActionListener(e -> {
      refreshScreen("SelectCharacter");
      showScreen("SelectCharacter");
    });

    addChar.addActionListener(e -> {
      refreshScreen("SelectPlayer");
      showScreen("SelectPlayer");
    });

    exit.addActionListener(e -> System.exit(0));

    buttons.add(selectChar);
    buttons.add(addChar);
    buttons.add(exit);

    panel.add(buttons, BorderLayout.CENTER);

    return panel;
  }

  //Select Character Screen
  private JPanel buildSelectCharacterScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));
    panel.setName("SelectCharacter");

    JLabel title = new JLabel("Select Character", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel characterPanel = new JPanel(new GridLayout(0,2,10,10));
    JScrollPane scrollPane = new JScrollPane(characterPanel);
    panel.add(scrollPane, BorderLayout.CENTER);

    JButton back = new JButton("Exit");
    back.setBackground(new Color(255,204,204));
    back.addActionListener(e -> showScreen("MainMenu"));
    panel.add(back, BorderLayout.SOUTH);

    panel.putClientProperty("listPanel", characterPanel);
    return panel;
  }

  //Select Player Screen
  private JPanel buildSelectPlayerScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));
    panel.setName("SelectPlayer");

    JLabel title = new JLabel("Select Player", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel playerPanel = new JPanel(new GridLayout(0,2,10,10));
    JScrollPane scrollPane = new JScrollPane(playerPanel);
    panel.add(scrollPane, BorderLayout.CENTER);

    JButton back = new JButton("Exit");
    back.setBackground(new Color(255,204,204));
    back.addActionListener(e -> showScreen("MainMenu"));
    panel.add(back, BorderLayout.SOUTH);

    panel.putClientProperty("listPanel", playerPanel);
    return panel;
  }

  //Edit Character Screen
  private JPanel buildEditCharacterScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));

    JLabel title = new JLabel("Edit Character", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel form = new JPanel(new GridLayout(8,1,10,10));

    JTextField maxHpField = new JTextField();
    JTextField currHpField = new JTextField();
    JTextField strengthField = new JTextField();
    JTextField staminaField = new JTextField();

    form.add(new JLabel("Max Hit Points:"));
    form.add(maxHpField);
    form.add(new JLabel("Current Hit Points:"));
    form.add(currHpField);
    form.add(new JLabel("Strength:"));
    form.add(strengthField);
    form.add(new JLabel("Stamina:"));
    form.add(staminaField);

    JButton delete = new JButton("Delete Character");
    JButton apply = new JButton("Apply Changes");

    delete.setBackground(new Color(255,255,204));
    apply.setBackground(new Color(255,255,204));

    form.add(delete);
    form.add(apply);

    panel.add(form, BorderLayout.CENTER);

    JButton back = new JButton("Exit");
    back.setBackground(new Color(255,204,204));
    back.addActionListener(e -> showScreen("SelectCharacter"));
    panel.add(back, BorderLayout.SOUTH);

    delete.addActionListener(e -> {
      showScreen("DeleteConfirm");
    });

    apply.addActionListener(e -> {
      String sql = "UPDATE GAME_CHARACTER SET Max_HP=" + maxHpField.getText() +
              ", Curr_HP=" + currHpField.getText() +
              ", Strength=" + strengthField.getText() +
              ", Stamina=" + staminaField.getText() +
              " WHERE Name='" + selectedCharacter + "'";
      sendQuery(sql);
      refreshScreen("SelectCharacter");
      showScreen("SelectCharacter");
    });

    return panel;
  }

  //Add New Character Screen
  private JPanel buildAddCharacterScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));

    JLabel title = new JLabel("Add New Character", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));
    panel.add(title, BorderLayout.NORTH);

    JPanel form = new JPanel(new GridLayout(6,1,10,10));

    JTextField nameField = new JTextField();
    JTextField maxHpField = new JTextField();
    JTextField currHpField = new JTextField();
    JTextField strengthField = new JTextField();
    JTextField staminaField = new JTextField();

    form.add(new JLabel("Name:"));
    form.add(nameField);
    form.add(new JLabel("Max Hit Points:"));
    form.add(maxHpField);
    form.add(new JLabel("Current Hit Points:"));
    form.add(currHpField);
    form.add(new JLabel("Strength:"));
    form.add(strengthField);
    form.add(new JLabel("Stamina:"));
    form.add(staminaField);

    panel.add(form, BorderLayout.CENTER);

    JButton add = new JButton("Add Character");
    add.setBackground(new Color(255,255,204));
    add.addActionListener(e -> {
      String sql = "INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('"
              + nameField.getText() + "', " +
              strengthField.getText() + ", " +
              staminaField.getText() + ", " +
              maxHpField.getText() + ", " +
              currHpField.getText() + ", 1)";
      sendQuery(sql);
      refreshScreen("SelectPlayer");
      showScreen("SelectPlayer");
    });
    panel.add(add, BorderLayout.SOUTH);

    JButton back = new JButton("Exit");
    back.setBackground(new Color(255,204,204));
    back.addActionListener(e -> showScreen("SelectPlayer"));
    panel.add(back, BorderLayout.NORTH);

    return panel;
  }

  //Delete Confirmation Screen
  private JPanel buildDeleteConfirmationScreen() {
    JPanel panel = new JPanel(new BorderLayout(10,10));

    JLabel question = new JLabel("Are you sure you want to delete this character?", SwingConstants.CENTER);
    question.setFont(new Font("Arial", Font.BOLD, 20));
    panel.add(question, BorderLayout.NORTH);

    JPanel buttons = new JPanel(new FlowLayout());

    JButton delete = new JButton("Delete");
    JButton cancel = new JButton("No");

    delete.setBackground(new Color(255,255,204));
    cancel.setBackground(new Color(255,204,204));

    delete.addActionListener(e -> {
      sendQuery("DELETE FROM GAME_CHARACTER WHERE Name='" + selectedCharacter + "'");
      refreshScreen("SelectCharacter");
      showScreen("SelectCharacter");
    });

    cancel.addActionListener(e -> showScreen("EditCharacter"));

    buttons.add(delete);
    buttons.add(cancel);
    panel.add(buttons, BorderLayout.CENTER);

    return panel;
  }

  private void showScreen(String name) {
    cardLayout.show(mainPanel, name);
  }

  private void refreshScreen(String name) {
    JPanel screen = null;
    for (Component c : mainPanel.getComponents()) {
      if (c instanceof JPanel && name.equals(((JPanel)c).getName())) {
        screen = (JPanel)c;
        break;
      }
    }
    if (screen == null) return;

    JPanel listPanel = (JPanel) screen.getClientProperty("listPanel");
    if (listPanel == null) return;
    listPanel.removeAll();

    ArrayList<String> entries;
    if (name.equals("SelectCharacter"))
      entries = fetchFromDatabase("SELECT Name FROM GAME_CHARACTER");
    else
      entries = fetchFromDatabase("SELECT Login FROM PERSON");

    for (String entry : entries) {
      JLabel label = new JLabel(entry, SwingConstants.CENTER);
      JButton button = new JButton(name.equals("SelectCharacter") ? "Edit" : "Select");
      button.setBackground(new Color(255,255,204));

      if (name.equals("SelectCharacter")) {
        button.addActionListener(e -> {
          selectedCharacter = entry;
          showScreen("EditCharacter");
        });
      } else {
        button.addActionListener(e -> {
          selectedCharacter = entry;
          showScreen("AddCharacter");
        });
      }

      listPanel.add(label);
      listPanel.add(button);
    }

    listPanel.revalidate();
    listPanel.repaint();
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
        if (skipHeader) { skipHeader = false; continue; }
        results.add(line.trim());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }

  private void sendQuery(String query) {
    try (Socket socket = new Socket("127.0.0.1", 4446);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      out.println(query);
      in.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new GUI_Character();
  }
}





