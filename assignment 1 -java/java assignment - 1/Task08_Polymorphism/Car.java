class Car extends Vehicle { 
    // Implemented: Write the constructor using super, and override ratePerHour() to return
    // 30
    Car(String regNo) { super(regNo); }
    @Override protected double ratePerHour() { return 30; }
}
