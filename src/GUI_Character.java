package src;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Amanda DiFalco
 */
public class GUI_Character extends JFrame {
  private final JPanel mainPanel;
  private final CardLayout cardLayout;
  private String selectedCharacter = "";
  private JPanel listPanel;

  public GUI_Character() {
    setTitle("Character Manager");
    setSize(700, 700);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    mainPanel = new JPanel(cardLayout);

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

  private JPanel buildMainMenuScreen() {
    JPanel panel = createBasePanel();

    JLabel title = createTitleLabel("Character Settings");
    panel.add(title, BorderLayout.NORTH);

    JPanel buttons = new JPanel(new GridLayout(3, 1, 15, 15));
    buttons.setOpaque(false);

    JButton selectChar = createButton("Select Character to Edit");
    JButton addChar = createButton("Add New Character");
    JButton exit = createExitButton("Exit");

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

  private JPanel buildSelectCharacterScreen() {
    JPanel panel = createBasePanel();
    panel.setName("SelectCharacter");

    JLabel title = createTitleLabel("Select Character");
    panel.add(title, BorderLayout.NORTH);

    listPanel = createListPanel();
    panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

    JButton back = createExitButton("Exit");
    back.addActionListener(e -> showScreen("MainMenu"));
    panel.add(back, BorderLayout.SOUTH);

    panel.putClientProperty("listPanel", listPanel);
    return panel;
  }

  private JPanel buildSelectPlayerScreen() {
    JPanel panel = createBasePanel();
    panel.setName("SelectPlayer");

    JLabel title = createTitleLabel("Select Player");
    panel.add(title, BorderLayout.NORTH);

    listPanel = createListPanel();
    panel.add(new JScrollPane(listPanel), BorderLayout.CENTER);

    JButton back = createExitButton("Exit");
    back.addActionListener(e -> showScreen("MainMenu"));
    panel.add(back, BorderLayout.SOUTH);

    panel.putClientProperty("listPanel", listPanel);
    return panel;
  }

  private JPanel buildEditCharacterScreen() {
    JPanel panel = createBasePanel();

    JLabel title = createTitleLabel("Edit Character");
    panel.add(title, BorderLayout.NORTH);

    JPanel form = createFormPanel(8);

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

    JButton delete = createButton("Delete Character");
    JButton apply = createButton("Apply Changes");

    delete.addActionListener(e -> showScreen("DeleteConfirm"));

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

    form.add(delete);
    form.add(apply);

    panel.add(form, BorderLayout.CENTER);

    JButton back = createExitButton("Exit");
    back.addActionListener(e -> showScreen("SelectCharacter"));
    panel.add(back, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel buildAddCharacterScreen() {
    JPanel panel = createBasePanel();

    JLabel title = createTitleLabel("Add New Character");
    panel.add(title, BorderLayout.NORTH);

    JPanel form = createFormPanel(6);

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

    JButton add = createButton("Add Character");
    add.addActionListener(e -> {
      String sql = "INSERT INTO GAME_CHARACTER (Name, Strength, Stamina, Max_HP, Curr_HP, Location_ID) VALUES ('"
              + nameField.getText() + "', "
              + strengthField.getText() + ", "
              + staminaField.getText() + ", "
              + maxHpField.getText() + ", "
              + currHpField.getText() + ", 1)";
      sendQuery(sql);
      refreshScreen("SelectPlayer");
      showScreen("SelectPlayer");
    });

    panel.add(form, BorderLayout.CENTER);
    panel.add(add, BorderLayout.SOUTH);

    return panel;
  }

  private JPanel buildDeleteConfirmationScreen() {
    JPanel panel = createBasePanel();

    JLabel question = createTitleLabel("Are you sure you want to delete this character?");
    panel.add(question, BorderLayout.NORTH);

    JPanel buttons = new JPanel(new FlowLayout());
    buttons.setOpaque(false);

    JButton delete = createButton("Delete");
    JButton cancel = createExitButton("Cancel");

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
      if (c instanceof JPanel && name.equals(((JPanel) c).getName())) {
        screen = (JPanel) c;
        break;
      }
    }
    if (screen == null) return;

    listPanel = (JPanel) screen.getClientProperty("listPanel");
    if (listPanel == null) return;
    listPanel.removeAll();

    List<String> entries;
    if (name.equals("SelectCharacter"))
      entries = fetchFromDatabase("SELECT Name FROM GAME_CHARACTER");
    else
      entries = fetchFromDatabase("SELECT Login FROM PERSON WHERE Type_of_Account = 'Player'");

    for (String entry : entries) {
      JButton button = createButton(entry);
      button.addActionListener(e -> {
        selectedCharacter = entry;
        if (name.equals("SelectCharacter")) {
          showScreen("EditCharacter");
        } else {
          showScreen("AddCharacter");
        }
      });
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
    new GUI_Character();
  }
}