import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class InventoryTest {
    private CollectibleInventory inventory;

    @Before
    public void setUp() {
        inventory = new CollectibleInventory();
    }

    @Test
    public void emptyInventoryReturnsEmptyList() {
        List<Collectible> result = inventory.getTopSpreadItems(5);
        assertNotNull("Result should not be null", result);
        assertTrue("Result should be empty for empty inventory", result.isEmpty());
    }

    @Test
    public void singleItemReturnsSpreadZero()
    {
        YearEstimate estimate = new YearEstimate(1800, 1800); // spread = 0
        Coin coin = new Coin("C001", "Owner1", "mint", 100.0, estimate,
                "Gold", "UK", 1.0, "GBP");
        inventory.addCollectible(coin);

        List<Collectible> result = inventory.getTopSpreadItems(1);
        assertEquals("Should return 1 item", 1, result.size());
        assertEquals("Should return the coin", coin, result.get(0));
    }

    @Test
    public void ordersByDescendingSpread()
    {
        // Create items with different spreads
        YearEstimate smallSpread = new YearEstimate(1900, 1910); // spread = 10
        YearEstimate mediumSpread = new YearEstimate(1800, 1850); // spread = 50
        YearEstimate largeSpread = new YearEstimate(1700, 1800); // spread = 100

        Coin coin1 = new Coin("C001", "Owner1", "mint", 100.0, smallSpread,
                "Silver", "UK", 1.0, "GBP");
        Coin coin2 = new Coin("C002", "Owner2", "restored", 200.0, mediumSpread,
                "Gold", "France", 2.0, "EUR");
        Coin coin3 = new Coin("C003", "Owner3", "mint", 300.0, largeSpread,
                "Bronze", "Italy", 3.0, "EUR");

        inventory.addCollectible(coin1);
        inventory.addCollectible(coin2);
        inventory.addCollectible(coin3);

        List<Collectible> result = inventory.getTopSpreadItems(3);
        assertEquals("Should return 3 items", 3, result.size());
        // Should be ordered by spread descending: 100, 50, 10
        assertEquals("First item should have largest spread", coin3, result.get(0));
        assertEquals("Second item should have medium spread", coin2, result.get(1));
        assertEquals("Third item should have smallest spread", coin1, result.get(2));
    }

    @Test
    public void limitsToRequestedCount()
    {
        YearEstimate estimate1 = new YearEstimate(1800, 1850); // spread = 50
        YearEstimate estimate2 = new YearEstimate(1900, 1920); // spread = 20
        YearEstimate estimate3 = new YearEstimate(1950, 2000); // spread = 50

        Coin coin1 = new Coin("C001", "Owner1", "mint", 100.0, estimate1,
                "Gold", "UK", 1.0, "GBP");
        Coin coin2 = new Coin("C002", "Owner2", "restored", 200.0, estimate2,
                "Silver", "France", 2.0, "EUR");
        Coin coin3 = new Coin("C003", "Owner3", "mint", 300.0, estimate3,
                "Bronze", "Italy", 3.0, "EUR");

        inventory.addCollectible(coin1);
        inventory.addCollectible(coin2);
        inventory.addCollectible(coin3);

        List<Collectible> result = inventory.getTopSpreadItems(2);
        assertEquals("Should return only 2 items when requested", 2, result.size());
    }

}
