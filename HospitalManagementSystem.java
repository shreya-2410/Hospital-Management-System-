package HospitalManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "root";

    private Connection connection;
    private Patient patient;
    private Doctor doctor;

    private JTextField patientIdField, doctorIdField, appointmentDateField;
    private JTextArea outputArea;
    private Scanner scanner;

    public HospitalManagementSystem(Scanner scanner) {
        super("Hospital Management System");
        this.scanner = scanner;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            patient = new Patient(connection, scanner);
            doctor = new Doctor(connection, scanner);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 400));

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Patient ID:"));
        patientIdField = new JTextField();
        inputPanel.add(patientIdField);

        inputPanel.add(new JLabel("Doctor ID:"));
        doctorIdField = new JTextField();
        inputPanel.add(doctorIdField);

        inputPanel.add(new JLabel("Appointment Date (YYYY-MM-DD):"));
        appointmentDateField = new JTextField();
        inputPanel.add(appointmentDateField);

        JButton bookAppointmentButton = new JButton("Book Appointment");
        bookAppointmentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookAppointment();
            }
        });
        inputPanel.add(bookAppointmentButton);

        add(inputPanel, BorderLayout.NORTH);

        // Panel for output
        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void bookAppointment() {
        int patientId = Integer.parseInt(patientIdField.getText());
        int doctorId = Integer.parseInt(doctorIdField.getText());
        String appointmentDate = appointmentDateField.getText();

        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        outputArea.append("Appointment Booked!\n");
                    } else {
                        outputArea.append("Failed to Book Appointment!\n");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    outputArea.append("Error: " + ex.getMessage() + "\n");
                }
            } else {
                outputArea.append("Doctor not available on this date!!\n");
            }
        } else {
            outputArea.append("Either doctor or patient doesn't exist!!!\n");
        }
    }

    private boolean checkDoctorAvailability(int doctorId, String appointmentDate) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            outputArea.append("Error: " + e.getMessage() + "\n");
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new HospitalManagementSystem(scanner);
            }
        });
    }
}
