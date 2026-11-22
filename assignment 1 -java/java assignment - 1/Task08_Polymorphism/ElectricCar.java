class ElectricCar extends Car implements Chargeable { 
    ElectricCar(String regNo) { super(regNo); } 

    // Implemented: Implement chargingFee() at Rs 12 per kWh
    @Override public double chargingFee(int kWh) { return kWh * 12; }
}
