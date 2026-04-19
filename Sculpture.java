/**
 * Sculpture collectible capturing subject, material and physical height.
 */
public class Sculpture extends Collectible {
    private final String subject;
    private final String material;
    private final double heightCm;

    // constructor to create a sculpture with its specific details
    public Sculpture(String itemId,
                     String owner,
                     String condition,
                     double startingPrice,
                     YearEstimate estimate,
                     String subject,
                     String material,
                     double heightCm) {
        super(itemId, owner, condition, startingPrice, estimate);
        this.subject = subject;
        this.material = material;
        this.heightCm = heightCm;
    }

    // returns the category name
    @Override
    public String getCategory()
    {
        return "Sculpture";
    }

    // get what the sculpture represents
    public String getSubject()
    {
        return subject;
    }

    // get what material its made from
    public String getMaterial()
    {
        return material;
    }

    // get the height in cm
    public double getHeightCm()
    {
        return heightCm;
    }

    // create a short summary for the table
    @Override
    public String getShortDescriptor()
    {
        return String.format("%s of %s in %s (£%.2f)",
                getCategory(),
                subject,
                material,
                getStartingPrice());
    }

    // create a full detailed description for the popup
    @Override
    public String getLongDescription()
    {
        return new StringBuilder()
                .append("ID: ").append(getItemId()).append("\n")
                .append("Category: ").append(getCategory()).append("\n")
                .append("Subject: ").append(subject).append("\n")
                .append("Material: ").append(material).append("\n")
                .append(String.format("Height: %.1f cm%n", heightCm))
                .append("Owner: ").append(getOwner()).append("\n")
                .append("Condition: ").append(getCondition()).append("\n")
                .append("Year Estimate: ").append(getYearEstimate()).append("\n")
                .append(String.format("Starting Price: £%.2f%n", getStartingPrice()))
                .toString();
    }
}


