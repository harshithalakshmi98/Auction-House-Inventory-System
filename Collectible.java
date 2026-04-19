import java.util.Objects;

/**
 * Root type for every collectible item stored in the inventory.
 * Each subclass adds its own specific fields (e.g., dimensions for furniture).
 */
public abstract class Collectible
{
    private final String itemId;
    private String owner;
    private String condition;
    private double startingPrice;
    private final YearEstimate yearEstimate;

    // constructor to set up basic collectible info
    protected Collectible(String itemId,
                          String owner,
                          String condition,
                          double startingPrice,
                          YearEstimate yearEstimate)
    {
        this.itemId = Objects.requireNonNull(itemId, "itemId");
        this.owner = Objects.requireNonNull(owner, "owner");
        this.condition = Objects.requireNonNull(condition, "condition");
        this.yearEstimate = Objects.requireNonNull(yearEstimate, "yearEstimate");
        this.startingPrice = startingPrice;
    }

    // get the unique id for this item
    public String getItemId()
    {
        return itemId;
    }

    // get who owns this collectible
    public String getOwner()
    {
        return owner;
    }

    // change the owner name
    public void setOwner(String owner)

    {
        this.owner = Objects.requireNonNull(owner, "owner");
    }

    // get the condition status
    public String getCondition()
    {
        return condition;
    }

    // update the condition
    public void setCondition(String condition)
    {
        this.condition = Objects.requireNonNull(condition, "condition");
    }

    // get the starting auction price
    public double getStartingPrice()
    {
        return startingPrice;
    }

    // set a new starting price, but it cant be negative
    public void setStartingPrice(double startingPrice)
    {
        if (startingPrice < 0)
        {
            throw new IllegalArgumentException("Starting price cannot be negative.");
        }
        this.startingPrice = startingPrice;
    }

    // get the year estimate object with low and high years
    public YearEstimate getYearEstimate()
    {
        return yearEstimate;
    }

    // each subclass needs to say what category it is
    public abstract String getCategory();

    // each subclass needs to give a short summary for the table
    public abstract String getShortDescriptor();

    // each subclass needs to give full details for the popup
    public abstract String getLongDescription();
}

