package main.java.model;

public class HourlyFineScheme implements FineScheme {

    @Override
    public double calculateFine(long overstayHours) {
        return overstayHours * 20.0;
    }
}
