import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple table model used by the Swing prototype to list collectibles.
 */
public class CollectibleTableModel extends AbstractTableModel
{
    private final String[] headers = {
            "ID", "Category", "Summary", "Owner", "Condition",
            "Start Price (£)", "Low Year", "High Year", "Middle Year"
    };

    private final List<Collectible> rows = new ArrayList<>();

    // replace all the items in the table with a new list
    public void setItems(List<Collectible> items)
    {
        rows.clear();
        rows.addAll(items);
        fireTableDataChanged();
    }

    // get the collectible item at a specific row index
    public Collectible getAt(int row)
    {
        if (row < 0 || row >= rows.size())
        {
            return null;
        }
        return rows.get(row);
    }

    // tell the table how many rows we have
    @Override
    public int getRowCount()
    {
        return rows.size();
    }

    // tell the table how many columns we have
    @Override
    public int getColumnCount()
    {
        return headers.length;
    }

    // get the column header name for a given column index
    @Override
    public String getColumnName(int column)
    {
        return headers[column];
    }

    // get the value to display in a specific cell of the table
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        Collectible item = rows.get(rowIndex);
        YearEstimate estimate = item.getYearEstimate();
        switch (columnIndex) {
            case 0:
                return item.getItemId();
            case 1:
                return item.getCategory();
            case 2:
                return item.getShortDescriptor();
            case 3:
                return item.getOwner();
            case 4:
                return item.getCondition();
            case 5:
                return String.format("£%.2f", item.getStartingPrice());
            case 6:
                return estimate.getLowYear();
            case 7:
                return estimate.getHighYear();
            case 8:
                return estimate.getMiddleYear();
            default:
                return "";
        }
    }
}


