package main.java.model;

public class FixedFineScheme implements FineScheme {

    @Override
    public double calculateFine(long overstayHours) {
        return 50.0;
    }
}
