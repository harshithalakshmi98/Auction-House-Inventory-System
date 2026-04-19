/**
 * Furniture collectible with the detailed attributes
 */
public class Furniture extends Collectible
{
    private final String furnitureType;
    private final String style;
    private final String maker;
    private final double lengthCm;
    private final double heightCm;
    private final double depthCm;

    // constructor to create a furniture item with all its details
    public Furniture(String itemId,
                     String owner,
                     String condition,
                     double startingPrice,
                     YearEstimate estimate,
                     String furnitureType,
                     String style,
                     String maker,
                     double lengthCm,
                     double heightCm,
                     double depthCm) {
        super(itemId, owner, condition, startingPrice, estimate);
        this.furnitureType = furnitureType;
        this.style = style;
        this.maker = maker;
        this.lengthCm = lengthCm;
        this.heightCm = heightCm;
        this.depthCm = depthCm;
    }

    // returns the category name
    @Override
    public String getCategory()
    {
        return "Furniture";
    }

    // get what type of furniture this is
    public String getFurnitureType()
    {
        return furnitureType;
    }

    // get the style period
    public String getStyle()
    {
        return style;
    }

    // get who made it
    public String getMaker()
    {
        return maker;
    }

    // get length in cm
    public double getLengthCm()
    {
        return lengthCm;
    }

    // get height in cm
    public double getHeightCm()
    {
        return heightCm;
    }

    // get depth in cm
    public double getDepthCm()
    {
        return depthCm;
    }

    // calculate the volume by multiplying all dimensions
    public double estimateVolume()
    {
        return lengthCm * heightCm * depthCm;
    }

    // create a short summary line for the table view
    @Override
    public String getShortDescriptor()
    {
        return String.format("%s %s by %s (£%.2f)",
                getCategory(),
                furnitureType,
                maker,
                getStartingPrice());
    }

    // create a full detailed description for the popup window
    @Override
    public String getLongDescription()
    {
        return new StringBuilder()
                .append("ID: ").append(getItemId()).append("\n")
                .append("Category: ").append(getCategory()).append("\n")
                .append("Furniture Type: ").append(furnitureType).append("\n")
                .append("Style: ").append(style).append("\n")
                .append("Maker: ").append(maker).append("\n")
                .append(String.format("Dimensions (cm): %.1f × %.1f × %.1f%n", lengthCm, heightCm, depthCm))
                .append("Owner: ").append(getOwner()).append("\n")
                .append("Condition: ").append(getCondition()).append("\n")
                .append("Year Estimate: ").append(getYearEstimate()).append("\n")
                .append(String.format("Starting Price: £%.2f%n", getStartingPrice()))
                .toString();
    }
}


