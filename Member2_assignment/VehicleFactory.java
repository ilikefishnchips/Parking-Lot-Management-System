package Member2_assignment;

public class VehicleFactory {
    
    public static Vehicle createVehicle(String type, String plate) {
        switch (type.toLowerCase()) {
            case "motorcycle":
                return new Motorcycle(plate);
            case "car":
                return new Car(plate);
            case "suv":
            case "truck":
                return new SUV(plate);
            case "handicapped":
                return new HandicappedVehicle(plate);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}