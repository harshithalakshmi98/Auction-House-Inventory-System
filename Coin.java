/**
 * Coin collectible keeping track of material, provenance and original face value.
 */
public class Coin extends Collectible {
    private final String material;
    private final String placeOfOrigin;
    private final double originalValue;
    private final String originalCurrency;

    // constructor to create a coin with all its coin-specific info
    public Coin(String itemId,
                String owner,
                String condition,
                double startingPrice,
                YearEstimate estimate,
                String material,
                String placeOfOrigin,
                double originalValue,
                String originalCurrency) {
        super(itemId, owner, condition, startingPrice, estimate);
        this.material = material;
        this.placeOfOrigin = placeOfOrigin;
        this.originalValue = originalValue;
        this.originalCurrency = originalCurrency;
    }

    // returns the category name
    @Override
    public String getCategory()
    {

        return "Coin";
    }

    // get what metal the coin is made of
    public String getMaterial()
    {
        return material;
    }

    // get where the coin came from originally
    public String getPlaceOfOrigin()
    {
        return placeOfOrigin;
    }

    // get the original face value
    public double getOriginalValue()
    {
        return originalValue;
    }

    // get the currency of the original value
    public String getOriginalCurrency()
    {
        return originalCurrency;
    }

    // create a short summary for the table view
    @Override
    public String getShortDescriptor()
    {
        return String.format("%s from %s (%s %.2f) (£%.2f)",
                material,
                placeOfOrigin,
                originalCurrency,
                originalValue,
                getStartingPrice());
    }

    // create a full detailed description for the popup window
    @Override
    public String getLongDescription()
    {
        return new StringBuilder()
                .append("ID: ").append(getItemId()).append("\n")
                .append("Category: ").append(getCategory()).append("\n")
                .append("Material: ").append(material).append("\n")
                .append("Place of Origin: ").append(placeOfOrigin).append("\n")
                .append(String.format("Original Value: %s %.2f%n", originalCurrency, originalValue))
                .append("Owner: ").append(getOwner()).append("\n")
                .append("Condition: ").append(getCondition()).append("\n")
                .append("Year Estimate: ").append(getYearEstimate()).append("\n")
                .append(String.format("Starting Price: £%.2f%n", getStartingPrice()))
                .toString();
    }
}


