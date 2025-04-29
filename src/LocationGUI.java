package src;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * LocationGUI.java
 *
 * This class creates a GUI for managing locations in a game.
 * It allows users to view, edit, and add locations.
 *
 * @author Kevin Pickelman
 * @group 6 -- Carlie Cann, Amanda DiFalco, Nolan Dermigny, and Kevin Pickelman
 */

/**
 * LocationGUI provides a GUI interface to view, edit, add, and remove locations
 * by interacting with a backend database through sockets.
 */
public class LocationGUI extends JFrame {

  private final JPanel mainPanel; // Main panel that holds all other panels using CardLayout
  private final CardLayout cl; // CardLayout used to switch between different screens (home, view, edit)

  /**
   * Main method that creates and displays the Location GUI window.
   * Entry point of the application.
   */
  public static void main(String[] args) {
    LocationGUI gui = new LocationGUI();
    gui.setVisible(true);
  }

  /**
   * Constructor sets up the main GUI frame and initializes all screens.
   */
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

  /**
   * Creates the main home panel with navigation buttons.
   * @return the home panel
   */
  public Component setMainPanel() {

    JPanel mainPanel = createBasePanel();
//    JLabel title = createTitleLabel("Location GUI");
//    mainPanel.add(title, BorderLayout.NORTH);
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

  /**
   * Creates a simple location viewing panel with dummy locations.
   * @return the location viewing panel
   */
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

/**
 * Fetches location information from the database and creates a viewing panel.
 * @return the database-backed location viewing panel
 */
private Component viewDBLocations() {
  JPanel locationPanel = new JPanel(new BorderLayout());
  JPanel buttonPanel = new JPanel(new GridLayout(0, 1));

  ArrayList<String> locations = fetchFromDatabase("SELECT Location_ID, Location_Type FROM LOCATION");
  for (String loc : locations) {
    String[] parts = loc.split(", ");
    String locId = parts[0];
    String desc = (parts.length > 1) ? parts[1] : "Unknown"; // <-- safeguard

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

/**
 * Displays the detailed information for a selected location.
 * @param locationId ID of the location to show
 */
  private void showLocationDetails(String locationId) {
    JPanel detailsPanel = new JPanel(new BorderLayout());
    JLabel detailsLabel = new JLabel("Location Details:");
    JTextArea detailsTextArea = new JTextArea(10, 30);
    detailsTextArea.setEditable(false);

    // Fetch location details
    ArrayList<String> locationDetails = fetchFromDatabase(
          "SELECT Location_ID, Location_Type, Exit_ID FROM LOCATION WHERE Location_ID = " + locationId);

    if (!locationDetails.isEmpty()) {
      String[] parts = locationDetails.get(0).split(", ");
      if (parts.length >= 3) {
        detailsTextArea.setText("Location ID: " + parts[0] + "\n" +
              "Location Type: " + parts[1] + "\n" +
              "Exit ID: " + parts[2]);
      }
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
                  String sql = "UPDATE LOCATION SET Exit_ID = " + exitID + " WHERE Location_ID = " + locationID;
                  sendQuery(sql);
                  JOptionPane.showMessageDialog(this, "Exit " + exitID + " added to location " + locationID);
                }
              } else if (choice.equals("2")) {
                String exitID = JOptionPane.showInputDialog("Enter Exit ID to remove:");
                if (exitID != null && !exitID.trim().isEmpty()) {
                  String sql = "UPDATE LOCATION SET Exit_ID = NULL WHERE Location_ID = " + locationID;
                  sendQuery(sql);
                  JOptionPane.showMessageDialog(this, "Exit removed from location " + locationID);
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
      String locID = JOptionPane.showInputDialog(this, "Enter Location ID:");
      String locationType = JOptionPane.showInputDialog(this, "Enter Location Type:");
      String sizeStr = JOptionPane.showInputDialog(this, "Enter Size of Location:");
      String exitID = JOptionPane.showInputDialog(this, "Enter Exit ID:");

      if (locID != null && locationType != null && sizeStr != null) {
        try {
          int size = Integer.parseInt(sizeStr);
          String sql = "INSERT INTO LOCATION (LocationID, Location_Type, Size, Exit_ID) VALUES ('" +
                locID + "', " + locationType + "', " + size + ", '" + exitID + "')";
          sendQuery(sql);
          JOptionPane.showMessageDialog(this, "Location added successfully.");
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(this, "Invalid size entered.");
        }
      }
    });
    backButton.addActionListener(e -> showScreen("home"));

    buttons.add(editInfoButton);
    buttons.add(removeLocButton);
    buttons.add(addLocButton);
    editPanel.add(buttons, BorderLayout.CENTER);

    return editPanel;
  }



//  HELPER FUNCTIONS FROM AMANDA'S GUI_CHARACTER //

  /**
   * Switches the displayed screen in the CardLayout.
   * @param name the screen name
   */
  private void showScreen(String name) {
    cl.show(mainPanel, name);
  }
  
  /**
   * Creates a basic JPanel with a BorderLayout and background color.
   * @return the base panel
   */
  private JPanel createBasePanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(new Color(245, 245, 250));
    return panel;
  }

  /**
   * Creates a formatted title JLabel.
   * @param text title text
   * @return the JLabel
   */
  private JLabel createTitleLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setFont(new Font("Segoe UI", Font.BOLD, 26));
    label.setForeground(new Color(50, 50, 50));
    return label;
  }

  /**
   * Creates a panel for listing components vertically.
   * @return the list panel
   */
  private JPanel createListPanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
    panel.setBackground(new Color(245, 245, 250));
    return panel;
  }

  /**
   * Creates a form panel with a specified number of rows.
   * @param rows number of rows
   * @return the form panel
   */
  private JPanel createFormPanel(int rows) {
    JPanel panel = new JPanel(new GridLayout(rows, 1, 10, 10));
    panel.setOpaque(false);
    return panel;
  }

  /**
   * Creates a styled button with text.
   * @param text button text
   * @return the JButton
   */
  private JButton createButton(String text) {
    JButton button = new JButton(text);
    button.setFocusPainted(false);
    button.setBackground(new Color(230, 230, 250));
    button.setForeground(Color.BLACK);
    button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    return button;
  }

  /**
   * Creates a specially styled exit button.
   * @param text button text
   * @return the exit JButton
   */
  private JButton createExitButton(String text) {
    JButton button = createButton(text);
    button.setBackground(new Color(255, 204, 204));
    return button;
  }

  /**
   * Fetches results from the database using a socket connection.
   * @param query SQL query
   * @return list of result strings
   */
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

  /**
   * Sends an update or insert query to the database using a socket.
   * @param query SQL query to execute
   */
  private void sendQuery(String query) {
    try (Socket socket = new Socket("127.0.0.1", 4446);
         PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
      out.println(query);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

