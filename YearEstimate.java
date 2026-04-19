/**
 * Captures the low and high estimate for the year of origin of a collectible.
 * The class is immutable to prevent accidental drift between the paired values.
 */
public final class YearEstimate {
    private final int lowYear;
    private final int highYear;

    // constructor that takes low and high year estimates and validates them
    public YearEstimate(int lowYear, int highYear)
    {
        if (lowYear <= 0 || highYear <= 0) {
            throw new IllegalArgumentException("Year estimates must be positive.");
        }
        if (lowYear > highYear) {
            throw new IllegalArgumentException("Low estimate cannot exceed the high estimate.");
        }
        this.lowYear = lowYear;
        this.highYear = highYear;
    }

    // get the lower year estimate
    public int getLowYear()
    {
        return lowYear;
    }

    // get the higher year estimate
    public int getHighYear()
    {
        return highYear;
    }

    // calculate the middle year by averaging low and high, rounded to whole number
    public int getMiddleYear()
    {
        return (int) Math.round((lowYear + highYear) / 2.0);
    }

    // calculate how many years difference between high and low estimate
    public int getSpread()
    {
        return highYear - lowYear;
    }

    // convert to a readable string format
    @Override
    public String toString()
    {
        return lowYear + " - " + highYear + " (≈" + getMiddleYear() + ")";
    }
}


