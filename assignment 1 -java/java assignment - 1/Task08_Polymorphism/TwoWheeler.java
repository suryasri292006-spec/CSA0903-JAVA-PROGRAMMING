class TwoWheeler extends Vehicle { 
    TwoWheeler(String regNo) { super(regNo); } 
    @Override protected double ratePerHour() { return 10; } 
}
