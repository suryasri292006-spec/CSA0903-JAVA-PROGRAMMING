# System UML Class Diagram (Tasks 11 and 12)

Classes marked *(T12)* were added in Task 12; all others are unchanged from Task 11.

```mermaid
classDiagram
    class Vehicle {
        <<abstract>>
        -String regNo
        +validateRegNo(String)$ String
        +getRegNo() String
        #ratePerHour()* double
        +calculateFee(long minutes) double
        +calculateFee(LocalDateTime, LocalDateTime) double
        +equals(Object) boolean
        +hashCode() int
    }
    class TwoWheeler
    class Car
    class ElectricCar {
        +chargingFee(int kWh) double
    }
    class EBike {
        +calculateFee(long) double
    }
    class DeliveryVan {
        +calculateFee(LocalDateTime, LocalDateTime) double
        +peakMinutes(entry, exit)$ long
    }
    class Chargeable {
        <<interface>>
        +chargingFee(int kWh) double
    }
    class Pass {
        <<interface>>
        +getRegNo() String
        +covers(entry, exit) boolean
        +getPrice() double
        +describe() String
    }
    class MonthlyPass
    class DayPass
    class UserType {
        <<enumeration>>
        STUDENT
        STAFF
        VISITOR
    }
    class ParkingTicket {
        -String ticketNo
        -int floor
        -int slot
        -LocalDateTime entryTime
    }
    class Receipt {
        <<record>>
        +total() double
    }
    class ParkingFacility {
        -String[][] slots
        +allocate(String) int[]
        +release(int, int) boolean
    }
    class VehicleFactory {
        +register(String, Function)
        +create(String, String) Vehicle
    }
    class ParkingSystem {
        +enter(...) ParkingTicket
        +exit(...) Receipt
        +registerPass(Pass)
        +availabilityReport() String
    }
    class CommandProcessor {
        +register(String, CommandHandler)
        +process(String) String
    }
    class ParkingApp12 {
        +build() CommandProcessor
    }

    Vehicle <|-- TwoWheeler
    Vehicle <|-- Car
    Car <|-- ElectricCar
    Vehicle <|-- EBike : (T12)
    Car <|-- DeliveryVan : (T12)
    Chargeable <|.. ElectricCar
    Pass <|.. MonthlyPass
    Pass <|.. DayPass : (T12)
    ParkingSystem o-- ParkingFacility
    ParkingSystem o-- VehicleFactory
    ParkingSystem o-- "*" ParkingTicket
    ParkingSystem o-- "*" Pass
    ParkingSystem ..> Receipt
    ParkingTicket --> Vehicle
    ParkingTicket --> UserType
    CommandProcessor --> ParkingSystem
    VehicleFactory ..> Vehicle
    ParkingApp12 ..> EBike
    ParkingApp12 ..> DeliveryVan
    ParkingApp12 ..> DayPass
```

Vehicle hierarchy only (Task 6): see `../Task06_Inheritance/UML_Diagram.md` (includes a plain-text version).
