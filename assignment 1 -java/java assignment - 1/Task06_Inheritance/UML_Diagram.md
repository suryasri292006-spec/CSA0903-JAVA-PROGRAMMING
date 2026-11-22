# Task 6 – UML Class Diagram (vehicle hierarchy)

## Mermaid version (renders on GitHub and in VS Code with a Mermaid extension)

```mermaid
classDiagram
    class Vehicle {
        <<abstract>>
        -String regNo
        #Vehicle(String regNo)
        +getRegNo() String
        #ratePerHour()* double
        +describe() String
    }
    class TwoWheeler {
        +TwoWheeler(String regNo)
        #ratePerHour() double
    }
    class Car {
        +Car(String regNo)
        #ratePerHour() double
        +describe() String
    }
    class ElectricCar {
        -int batteryKWh
        +ElectricCar(String regNo)
        +ElectricCar(String regNo, int batteryKWh)
        +getBatteryKWh() int
        +describe() String
    }
    Vehicle <|-- TwoWheeler
    Vehicle <|-- Car
    Car <|-- ElectricCar
```

## Text version (always readable)

```
                +-----------------------------+
                |     <<abstract>> Vehicle    |
                |-----------------------------|
                | -regNo : String             |
                |-----------------------------|
                | #Vehicle(regNo)             |
                | +getRegNo() : String        |
                | #ratePerHour() : double {abstract}
                | +describe() : String        |
                +--------------^--------------+
                               |
              +----------------+----------------+
              |                                 |
   +----------+-----------+         +-----------+-----------+
   |      TwoWheeler      |         |          Car          |
   |----------------------|         |-----------------------|
   | +TwoWheeler(regNo)   |         | +Car(regNo)           |
   | #ratePerHour() = 10  |         | #ratePerHour() = 30   |
   +----------------------+         | +describe()           |
                                    +-----------^-----------+
                                                |
                                    +-----------+-----------+
                                    |      ElectricCar      |
                                    |-----------------------|
                                    | -batteryKWh : int     |
                                    |-----------------------|
                                    | +ElectricCar(regNo)   |
                                    | +ElectricCar(regNo,kWh)|
                                    | +getBatteryKWh()      |
                                    | +describe()           |
                                    +-----------------------+
```

Legend: `-` private, `#` protected, `+` public; hollow-arrow (`^`) = inheritance ("is-a"); `{abstract}` = abstract method.
