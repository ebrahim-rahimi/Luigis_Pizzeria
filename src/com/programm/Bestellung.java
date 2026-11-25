package com.programm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.Locale;

// Displays all saved orders from the CSV file and sums up the revenue
public class BestellungenGUI extends JFrame {

    private DefaultTableModel tableModel;
    private JLabel lblRevenue;

    public BestellungenGUI() {
        setTitle("Luigi's Pizzeria — Saved Orders");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window, not the whole app
        setSize(760, 500);
        setMinimumSize(new Dimension(650, 400));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(GUITheme.BG_CREAM);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));
        setContentPane(root);

        // Top summary card
        JPanel headerCard = new JPanel(new BorderLayout());
        headerCard.setBackground(GUITheme.BG_CARD);
        headerCard.setBorder(GUITheme.createCardBorder(14, 20));

        JLabel lblTitle = new JLabel("Order History");
        lblTitle.setFont(GUITheme.FONT_BRAND);
        lblTitle.setForeground(GUITheme.TEXT_ESPRESSO);
        headerCard.add(lblTitle, BorderLayout.WEST);

        lblRevenue = new JLabel("Total Revenue: 0.00 €");
        lblRevenue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRevenue.setForeground(GUITheme.BASIL_GREEN);
        headerCard.add(lblRevenue, BorderLayout.EAST);

        root.add(headerCard, BorderLayout.NORTH);

        // Read-only table for CSV entries
        String[] columns = {"Customer", "Pizza", "Size", "Extra Cheese", "Price (€)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(GUITheme.FONT_INPUT);
        table.setForeground(GUITheme.TEXT_ESPRESSO);
        table.setRowHeight(30);
        table.setGridColor(GUITheme.BORDER_WARM);
        table.getTableHeader().setFont(GUITheme.FONT_SECTION);
        table.getTableHeader().setBackground(GUITheme.BG_WARM_TINT);
        table.getTableHeader().setForeground(GUITheme.TEXT_ESPRESSO);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(GUITheme.BORDER_WARM, 1, true));
        scrollPane.getViewport().setBackground(GUITheme.BG_CARD);
        root.add(scrollPane, BorderLayout.CENTER);

        // Bottom action bar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBackground(GUITheme.BG_CREAM);

        JButton btnClearCsv = GUITheme.createStyledButton(
                "Clear History", GUITheme.BTN_NEUTRAL, GUITheme.BTN_NEUTRAL_HV, GUITheme.TOMATO_RED
        );
        JButton btnRefresh = GUITheme.createStyledButton(
                "Refresh", GUITheme.BTN_NEUTRAL, GUITheme.BTN_NEUTRAL_HV, GUITheme.TEXT_ESPRESSO
        );
        JButton btnClose = GUITheme.createStyledButton(
                "Close Window", GUITheme.TOMATO_RED, GUITheme.TOMATO_HOVER, Color.WHITE
        );

        btnClearCsv.addActionListener(e -> verlaufLoeschen());
        btnRefresh.addActionListener(e -> ladeBestellungen());
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnClearCsv);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnClose);
        root.add(btnPanel, BorderLayout.SOUTH);

        ladeBestellungen();
    }

    // Reads bestellungen.csv line by line and populates the table
    public void ladeBestellungen() {
        tableModel.setRowCount(0);
        double summe = 0.0;
        File datei = new File(GUITheme.CSV_DATEI);

        if (!datei.exists()) {
            lblRevenue.setText("Total Revenue: 0.00 €");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(datei))) {
            String line = reader.readLine(); // Skip CSV header line
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    tableModel.addRow(parts);
                    summe += Double.parseDouble(parts[4]);
                }
            }
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Could not load orders: " + ex.getMessage(),
                    "Read Error", JOptionPane.ERROR_MESSAGE);
        }

        lblRevenue.setText(String.format(Locale.US, "Total Revenue: %.2f €", summe));
    }

    // Deletes the CSV file after confirmation
    private void verlaufLoeschen() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete all saved orders permanently?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            File datei = new File(GUITheme.CSV_DATEI);
            if (datei.exists() && datei.delete()) {
                ladeBestellungen();
            }
        }
    }
}