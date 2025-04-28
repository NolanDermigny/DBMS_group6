//package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AbilityManager extends JFrame {
    private DefaultListModel<String> abilityListModel;
    private JList<String> abilityList;
    private JTextArea abilityInfo;
    private List<String[]> abilities = new ArrayList<>();

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

    private void loadAbilities() {
        abilities.clear();
        abilityListModel.clear();
        String response = sendSQL("SELECT * FROM ABILITY");
        if (response != null) {
            String[] lines = response.trim().split("\\n");
            if (lines.length > 1) {
                for (int i = 1; i < lines.length; i++) {
                    String[] parts = lines[i].split("\\t");
                    abilities.add(parts);
                    abilityListModel.addElement(parts[0]); // Name
                }
            }
        }
    }

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

    private void editAbility() {
        int index = abilityList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select an ability to edit.");
            return;
        }
        String[] ability = abilities.get(index);
        JTextField statField = new JTextField(ability[4]);
        JTextField effectField = new JTextField(ability[5]);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AbilityManager().setVisible(true));
    }
}

