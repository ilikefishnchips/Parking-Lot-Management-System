package Member2_assignment;

// You can put multiple classes in one file if they are not 'public'
// Or save these as separate files. Ideally, save this file as "Motorcycle.java"
// and I will separate them below for easier copying.

// --- COPY THESE INTO SEPARATE FILES IF YOU PREFER, OR KEEP ALL IN ONE FILE REMOVING 'public' ---
// For simplicity, here are the classes defined simply. 
// IF YOU GET ERRORS, put each class in its own file named after the class.

class Motorcycle extends Vehicle {
    public Motorcycle(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.COMPACT;
    }
}

class Car extends Vehicle {
    public Car(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.COMPACT || spotType == ParkingSpotType.REGULAR;
    }
}

class SUV extends Vehicle {
    public SUV(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return spotType == ParkingSpotType.REGULAR;
    }
}

class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate) { super(plate); }

    @Override
    public boolean canParkIn(ParkingSpotType spotType) {
        return true; 
    }
}