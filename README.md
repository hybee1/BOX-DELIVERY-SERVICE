
BOX DELIVERY SERVICE

This project simulates a delivery service using 
electronics/hardware-attached devices connected to boxes.
Each box is powered by a battery and the battery 
consumption is calculated based on a simple model. 
Note: No real hardware is connected. the box, the battery will be
simulated.

ASSUMPTIONS FOR BATTERY CONSUMPTION: 

Since i am not communicating with a real hardware (for the case 
of the box) to get actual battery level as the battery
capacity drops over time as a result of usage.

I assumed that for a given box, battery drains base on this
simple math 

baseWeight = 500; // max weight in grams 

baseDrain = 5; // battery drain in percent

baseTimeDrain = 60; // base time in minutes

on max load for 1hr, battery drains by 5%

500g = 1hr = 5%

REQUIREMENTS

Java 23

Spring Boot 3.5.4

Embedded H2 database


RUNNING THE PROJECT

1. Clone the repository and build the project:
    --.\mvnw clean install

2. RUN THE APPLICATION:
   -- .\mvnw spring-boot:run

3. Access the H2 database console (optional):
    --    http://localhost:8080/h2-console

 
When relating with the endpoints you will need data, here
is a handy one that you can use:

-- To create a Box name/txRef use: 

"Box_002" or any other name with max character length of 20

Box name/txRef: "BOX_001" is already prefilled with battery 
capacity 24. for special need case

-- To load item(s) into the box:
   
 " [

        {
        "name": "Item-1",
        "weight": 100.78,
        "code": "ITEM_1"
        },

        {
        "name": "Item-2",
        "weight": 150.94,
        "code": "ITEM_2"
        }

   ] 
 "

API DOCUMENTATION

Swagger UI is available at:   
http://localhost:8080/swagger-ui.html


















