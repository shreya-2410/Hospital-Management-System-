package HospitalManagementSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;

public class Doctor extends JFrame {
    private JButton viewDoctorsButton;
    private JButton getDoctorButton;
    private Connection connection;
    private JTextArea outputTextArea;
    private JTextField idTextField;

    public Doctor(Connection connection, Scanner scanner) {
        super("Doctor Management System");
        this.connection = connection;

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        viewDoctorsButton = new JButton("View Doctors");
        getDoctorButton = new JButton("Get Doctor");

        buttonPanel.add(viewDoctorsButton);
        buttonPanel.add(getDoctorButton);

        JPanel searchPanel = new JPanel(new FlowLayout());
        idTextField = new JTextField(10);
        searchPanel.add(new JLabel("Enter Doctor ID:"));
        searchPanel.add(idTextField);
        searchPanel.add(getDoctorButton);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        viewDoctorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewDoctors();
            }
        });

        getDoctorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idTextField.getText());
                getDoctorById(id);
            }
        });
    }

    private void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            StringBuilder sb = new StringBuilder();
            sb.append("Doctors: \n");
            sb.append("+------------+--------------------+------------------+ \n");
            sb.append("| Doctor Id  | Name               | Specialization   | \n");
            sb.append("+------------+--------------------+------------------+ \n");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                sb.append(String.format("| %-10s | %-18s | %-16s |\n", id, name, specialization));
                sb.append("+------------+--------------------+------------------+\n");
            }
            outputTextArea.setText(sb.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Doctor Details: \n");
                sb.append("+------------+--------------------+------------------+ \n");
                sb.append("| Doctor Id  | Name               | Specialization   | \n");
                sb.append("+------------+--------------------+------------------+ \n");

                int doctorId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                sb.append(String.format("| %-10s | %-18s | %-16s |\n", doctorId, name, specialization));

                sb.append("+------------+--------------------+------------------+\n");
                outputTextArea.setText(sb.toString());
                return true; // Doctor with ID exists
            } else {
                outputTextArea.setText("No doctor found with ID: " + id);
                return false; // Doctor with ID doesn't exist
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            return false; // Exception occurred
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Establish database connection
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "root");

                    // Create an instance of Doctor with the established connection
                    Doctor doctor = new Doctor(connection, new Scanner(System.in));

                    // Display GUI for Doctor
                    doctor.setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
                }
            }
        });
    }
}
