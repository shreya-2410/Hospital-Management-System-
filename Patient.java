package HospitalManagementSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient extends JFrame {
    private JButton addPatientButton;
    private JButton viewPatientsButton;
    private Connection connection;
    private JTextArea outputTextArea;
    private JTextField idTextField;
    private JButton searchButton;
    private JButton deleteButton; // Added delete button
    private Scanner scanner; // Added scanner

    public Patient(Connection connection, Scanner scanner) {
        super("Hospital Management System");
        this.connection = connection;
        this.scanner = scanner; // Initialize scanner

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(500, 300);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        addPatientButton = new JButton("Add Patient");
        viewPatientsButton = new JButton("View Patients");

        buttonPanel.add(addPatientButton);
        buttonPanel.add(viewPatientsButton);

        JPanel searchPanel = new JPanel(new FlowLayout());
        idTextField = new JTextField(10);
        searchButton = new JButton("Search");

        searchPanel.add(new JLabel("Enter Patient ID:"));
        searchPanel.add(idTextField);
        searchPanel.add(searchButton);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        // Adding delete button
        deleteButton = new JButton("Delete");
        JPanel deletePanel = new JPanel(new FlowLayout());
        deletePanel.add(new JLabel("Enter Patient ID to Delete:"));
        deletePanel.add(idTextField); // Reusing the same text field for delete
        deletePanel.add(deleteButton);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
        add(deletePanel, BorderLayout.SOUTH); // Adding delete panel

        addPatientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPatient();
            }
        });

        viewPatientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPatients();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idTextField.getText());
                getPatientById(id);
            }
        });

        // Action listener for the delete button
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idTextField.getText());
                    deletePatientById(id);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid patient ID.");
                }
            }
        });
    }

    // Method to add a patient
    private void addPatient() {
        String name = JOptionPane.showInputDialog("Enter Patient Name:");
        String ageStr = JOptionPane.showInputDialog("Enter Patient Age:");
        int age = Integer.parseInt(ageStr);
        String gender = JOptionPane.showInputDialog("Enter Patient Gender:");

        try {
            String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Patient Added Successfully!!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to add Patient!!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    // Method to view all patients
    private void viewPatients() {
        String query = "SELECT * FROM patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Patients: \n");
            sb.append("+------------+--------------------+----------+------------+\n");
            sb.append("| Patient Id | Name               | Age      | Gender     |\n");
            sb.append("+------------+--------------------+----------+------------+\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                sb.append(String.format("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender));
                sb.append("+------------+--------------------+----------+------------+\n");
            }
            outputTextArea.setText(sb.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    // Method to get a patient by ID
  boolean getPatientById(int id) {
        try {
            String query = "SELECT * FROM patients WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Patient Details: \n");
            sb.append("+------------+--------------------+----------+------------+\n");
            sb.append("| Patient Id | Name               | Age      | Gender     |\n");
            sb.append("+------------+--------------------+----------+------------+\n");

            if (resultSet.next()) {
                int patientId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                sb.append(String.format("| %-10s | %-18s | %-8s | %-10s |\n", patientId, name, age, gender));
                sb.append("+------------+--------------------+----------+------------+\n");
                outputTextArea.setText(sb.toString());
                return true; // Return true if patient with given ID exists
            } else {
                sb.append("No patient found with ID: " + id);
                sb.append("+------------+--------------------+----------+------------+\n");

                outputTextArea.setText(sb.toString());
                return false; // Return false if patient with given ID does not exist
            }
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            return false; // Return false in case of any exception
        }
    }

    // Method to delete a patient by ID
    private void deletePatientById(int id) {
        try {
            String query = "DELETE FROM patients WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Patient deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "No patient found with the provided ID.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Establish database connection
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "root");

                    // Initialize scanner
                    Scanner scanner = new Scanner(System.in);

                    // Create an instance of Patient with the established connection and scanner
                    Patient patient = new Patient(connection, scanner);
                    patient.setVisible(true);

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
                }
            }
        });
    }
}

