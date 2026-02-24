package main.java.model;

/**
 * FUTURE-PROOFING: A fine scheme that applies a cap (maximum) to any fine amount.
 * 
 * This demonstrates the Decorator pattern - it wraps another FineScheme and ensures
 * the fine does not exceed a maximum limit. This is useful for customer fairness
 * and predictable maximum penalties.
 * 
 * To activate this feature:
 * 1. Uncomment this class
 * 2. Add "Capped" option to AdminPanel's fine scheme combo box
 * 3. Handle the "Capped" case in the button's action listener
 * 
 * Example usage:
 *   fineService.setFineScheme(new CappedFineScheme(new HourlyFineScheme(), 500.0));
 *   // This will apply hourly fines but cap them at RM500
 * 
 * @author Future-Proof Implementation
 */
public class CappedFineScheme implements FineScheme {

    private final FineScheme baseScheme;
    private final double maxFine;

    /**
     * Creates a capped fine scheme that wraps another scheme.
     * 
     * @param baseScheme The underlying fine scheme (e.g., ProgressiveFineScheme, HourlyFineScheme)
     * @param maxFine    The maximum fine allowed (e.g., 500.0)
     */
    public CappedFineScheme(FineScheme baseScheme, double maxFine) {
        this.baseScheme = baseScheme;
        this.maxFine = maxFine;
    }

    /**
     * Calculates the fine, ensuring it doesn't exceed the maximum cap.
     * 
     * @param overstayHours The number of hours the vehicle has overstayed
     * @return The calculated fine, capped at maxFine
     */
    @Override
    public double calculateFine(long overstayHours) {
        double rawFine = baseScheme.calculateFine(overstayHours);
        return Math.min(rawFine, maxFine);
    }
}
