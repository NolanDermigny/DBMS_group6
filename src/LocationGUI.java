package src;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class LocationGUI extends JFrame {

  private final JPanel mainPanel;
  private final CardLayout cl;
//  private String selectedLocation;
//  private String viewLocation;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      LocationGUI gui = new LocationGUI();
      gui.setVisible(true);
    });
  }
  public LocationGUI() {

    setTitle("Locations");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(450, 500);
    setLocationRelativeTo(null);

    cl = new CardLayout();
    mainPanel = new JPanel(cl);

    // Add all panels
    mainPanel.add(setMainPanel(), "home");
    mainPanel.add(viewDBLocations(), "View Locations");
    mainPanel.add(setEditLocationsPanel(), "Edit Locations");

    add(mainPanel);

    showScreen("home");

  }

  public Component setMainPanel() {

    JPanel mainPanel = createBasePanel();
    JLabel title = createTitleLabel("View Locations");
    mainPanel.add(title, BorderLayout.NORTH);
    JPanel buttons = new JPanel(new GridLayout(3, 1));
//    make the buttons black
    buttons.setForeground(new Color(255, 255, 255));

    JButton viewLocsButton = createButton("View Locations");
    JButton editLocsButton = createButton("Edit Locations");
    JButton exitButton = createExitButton("Exit");

    viewLocsButton.addActionListener(e -> {
      showScreen("View Locations");
    });
    editLocsButton.addActionListener(e -> {
      showScreen("Edit Locations");
    });
    exitButton.addActionListener(e -> {System.exit(0);});

    buttons.add(viewLocsButton);
    buttons.add(editLocsButton);
    buttons.add(exitButton);
    mainPanel.add(buttons, BorderLayout.CENTER);
    return mainPanel;
  }

//  pull details from sql database
private Component viewLocations() {
  JPanel locationPanel = new JPanel(new BorderLayout());
  JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

  String[] locations = {"Location 1", "Location 2", "Location 3", "Location 4", "Location 5", "Location 6"};
  for (String location : locations) {
    JButton button = createButton(location);
    button.addActionListener(e -> cl.show(mainPanel, "details"));
    buttonPanel.add(button);
  }

  JButton backButton = createButton("Back");
  backButton.addActionListener(e -> showScreen("home"));

  locationPanel.add(new JScrollPane(buttonPanel), BorderLayout.CENTER);
  locationPanel.add(backButton, BorderLayout.SOUTH);

  JPanel detailsPanel = new JPanel(new BorderLayout());
  JLabel detailsLabel = new JLabel("Details: ");
  JTextArea detailsTextArea = new JTextArea(10, 30);
  detailsTextArea.setEditable(false);

  JButton backButton2 = createButton("Back");
  backButton2.addActionListener(e -> {
    cl.show(mainPanel, "View Locations");
    detailsTextArea.setText("");
  });

  detailsPanel.add(detailsLabel, BorderLayout.NORTH);
  detailsPanel.add(new JScrollPane(detailsTextArea), BorderLayout.CENTER);
  detailsPanel.add(backButton2, BorderLayout.SOUTH);

  mainPanel.add(locationPanel, "View Locations");
  mainPanel.add(detailsPanel, "details");

  return locationPanel;
}

private Component viewDBLocations() {
  JPanel locationPanel = new JPanel(new BorderLayout());
  JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

  ArrayList<String> locations = fetchFromDatabase("SELECT Location_ID, Description FROM LOCATION");
  for (String loc : locations) {
    String[] parts = loc.split(", ");
    String locId = parts[0];
    String desc = parts[1];

    JButton button = createButton("[" + locId + "] " + desc);
    button.addActionListener(e -> showLocationDetails(locId));
    buttonPanel.add(button);
  }

  JButton backButton = createButton("Back");
  backButton.addActionListener(e -> showScreen("home"));

  locationPanel.add(new JScrollPane(buttonPanel), BorderLayout.CENTER);
  locationPanel.add(backButton, BorderLayout.SOUTH);

  mainPanel.add(locationPanel, "View Locations");

  return locationPanel;
}

  private void showLocationDetails(String locationId) {
    JPanel detailsPanel = new JPanel(new BorderLayout());
    JLabel detailsLabel = new JLabel("Location Details:");
    JTextArea detailsTextArea = new JTextArea(10, 30);
    detailsTextArea.setEditable(false);

    // Fetch location details
    ArrayList<String> locationDetails = fetchFromDatabase(
          "SELECT Description, Location_Type, Exit_ID FROM LOCATION WHERE Location_ID = " + locationId
    );

    if (!locationDetails.isEmpty()) {
      detailsTextArea.setText(locationDetails.get(0));  // Show fetched details
    } else {
      detailsTextArea.setText("No details found.");
    }

    JButton backButton = createButton("Back");
    backButton.addActionListener(e -> showScreen("View Locations"));

    detailsPanel.add(detailsLabel, BorderLayout.NORTH);
    detailsPanel.add(new JScrollPane(detailsTextArea), BorderLayout.CENTER);
    detailsPanel.add(backButton, BorderLayout.SOUTH);

    mainPanel.add(detailsPanel, "details");

    showScreen("details");
  }

// for editing:
//  1. add or remove an exit from a location
//  after user selects "edit location" from menu prompt for location ID
//  show users the current exits for entered location ID
//  allow user to add or remove exit
  public Component setEditLocationsPanel() {
    JPanel editPanel = createBasePanel();
    JLabel title = createTitleLabel("Location Settings");
    title.setFont(new Font("Segoe UI", Font.BOLD, 26));
    title.setForeground(new Color(50, 50, 50));
    editPanel.add(title, BorderLayout.NORTH);
    JPanel buttons = new JPanel(new GridLayout(3, 1));

    JButton editInfoButton = createButton("Edit Location Info");
    JButton removeLocButton = createButton("Remove Location");
    JButton addLocButton = createButton("Add Location");
    JButton backButton = createButton("Back");
    editPanel.add(backButton, BorderLayout.SOUTH);

    editInfoButton.addActionListener(e -> {
      String locationID = JOptionPane.showInputDialog("Enter Location ID:");
      if (locationID == null || locationID.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Location ID: " + locationID + "Does not exist.");
      }
        else {
          String choice = JOptionPane.showInputDialog("Enter 1 to add an exit, 2 to remove an exit:");
            if (choice == null || choice.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid choice.");
            } else {
                if (choice.equals("1")) {
                String exitID = JOptionPane.showInputDialog("Enter Exit ID to add:");
                if (exitID != null && !exitID.trim().isEmpty()) {
                    // add sql query to add exit
                    JOptionPane.showMessageDialog(this, "Exit " + exitID + " added to location " + locationID);
                }
                } else if (choice.equals("2")) {
                String exitID = JOptionPane.showInputDialog("Enter Exit ID to remove:");
                if (exitID != null && !exitID.trim().isEmpty()) {
                    // add sql query to remove exit
                    JOptionPane.showMessageDialog(this, "Exit " + exitID + " removed from location " + locationID);
                }
                } else {
                JOptionPane.showMessageDialog(this, "Invalid choice.");
                }
            }
      }
    });

    removeLocButton.addActionListener(e -> {
      String locationID = JOptionPane.showInputDialog(this, "Enter Location ID to Remove:");
      if (locationID != null && !locationID.isEmpty()) {
        String sql = "DELETE FROM LOCATION WHERE Location_ID = " + locationID;
        sendQuery(sql);
        JOptionPane.showMessageDialog(this, "Location removed successfully.");
      }
      else {
        locationID = JOptionPane.showInputDialog(this, "Location ID does not exist. Try again.");
      }
    });
    addLocButton.addActionListener(e -> {
      String description = JOptionPane.showInputDialog(this, "Enter Location Description:");
      String locationType = JOptionPane.showInputDialog(this, "Enter Location Type:");
      String sql = "INSERT INTO LOCATION (Description, Location_Type) VALUES ('" + description + "', '" + locationType + "')";
      sendQuery(sql);
    });
    backButton.addActionListener(e -> showScreen("home"));

    buttons.add(editInfoButton);
    buttons.add(removeLocButton);
    buttons.add(addLocButton);
    editPanel.add(buttons, BorderLayout.CENTER);

    return editPanel;
  }



//  HELPER FUNCTIONS FROM AMANDA'S GUI_CHARACTER //

  private void showScreen(String name) {
    cl.show(mainPanel, name);
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
}
