# CI Pipeline
![Java CI with Maven](https://github.com/IainHigh/ilpCoursework/actions/workflows/maven.yml/badge.svg)

# Run Tests:
c:; cd 'c:\Program Files (x86)\Eclipse Adoptium\jre\bin\java.exe' '-cp' 'C:\Users\s2062378\AppData\Local\Temp\cp_dd71q3mef251e578wag1yrjsj.jar' 'uk.ac.ed.inf.SystemTests.Driver'

To run with test coverage right click on the test folder and select "Run Tests with Coverage" (5th option from bottom.)

# Run Program:
c:; cd 'c:\Users\s2062378\OneDrive - University of Edinburgh\Teaching\Year 1\Software Testing\ilpCoursework'; & 'C:\Program Files (x86)\Eclipse Adoptium\jre\bin\java.exe' '-cp' 'C:\Users\s2062378\AppData\Local\Temp\cp_5r4w3m0cdtejue7e5xv79b3hl.jar' 'uk.ac.ed.inf.PizzaDrone' '2024-12-19' 'https://ilp-rest-2024.azurewebsites.net/'

(Change arguments as required.)

Alternatively in VSCode under the file explorer tab, go to "Java Projects", Right click on "PizzaDronz" and click run.

# Todo - Parts to test:
- Test date verification
- Test credit card verification
- Test 


# Types of Tests:
- Unit
- Integration
- Performance
- End-to-End

Based on the coursework specification, here are some useful types of tests that you can include in your test plan:

1. Unit Tests
Test name: Validate central area
Specific tests:
Test if a point is correctly identified as inside or outside the Central Area.
Test edge cases near the boundary of the Central Area.
Test name: Distance calculation test
Specific tests:
Verify if the Pythagorean distance between two points is correctly computed.
Test name: Pizza combination validation
Specific tests:
Ensure that an exception is thrown if the ordered pizzas are from different restaurants.
Test name: Order validation tests
Specific tests:
Validate the correctness of the order data (e.g., valid/invalid card details, expiration date, CVV).
2. Integration Tests
Test name: REST server data retrieval
Specific tests:
Ensure that the restaurants and menus are correctly retrieved from the REST service.
Ensure that orders are correctly retrieved and parsed.
Test name: Drone movement
Specific tests:
Validate if the drone correctly computes the next position based on compass direction.
Ensure the drone correctly avoids no-fly zones by integrating data from the geoJSON files.
3. Performance Tests
Test name: Runtime performance
Specific tests:
Ensure that the entire operation of planning the drone's path runs within 60 seconds.
Test name: Battery life management
Specific tests:
Verify that the drone does not exceed 2000 moves per day.
4. Boundary and Edge Case Tests
Test name: Central Area boundary test
Specific tests:
Test if the drone correctly identifies when it is close to the Central Area boundaries.
Test name: Illegal flight path test
Specific tests:
Verify that the drone does not leave the Central Area after entering it.
5. Functional Tests
Test name: No-fly zone avoidance
Specific tests:
Ensure that the drone’s path does not cross into no-fly zones.
Test name: Delivery outcomes
Specific tests:
Check if orders are correctly classified as Delivered, ValidButNotDelivered, or various invalid reasons (e.g., invalid card, invalid pizza count).
Each of these tests focuses on different aspects of the drone's functionality, ensuring correctness, performance, and adherence to specifications.