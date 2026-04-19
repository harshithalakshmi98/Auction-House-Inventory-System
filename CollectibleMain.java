import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Entry point used in both command line demonstrations and the Swing prototype.
 */
public class CollectibleMain
{
    private static final String DEFAULT_SOURCE_FILE = "collectibles_data.csv";
    private static final String STATISTICS_FILE = "inventory_statistics.txt";

    // main entry point - loads data, prints summary, and opens the GUI
    public static void main(String[] args)
    {
        System.out.println("========================================");
        System.out.println("Auction House Inventory System");
        System.out.println("========================================");

        String sourceFile = args.length > 0 ? args[0] : DEFAULT_SOURCE_FILE;
        CollectibleInventory inventory = new CollectibleInventory();

        System.out.println("Loading inventory from: " + sourceFile);
        int[] counts = loadFromCsv(sourceFile, inventory);
        if (counts == null)
        {
            return;
        }

        System.out.println("Loaded items: " + counts[0]);
        if (counts[1] > 0)
        {
            System.out.println("Skipped lines due to validation errors: " + counts[1]);
        }
        printSummary(inventory);

        try
        {
            inventory.generateStatisticsReport(STATISTICS_FILE);
            System.out.println("Statistics written to: " + STATISTICS_FILE);
        } catch (Exception e) {
            System.err.println("Unable to write statistics: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() ->
        {
            InventoryAppFrame frame = new InventoryAppFrame(inventory, sourceFile);
            frame.showWindow();
        });
    }

    // read the CSV file and load all collectibles into the inventory, returns [loaded count, skipped count]
    private static int[] loadFromCsv(String filePath, CollectibleInventory inventory)
    {
        int loaded = 0;
        int skipped = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            String rawLine;
            int lineNumber = 0;
            while ((rawLine = reader.readLine()) != null)
            {
                lineNumber++;
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#"))
                {
                    continue;
                }
                String[] columns = line.split(",");
                try
                {
                    Collectible item = buildCollectible(columns);
                    inventory.addCollectible(item);
                    loaded++;
                } catch (IllegalArgumentException ex)
                {
                    skipped++;
                    System.err.println("Line " + lineNumber + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to read file '" + filePath + "': " + e.getMessage());
            return null;
        }
        return new int[]{loaded, skipped};
    }

    // figure out what type of collectible this is and call the right builder method
    //Determines collectible type from first column
    private static Collectible buildCollectible(String[] columns)
    {
        if (columns.length == 0)
        {
            throw new IllegalArgumentException("Empty line.");
        }
        String type = columns[0].trim().toLowerCase();
        switch (type) {
            case "furniture":
                return buildFurniture(columns);
            case "sculpture":
                return buildSculpture(columns);
            case "coin":
                return buildCoin(columns);
            default:
                throw new IllegalArgumentException("Unsupported collectible type: " + columns[0]);
        }
    }

    // parse the CSV columns and create a Furniture object
    private static Collectible buildFurniture(String[] c)
    {
        expectColumns(c, 13, "furniture"); //verifies correct number of fields for each type
        String id = trim(c[1]);
        String furnitureType = trim(c[2]);
        String style = trim(c[3]);
        String maker = trim(c[4]);
        double length = parseDouble(c[5], "length");
        double height = parseDouble(c[6], "height");
        double depth = parseDouble(c[7], "depth");
        YearEstimate estimate = buildEstimate(c[8], c[9]);
        String owner = trim(c[10]);
        String condition = normaliseCondition(c[11]); //Validates condition is one of: "mint", "restored", "needs restoring"
        double price = parseDouble(c[12], "starting price");
        return new Furniture(id, owner, condition, price, estimate,
                furnitureType, style, maker, length, height, depth);
    }

    // parse the CSV columns and create a Sculpture object
    private static Collectible buildSculpture(String[] c)
    {
        expectColumns(c, 10, "sculpture");
        String id = trim(c[1]);
        String subject = trim(c[2]);
        String material = trim(c[3]);
        double height = parseDouble(c[4], "height");
        YearEstimate estimate = buildEstimate(c[5], c[6]);
        String owner = trim(c[7]);
        String condition = normaliseCondition(c[8]);
        double price = parseDouble(c[9], "starting price");
        return new Sculpture(id, owner, condition, price, estimate, subject, material, height);
    }

    // parse the CSV columns and create a Coin object
    private static Collectible buildCoin(String[] c)
    {
        expectColumns(c, 11, "coin");
        String id = trim(c[1]);
        String material = trim(c[2]);
        String origin = trim(c[3]);
        double originalValue = parseDouble(c[4], "original value");
        String currency = trim(c[5]);
        YearEstimate estimate = buildEstimate(c[6], c[7]);
        String owner = trim(c[8]);
        String condition = normaliseCondition(c[9]);
        double price = parseDouble(c[10], "starting price");
        return new Coin(id, owner, condition, price, estimate, material, origin, originalValue, currency);
    }

    // clean up the condition string and check if its valid
    private static String normaliseCondition(String value)
    {
        String cleaned = trim(value).toLowerCase();
        if (!CollectibleInventory.isValidCondition(cleaned))
        {
            throw new IllegalArgumentException("Condition must be mint, restored or needs restoring.");
        }
        return cleaned;
    }

    // parse the low and high year strings and create a YearEstimate object
    private static YearEstimate buildEstimate(String low, String high)
    {
        int lowYear = parseInt(low, "low year estimate");
        int highYear = parseInt(high, "high year estimate");
        return new YearEstimate(lowYear, highYear);
    }

    // try to parse a string as a double, throw error if it fails
    private static double parseDouble(String value, String label)
    {
        try
        {
            return Double.parseDouble(trim(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + label + ": '" + value + "'");
        }
    }

    // try to parse a string as an int, throw error if it fails
    private static int parseInt(String value, String label) {

        try
        {
            return Integer.parseInt(trim(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + label + ": '" + value + "'");
        }
    }

    // check that we have the right number of columns for this collectible type
    private static void expectColumns(String[] columns, int expected, String type)
    {
        if (columns.length != expected)
        {
            throw new IllegalArgumentException("Expected " + expected + " fields for " + type + " but found " + columns.length);
        }
    }

    // remove whitespace from a string, return empty string if null
    private static String trim(String raw)
    {
        return raw == null ? "" : raw.trim();
    }

    // print a summary of the inventory to the console
    private static void printSummary(CollectibleInventory inventory)
    {
        System.out.println();
        System.out.println("Inventory Summary");
        System.out.println("-----------------");
        System.out.println("Total items: " + inventory.getTotalCount());
        System.out.println("Unique owners: " + inventory.getUniqueOwners().size());

        inventory.findOldestByLowEstimate().ifPresent(item ->  //to safely handle empty results
                {
            System.out.println("Oldest span (low): " + item.getShortDescriptor()
                    + " -> " + item.getYearEstimate().getLowYear()
                    + " (middle " + item.getYearEstimate().getMiddleYear() + ")");
        }
        );
        inventory.findNewestByHighEstimate().ifPresent(item ->
        {
            System.out.println("Newest span (high): " + item.getShortDescriptor()
                    + " -> " + item.getYearEstimate().getHighYear()
                    + " (middle " + item.getYearEstimate().getMiddleYear() + ")");
        }
        );

        System.out.printf("Average price: £%.2f%n", inventory.getAveragePrice());
        System.out.printf("Price standard deviation: £%.2f%n", inventory.getPriceStandardDeviation());

        System.out.println();
        System.out.println("Top spreads:");
        List<Collectible> topSpreads = inventory.getTopSpreadItems(3);
        if (topSpreads.isEmpty())
        {
            System.out.println("  No collectibles available.");
        } else {
            int index = 1;
            for (Collectible item : topSpreads) {
                YearEstimate estimate = item.getYearEstimate();
                System.out.println(index + ". " + item.getShortDescriptor()
                        + " (" + estimate.getLowYear() + "-" + estimate.getHighYear()
                        + ", spread " + estimate.getSpread() + ")");
                index++;
            }
        }
        System.out.println();
    }
}

