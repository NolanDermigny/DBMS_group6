package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * AbilityManager.java
 * @author Carlie Cann
 */

/**
 * AbilityManager provides a GUI for viewing, adding, editing, and removing
 * abilities stored in a database, communicating with a server over sockets.
 */
public class AbilityManager extends JFrame {
	 /**
     * List model for abilities displayed in the UI.
     */
    private DefaultListModel<String> abilityListModel;
    
    /**
     * Visual list component for displaying ability names.
     */
    private JList<String> abilityList;
    
    /**
     * Text area for showing detailed information about a selected ability.
     */
    private JTextArea abilityInfo;
    
    /**
     * Internal list storing ability data, each entry as an array of fields.
     */
    private List<String[]> abilities = new ArrayList<>();

    /**
     * Constructs the AbilityManager UI and loads ability data.
     */
    public AbilityManager() {
        setTitle("Ability Manager");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        abilityListModel = new DefaultListModel<>();
        abilityList = new JList<>(abilityListModel);
        abilityList.addListSelectionListener(e -> showAbilityInfo());
        add(new JScrollPane(abilityList), BorderLayout.WEST);

        abilityInfo = new JTextArea();
        abilityInfo.setEditable(false);
        add(new JScrollPane(abilityInfo), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("Edit");
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton backButton = new JButton("Back");

        editButton.addActionListener(e -> editAbility());
        addButton.addActionListener(e -> addAbility());
        removeButton.addActionListener(e -> removeAbility());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadAbilities();
    }

    /**
     * Loads abilities from the database and updates the UI list.
     */
    private void loadAbilities() {
        abilities.clear();
        abilityListModel.clear();
        String response = sendSQL("SELECT * FROM ABILITY");
        if (response != null) {
            String[] lines = response.trim().split("\\n");
            if (lines.length > 1) {
                for (int i = 1; i < lines.length; i++) {
                    String[] parts = lines[i].split("\t");
                    if (parts.length <= 6) {
                        abilities.add(parts);
                        abilityListModel.addElement(parts[0]); // Name
                    } else {
                        System.out.println("Skipping invalid ability entry: " + String.join(",", parts));
                    }

                }
            }
        }
    }

    /**
     * Displays information about the currently selected ability.
     */
    private void showAbilityInfo() {
        int index = abilityList.getSelectedIndex();
        if (index >= 0) {
            String[] ability = abilities.get(index);
            StringBuilder info = new StringBuilder();
            String[] fields = {"Name", "Sound Effect", "Rate Of Occurrence", "Cast Time", "Stat Affected", "Effect Amount"};
            for (int i = 0; i < ability.length; i++) {
                info.append(fields[i]).append(": ").append(ability[i]).append("\n");
            }
            abilityInfo.setText(info.toString());
        }
    }

    /**
     * Allows the user to edit the selected ability's stat and effect amount.
     */
    private void editAbility() {
        int index = abilityList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select an ability to edit.");
            return;
        }
        
        String[] ability = abilities.get(index);
        JTextField statField = new JTextField(ability[4]);
        JTextField effectField = new JTextField(ability[5]);
        if (ability.length < 6) {
            JOptionPane.showMessageDialog(this, "Corrupted ability data. Cannot edit.");
            return;
        }


        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Stat Affected:"));
        panel.add(statField);
        panel.add(new JLabel("Effect Amount:"));
        panel.add(effectField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Ability", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String stat = statField.getText();
            String effect = effectField.getText();
            String query = String.format("UPDATE ABILITY SET Stat_Affected='%s', Effect_Amount=%s WHERE Name='%s'", stat, effect, ability[0]);
            sendSQL(query);
            loadAbilities();
        }
    }

    /**
     * Allows the user to add a new ability to the database.
     */
    private void addAbility() {
        JTextField nameField = new JTextField();
        JTextField soundField = new JTextField();
        JTextField rateField = new JTextField();
        JTextField castField = new JTextField();
        JTextField statField = new JTextField();
        JTextField effectField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Sound Effect:"));
        panel.add(soundField);
        panel.add(new JLabel("Rate Of Occurrence:"));
        panel.add(rateField);
        panel.add(new JLabel("Cast Time (hh:mm:ss):"));
        panel.add(castField);
        panel.add(new JLabel("Stat Affected:"));
        panel.add(statField);
        panel.add(new JLabel("Effect Amount:"));
        panel.add(effectField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Ability", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String query = String.format("INSERT INTO ABILITY (Name, Sound_Effect, Rate_Of_Occurrence, Cast_Time, Stat_Affected, Effect_Amount) VALUES ('%s', '%s', %s, '%s', '%s', %s)",
                    nameField.getText(), soundField.getText(), rateField.getText(), castField.getText(), statField.getText(), effectField.getText());
            sendSQL(query);
            loadAbilities();
        }
    }

    /**
     * Allows the user to remove the selected ability from the database.
     */
    private void removeAbility() {
        int index = abilityList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select an ability to remove.");
            return;
        }
        String name = abilities.get(index)[0];
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            sendSQL("DELETE FROM ABILITY WHERE Name='" + name + "'");
            loadAbilities();
        }
    }

    /**
     * Sends an SQL query to the server and returns the response.
     *
     * @param query The SQL query to send.
     * @return The server's response string, or null if there was an error.
     */
    private String sendSQL(String query) {
        try (Socket socket = new Socket("127.0.0.1", 4446);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(query);
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage());
            return null;
        }
    }

    /**
     * Main method to launch the AbilityManager UI.
     *
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AbilityManager().setVisible(true));
    }
}


