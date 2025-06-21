import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class CarRentalGUI extends JFrame {
    private CarRentalSystem crs;
    private JTable carTable;
    private DefaultTableModel tableModel;

    private Image backgroundImage;

    public CarRentalGUI(CarRentalSystem crs) {
        this.crs = crs;

        backgroundImage = Toolkit.getDefaultToolkit().createImage("background.jpg");

        setTitle("Car Rental System");
        setSize(1050, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        Font font = new Font("Segoe UI", Font.PLAIN, 15);
        Font headerFont = new Font("Segoe UI", Font.BOLD, 18);

        String[] columns = {"Car ID", "Brand", "Model", "Price/Day", "Fuel", "Transmission", "Mileage", "Type", "Seats", "Color", "Reg. No"};
        tableModel = new DefaultTableModel(columns, 0);
        carTable = new JTable(tableModel) {
            
            public boolean isCellEditable(int row, int column) {
                return false;
            }

           
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                
            }
        };
        carTable.setFont(font);
        carTable.setRowHeight(28);
        carTable.getTableHeader().setFont(headerFont);
        carTable.getTableHeader().setBackground(new Color(30, 144, 255));  // Dodger Blue
        carTable.getTableHeader().setForeground(Color.WHITE);
        carTable.setSelectionBackground(new Color(65, 105, 225)); // Royal Blue
        carTable.setSelectionForeground(Color.WHITE);

        carTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(new Color(65, 105, 225));
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? new Color(240, 248, 255) : Color.WHITE); 
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getViewport().setOpaque(false);
        carTable.setOpaque(false);
        carTable.getTableHeader().setOpaque(false);

        
        JButton rentButton = createStyledButton("Rent Car", new Color(34, 139, 34));
        JButton returnButton = createStyledButton("Return Car", new Color(255, 69, 0));
        JButton refreshButton = createStyledButton("Refresh", new Color(65, 105, 225));

        JButton viewRentalLogButton = createStyledButton("View Rental Log", new Color(100, 149, 237));  // Cornflower Blue
        JButton viewReturnLogButton = createStyledButton("View Return Log", new Color(255, 165, 0));    // Orange
        viewRentalLogButton.addActionListener(e -> showLogFile("rented_cars.txt"));
        viewReturnLogButton.addActionListener(e -> showLogFile("returned_cars.txt"));

        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        refreshButton.addActionListener(e -> loadAvailableCars());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        // buttonPanel.setLayout(new GridLayout(1, 3, 30, 0));
        // buttonPanel.add(rentButton);
        // buttonPanel.add(returnButton);
        // buttonPanel.add(refreshButton);
        buttonPanel.setLayout(new GridLayout(1, 5, 20, 0)); // update column count
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewRentalLogButton);
        buttonPanel.add(viewReturnLogButton);
            
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    
                    g.setColor(new Color(220, 230, 241));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);

        loadAvailableCars();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);

      
        button.setBorder(new RoundedBorder(15));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground().darker());
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius + 1;
            return insets;
        }
    }

    private void loadAvailableCars() {
        tableModel.setRowCount(0);
        for (Car car : crs.getCars()) {
            if (car.isAvailable()) {
                tableModel.addRow(new Object[]{
                        car.getCarId(), car.getBrand(), car.getModel(), car.getBasePricePerDay(),
                        car.getFuelType(), car.getTransmission(), car.getMileage(),
                        car.getVehicleType(), car.getSeatingCapacity(), car.getColor(),
                        car.getRegistrationNumber()
                });
            }
        }
    }

    private void rentCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to rent.");
            return;
        }

        String carId = (String) tableModel.getValueAt(selectedRow, 0);
        Car selectedCar = crs.findCarById(carId);
        if (selectedCar == null || !selectedCar.isAvailable()) {
            JOptionPane.showMessageDialog(this, "Selected car is not available.");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter your name:");
        if (name == null || name.trim().isEmpty()) return;

        String contact = JOptionPane.showInputDialog(this, "Enter your contact number:");
        if (contact == null || contact.trim().isEmpty()) return;

        String license = JOptionPane.showInputDialog(this, "Enter your license number:");
        if (license == null || license.trim().isEmpty()) return;

        Customer customer = new Customer("CUS" + (crs.getCustomers().size() + 1), name, contact, license);
        crs.addCustomer(customer);

        String startDateStr = JOptionPane.showInputDialog(this, "Enter start date (YYYY-MM-DD):");
        String endDateStr = JOptionPane.showInputDialog(this, "Enter return date (YYYY-MM-DD):");
        if (startDateStr == null || endDateStr == null) return;

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this, "Return date cannot be before start date.");
            return;
        }

        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) days = 1;

        int insuranceOption = JOptionPane.showConfirmDialog(this, "Add insurance?", "Insurance", JOptionPane.YES_NO_OPTION);
        boolean insurance = (insuranceOption == JOptionPane.YES_OPTION);

        double total = selectedCar.calculatePrice((int) days);
        if (insurance) total += crs.getInsuranceFeePerDay() * days;

        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Total price: %.2f\nConfirm booking?", total),
                "Confirm Booking", JOptionPane.YES_NO_OPTION);

        // if (confirm == JOptionPane.YES_OPTION) {
        //     crs.rentCar(selectedCar, customer, startDate, endDate, insurance);
        //     loadAvailableCars();
        // }
        if (confirm == JOptionPane.YES_OPTION) {
    crs.rentCar(selectedCar, customer, startDate, endDate, insurance);

    // Log the rental
    FileLogger.logRentedCar(
        carId, name, contact, license, startDate, endDate, insurance, total
    );

    loadAvailableCars();
}

    }

    private void returnCar() {
        String carId = JOptionPane.showInputDialog(this, "Enter Car ID to return:");
        if (carId == null || carId.trim().isEmpty()) return;

        // crs.returnCar(carId);
        // loadAvailableCars();
        // Log return before processing
    FileLogger.logReturnedCar(carId, LocalDate.now());

    crs.returnCar(carId);
    loadAvailableCars();

    }
    private void showLogFile(String filename) {
    try {
        java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filename));
        if (lines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No records found in " + filename);
            return;
        }

        JTextArea textArea = new JTextArea(String.join("\n", lines));
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Log - " + filename, JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading " + filename + ": " + e.getMessage());
    }
}

}