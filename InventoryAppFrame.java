import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;

/**
 * Minimal Swing interface to showcase the inventory features
 */
public class InventoryAppFrame extends JFrame {
    private final CollectibleInventory inventory;
    private final String dataFilePath;
    private final CollectibleTableModel tableModel;
    private final JTable table;

    // constructor to set up the GUI window with the inventory data
    public InventoryAppFrame(CollectibleInventory inventory, String dataFilePath)
    {
        super("Auction House Inventory Prototype");
        this.inventory = inventory;
        this.dataFilePath = dataFilePath;
        this.tableModel = new CollectibleTableModel();
        this.table = new JTable(tableModel);

        buildUi();
        refreshTable();
    }

    // make the window visible and center it on screen
    public void showWindow()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // set up all the buttons and table layout
    private void buildUi()
    {
        setLayout(new BorderLayout(10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new GridLayout(2, 4, 8, 8));

        JButton moreInfoButton = new JButton("More Info");
        moreInfoButton.addActionListener(e -> showMoreInfo());
        controls.add(moreInfoButton);

        JButton editButton = new JButton("Edit Item");
        editButton.addActionListener(e -> showEditDialog());
        controls.add(editButton);

        JButton sortByIdButton = new JButton("Sort by ID");
        sortByIdButton.addActionListener(e -> {
            inventory.sortById();
            refreshTable();
        });
        controls.add(sortByIdButton);

        JButton sortByPriceButton = new JButton("Sort by Price");
        sortByPriceButton.addActionListener(e -> {
            inventory.sortByPrice();
            refreshTable();
        });
        controls.add(sortByPriceButton);

        JButton sortByYearButton = new JButton("Sort by Year Estimate");
        sortByYearButton.addActionListener(e ->
        {
            inventory.sortByMiddleYear();
            refreshTable();
        });
        controls.add(sortByYearButton);

        JButton statsButton = new JButton("Generate Statistics");
        statsButton.addActionListener(e -> generateStatistics());
        controls.add(statsButton);

        JButton saveButton = new JButton("Save Inventory");
        saveButton.addActionListener(e -> saveInventory());
        controls.add(saveButton);

        JButton refreshButton = new JButton("Reload View");
        refreshButton.addActionListener(e -> refreshTable());
        controls.add(refreshButton);

        add(controls, BorderLayout.SOUTH);
    }

    // reload the table data from the inventory and refresh the display
    private void refreshTable()
    {
        List<Collectible> snapshot = inventory.getAll();
        tableModel.setItems(snapshot);
        if (!snapshot.isEmpty())
        {
            table.setRowSelectionInterval(0, 0);
        }
    }

    // show a popup window with full details of the selected item
    private void showMoreInfo()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0)
        {
            JOptionPane.showMessageDialog(this, "Please select an item first.", "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Collectible item = tableModel.getAt(selectedRow);
        JTextArea textArea = new JTextArea(item.getLongDescription());
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 250));
        JOptionPane.showMessageDialog(this, scrollPane, "Item details", JOptionPane.INFORMATION_MESSAGE);
    }

    // open a dialog to edit the price and condition of the selected item
    private void showEditDialog()
    {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "No selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Collectible item = tableModel.getAt(selectedRow);
        JTextField priceField = new JTextField(String.format("%.2f", item.getStartingPrice()));
        JComboBox<String> conditionBox = new JComboBox<>(CollectibleInventory.VALID_CONDITIONS);
        conditionBox.setSelectedItem(item.getCondition());
        conditionBox.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new javax.swing.JLabel("Starting price (£):"));
        panel.add(priceField);
        panel.add(new javax.swing.JLabel("Condition:"));
        panel.add(conditionBox);

        int choice = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Edit " + item.getItemId(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice == JOptionPane.OK_OPTION)
        {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                String newCondition = conditionBox.getSelectedItem().toString();
                if (!CollectibleInventory.isValidCondition(newCondition)) {
                    throw new IllegalArgumentException("Condition must be mint, restored or needs restoring.");
                }
                item.setStartingPrice(price);
                item.setCondition(newCondition);
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid number for the price.",
                        "Invalid value",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Invalid value",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // generate the statistics report file and show a success message
    private void generateStatistics()
    {
        try
        {
            inventory.generateStatisticsReport("inventory_statistics.txt");
            JOptionPane.showMessageDialog(this,
                    "Statistics file updated (inventory_statistics.txt).",
                    "Statistics generated",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this,
                    "Unable to write the statistics file.\n" + ex.getMessage(),
                    "Write error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // save the current inventory state back to the CSV file
    private void saveInventory() {
        try
        {
            inventory.saveToCsv(dataFilePath);
            JOptionPane.showMessageDialog(this,
                    "Inventory saved back to " + dataFilePath,
                    "Save completed",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this,
                    "Unable to save the inventory.\n" + ex.getMessage(),
                    "Save error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}


