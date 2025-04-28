package src;
import javax.swing.*;
import java.awt.*;

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

    setTitle("Location Settings");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(450, 500);

    mainPanel = new JPanel();
    cl = new CardLayout();
    mainPanel.setLayout(cl);
//    JPanel viewLocs = new JPanel();
//    viewLocs.setLayout(new GridLayout(0,1));
    JButton viewButton = new JButton("View Locations");
    viewButton.addActionListener(e -> {viewLocations();});
    JButton backButton = new JButton("Back");
    backButton.addActionListener(e -> {
      cl.show(mainPanel, "locations");
//      selectedLocation = null;
    });
    mainPanel.add(backButton, "backButton");
    mainPanel.add(viewButton, "viewButton");
    add(mainPanel);
    cl.show(mainPanel, "viewButton");


  }


//  continue working on first "back"
  public void viewLocations() {

    // location selection panel
    JPanel locationPanel = new JPanel();
    locationPanel.setLayout(new GridLayout(0, 1));
    //    run through for loop and give "Location{i}" for every location in sql table
    String[] locations = {"Location 1", "Location 2", "Location 3", "Location 4", "Location 5", "Location 6"};

    for (String location : locations) {
      JButton button = new JButton(location);
      button.addActionListener(e -> {
//        selectedLocation = location;
        cl.show(mainPanel, "details");
      });
      locationPanel.add(button);
    }
//    work on this -- not working currently
    JButton backButton = new JButton("Back");
    JTextArea textArea = new JTextArea(10, 30);
    backButton.addActionListener(e -> {
      cl.show(mainPanel, "locations");
      textArea.setText("");
//      selectedLocation = null;
    });

    mainPanel.add(locationPanel, "locations");
    locationPanel.add(backButton, BorderLayout.SOUTH);

    // Create the details panel
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BorderLayout());

    JLabel detailsLabel = new JLabel("Details: ");
    JTextArea detailsTextArea = new JTextArea(10, 30);
    detailsTextArea.setEditable(false);

//    this one does work though
    JButton backButton2 = new JButton("Back");
    backButton2.addActionListener(e -> {
      cl.show(mainPanel, "locations");
      detailsTextArea.setText("");
//      selectedLocation = null;
    });

    detailsPanel.add(detailsLabel, BorderLayout.NORTH);
    detailsPanel.add(new JScrollPane(detailsTextArea), BorderLayout.CENTER);
    detailsPanel.add(backButton2, BorderLayout.SOUTH);

    mainPanel.add(detailsPanel, "details");

    add(mainPanel);

    cl.show(mainPanel, "locations");

  }


}
