import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages the list of collectibles and provides statistics
 */
public class CollectibleInventory
{
    public static final String[] VALID_CONDITIONS = {"mint", "restored", "needs restoring"};

    private final ArrayList<Collectible> inventory;

    // create a new empty inventory
    public CollectibleInventory()
    {
        this.inventory = new ArrayList<>();
    }

    // check if a condition string is one of the valid options
    public static boolean isValidCondition(String value)
    {
        if (value == null) {
            return false;
        }
        for (String option : VALID_CONDITIONS) {
            if (option.equalsIgnoreCase(value.trim())) {
                return true;
            }
        }
        return false;
    }

    // add a new collectible to the inventory
    public void addCollectible(Collectible item)
    {
        inventory.add(item);
    }

    // get a copy of all items in the inventory
    public List<Collectible> getAll()
    {
        return new ArrayList<>(inventory);
    }
    // count how many items we have
    public int getTotalCount()
    {
        return inventory.size();
    }

    // find the item with the lowest year estimate
    public Optional<Collectible> findOldestByLowEstimate()
    {
        return inventory.stream()
                .min(Comparator.comparingInt(c -> c.getYearEstimate().getLowYear()));
    }

    // find the item with the highest year estimate
    public Optional<Collectible> findNewestByHighEstimate()
    {
        return inventory.stream()
                .max(Comparator.comparingInt(c -> c.getYearEstimate().getHighYear()));
    }

    // find the most expensive item by starting price
    public Optional<Collectible> findMostExpensive()
    {
        return inventory.stream()
                .max(Comparator.comparingDouble(Collectible::getStartingPrice));
    }

    // find the cheapest item by starting price
    public Optional<Collectible> findLeastExpensive()
    {

        return inventory.stream()
                .min(Comparator.comparingDouble(Collectible::getStartingPrice));
    }

    // get a set of all unique owner names
    public Set<String> getUniqueOwners()
    {
        return inventory.stream()
                .map(Collectible::getOwner)
                .collect(Collectors.toSet());
    }

    // calculate the average price of all items
    public double getAveragePrice()
    {
        return inventory.stream()
                .mapToDouble(Collectible::getStartingPrice)
                .average()
                .orElse(0);
    }

    // calculate standard deviation of prices for statistics
    public double getPriceStandardDeviation()
    {
        int count = inventory.size();
        if (count == 0)
        {
            return 0;
        }
        double average = getAveragePrice();
        double variance = inventory.stream()
                .mapToDouble(item -> Math.pow(item.getStartingPrice() - average, 2))
                .sum() / count;
        return Math.sqrt(variance);
    }

    // count how many items are in each condition category
    public Map<String, Long> getConditionBreakdown()
    {
        return inventory.stream()
                .collect(Collectors.groupingBy(item -> item.getCondition().toLowerCase(), Collectors.counting()));
    }

    // get the top N items with the biggest difference between high and low year estimates
    public List<Collectible> getTopSpreadItems(int count)
    {
        return inventory.stream()
                .sorted(Comparator.comparingInt((Collectible c) -> c.getYearEstimate().getSpread()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // sort all items by their ID in ascending order
    public void sortById()
    {
        inventory.sort(Comparator.comparing(Collectible::getItemId));
    }
    // sort all items by price from lowest to highest
    public void sortByPrice()
    {
        inventory.sort(Comparator.comparingDouble(Collectible::getStartingPrice));
    }

    // sort all items by middle year estimate from oldest to newest
    public void sortByMiddleYear()
    {
        inventory.sort(Comparator.comparingInt(c -> c.getYearEstimate().getMiddleYear()));
    }
    // write a full statistics report to a text file
    public void generateStatisticsReport(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename)))
        {
            writer.println("========================================");
            writer.println("AUCTION HOUSE INVENTORY STATISTICS");
            writer.println("========================================");
            writer.println();
            writer.println("Total Items in Inventory: " + getTotalCount());
            writer.println("Unique Owners Recorded: " + getUniqueOwners().size());
            writer.println();

            findOldestByLowEstimate().ifPresent(oldest -> {
                writer.println("Oldest Item (low estimate " + oldest.getYearEstimate().getLowYear() + "):");
                writer.println("  " + oldest.getShortDescriptor());
                writer.println("  Middle Year Estimate: " + oldest.getYearEstimate().getMiddleYear());
                writer.println();
            });

            findNewestByHighEstimate().ifPresent(newest -> {
                writer.println("Newest Item (high estimate " + newest.getYearEstimate().getHighYear() + "):");
                writer.println("  " + newest.getShortDescriptor());
                writer.println("  Middle Year Estimate: " + newest.getYearEstimate().getMiddleYear());
                writer.println();
            });

            findMostExpensive().ifPresent(item -> {
                writer.println(String.format("Most Expensive Item (£%.2f):", item.getStartingPrice()));
                writer.println("  " + item.getShortDescriptor());
                writer.println();
            });

            findLeastExpensive().ifPresent(item -> {
                writer.println(String.format("Least Expensive Item (£%.2f):", item.getStartingPrice()));
                writer.println("  " + item.getShortDescriptor());
                writer.println();
            });

            writer.println("Price Statistics:");
            writer.printf("  Average Price: £%.2f%n", getAveragePrice());
            writer.printf("  Standard Deviation: £%.2f%n", getPriceStandardDeviation());
            writer.println();

            writer.println("Condition Breakdown:");
            Map<String, Long> conditionBreakdown = new HashMap<>(getConditionBreakdown());
            for (String status : VALID_CONDITIONS)
            {
                long count = conditionBreakdown.getOrDefault(status, 0L);
                writer.println("  " + status + ": " + count + " items");
            }
            writer.println();

            writer.println("Top 3 Year Estimate Spreads:");
            List<Collectible> topSpread = getTopSpreadItems(3);
            if (topSpread.isEmpty()) {
                writer.println("  No data available.");
            } else {
                for (Collectible item : topSpread) {
                    YearEstimate estimate = item.getYearEstimate();
                    writer.println("  " + item.getShortDescriptor());
                    writer.println("    Low/High: " + estimate.getLowYear() + "/" + estimate.getHighYear()
                            + " (spread " + estimate.getSpread() + ")");
                }
            }
            writer.println();

            writer.println("========================================");
            writer.println("Report generated on: " + new Date());
            writer.println("========================================");
        }
    }

    // save the entire inventory back to a CSV file
    public void saveToCsv(String filePath) throws IOException
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath)))
        {
            for (Collectible item : inventory) {
                writer.println(toCsvLine(item));
            }
        }
    }

    // convert a collectible item back into a CSV line format
    private String toCsvLine(Collectible item)
    {
        YearEstimate estimate = item.getYearEstimate();
        if (item instanceof Furniture) {
            Furniture f = (Furniture) item;
            return String.format("furniture,%s,%s,%s,%s,%.1f,%.1f,%.1f,%d,%d,%s,%s,%.2f",
                    f.getItemId(),
                    f.getFurnitureType(),
                    f.getStyle(),
                    f.getMaker(),
                    f.getLengthCm(),
                    f.getHeightCm(),
                    f.getDepthCm(),
                    estimate.getLowYear(),
                    estimate.getHighYear(),
                    f.getOwner(),
                    f.getCondition(),
                    f.getStartingPrice());
        }
        if (item instanceof Sculpture)
        {
            Sculpture s = (Sculpture) item;
            return String.format("sculpture,%s,%s,%s,%.1f,%d,%d,%s,%s,%.2f",
                    s.getItemId(),
                    s.getSubject(),
                    s.getMaterial(),
                    s.getHeightCm(),
                    estimate.getLowYear(),
                    estimate.getHighYear(),
                    s.getOwner(),
                    s.getCondition(),
                    s.getStartingPrice());
        }
        if (item instanceof Coin)
        {
            Coin c = (Coin) item;
            return String.format("coin,%s,%s,%s,%.2f,%s,%d,%d,%s,%s,%.2f",
                    c.getItemId(),
                    c.getMaterial(),
                    c.getPlaceOfOrigin(),
                    c.getOriginalValue(),
                    c.getOriginalCurrency(),
                    estimate.getLowYear(),
                    estimate.getHighYear(),
                    c.getOwner(),
                    c.getCondition(),
                    c.getStartingPrice());
        }
        throw new IllegalArgumentException("Unknown collectible type: " + item.getClass().getSimpleName());
    }
}

