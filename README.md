

ASSUMPTIONS: 

Since i am not communicating with a real hardware (for the case 
of the box) to get actual battery level as the
the battery capacity drops over time as a result of usage.

I assumed that for a given box, battery drains base on this
simple math 

baseWeight = 500; // max weight in grams 

baseDrain = 5; // battery drain in percent

baseTimeDrain = 60; // base time in minutes

on max load for 1hr, battery drains by 5%

500g = 1hr = 5%

TO RUN THE CODE:

This project uses java 23 and springboot version 3.5.4
and embedded H2 database, just install the dependencies
and run the code.

When relating with the endpoints you will need data, here
is a handy one that you can use:

-- To create a Box name/txRef use: 

"Box_002" or any other name with max character length of 20

Box name/txRef: "BOX_001" is already prefilled with battery 
capacity 24. special need case

-- To create/supply item(s)/load for the created box:
   
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
   http://localhost:8080/swagger-ui.html


















