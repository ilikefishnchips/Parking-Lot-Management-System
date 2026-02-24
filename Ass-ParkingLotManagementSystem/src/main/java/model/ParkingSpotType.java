package main.java.model;

/**
 * FUTURE-PROOFING: Enum representing different parking spot types.
 * 
 * To add a new spot type (e.g., ELECTRIC):
 * 1. Add the new constant to this enum
 * 2. Update ConfigPanel's combo box to include the new type
 * 3. Update vehicle classes' canParkIn() methods to handle the new type
 * 4. Update ParkingLot's spot counting logic if needed
 */
public enum ParkingSpotType {
    COMPACT,
    REGULAR,
    HANDICAPPED,
    RESERVED,
    // FUTURE-PROOFING: Adding ELECTRIC spot type
    // To activate: Uncomment the line below
    // ELECTRIC
}