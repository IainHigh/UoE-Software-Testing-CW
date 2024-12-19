# Section 1: System-Level Requirements for PizzaDronz Service

## Functional Requirements
1. **Order Management**
   - Accept orders from a REST API endpoint and parse them into a structured format.
   - Filter out invalid orders based on criteria such as:
     - Incorrect credit card details.
     - Expired credit cards or invalid CVV.
     - Invalid pizza combinations (e.g., items from multiple restaurants in one order).

2. **Flight Path Calculation**
   - Calculate the shortest valid path to deliver pizzas while avoiding no-fly zones.
   - Ensure the drone returns to Appleton Tower before battery depletion.
   - Prevent re-entry into the Central Area after exiting it during delivery.

3. **No-Fly Zone Compliance**
   - Avoid entering or passing through predefined no-fly zones using GeoJSON data.

4. **Delivery Execution**
   - Deliver pizzas by hovering at the restaurant and drop-off points for one move each.
   - Ensure accurate location proximity during pickup and delivery (within 0.00015 degrees).

5. **Dynamic Data Handling**
   - Retrieve and process dynamic data (e.g., orders, restaurants, no-fly zones) from the REST service at runtime.

6. **Error Handling**
   - Handle invalid REST responses gracefully, including retry mechanisms.

## Measurable Quality Attributes
1. **Performance**
   - Maximum runtime: 60 seconds per session to calculate the day’s deliveries and flight paths.
   - Process at least 100 valid orders within a single drone operation cycle.

2. **Accuracy**
   - Maintain delivery location accuracy within a tolerance of 0.00015 degrees.
   - Ensure no overlap with no-fly zones during flight path execution.

3. **Reliability**
   - Achieve a minimum success rate of 95% for delivering valid orders.

4. **Battery Efficiency**
   - Limit drone moves to 2000 per session to conserve battery life.
   - Optimize flight paths to minimize energy consumption.

5. **Scalability**
   - Handle peak loads of up to 200 orders per day without performance degradation.

## Qualitative Requirements
1. **Usability**
   - Provide clear error messages for invalid input data.
   - Ensure all outputs (e.g., flight paths, deliveries) are recorded in readable JSON and GeoJSON formats.

2. **Security**
   - Do not store sensitive customer data (e.g., credit card details) persistently.
   - Validate all incoming data to prevent injection attacks.

3. **Maintainability**
   - Use Java 18 features like streams, records, and pattern matching for clean, modern code.
   - Document code thoroughly using JavaDoc for ease of future development.

4. **Extensibility**
   - Ensure the solution is adaptable to new restaurants or delivery points by relying on dynamic data.

## Additional Requirements
1. **Compliance**
   - Adhere to all constraints specified in the coursework, such as REST API formatting and GeoJSON standards.
   - Meet all naming conventions for files and output formats.

2. **Visualization**
   - Generate GeoJSON flight path files for easy visualization and debugging of drone movements.

3. **Reproducibility**
   - Use a single, seed-based pseudo-random number generator to ensure consistent results across executions.

4. **Logging**
   - Provide diagnostic logs for debugging, but ensure these are concise and non-intrusive.


# Section 2: Level of Requirements - System, Integration, and Unit Tests

| **Attribute**            | **Test Used** | **Notes**                                                                                              | **Test Approach**                                                                                    |
|--------------------------|---------------|--------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| **System-Level Tests**   |               |                                                                                                        |                                                                                                      |
| Runtime Statistic        | Performance   | Ensure total execution time for daily deliveries does not exceed 60 seconds.                           | Use automated performance benchmarking tools to measure execution time under various scenarios.      |
| Load Test                | Performance   | Simulate high load with 200 concurrent orders to ensure no performance degradation.                    | Conduct stress tests with synthetic load using tools like JMeter or custom load generators.          |
| Normal Test Data         | System        | Verify no errors occur during normal operation with valid input.                                       | Execute end-to-end tests with realistic test data to verify functionality under normal conditions.   |
| Extreme Test Data        | System        | Test with maximum values (e.g., order count, no-fly zones) to ensure stability.                        | Use boundary value analysis to generate extreme test cases for input limits.                         |
| Exceptional Test Data    | System        | Verify expected errors are thrown for invalid inputs like malformed JSON or invalid coordinates.       | Perform fuzz testing to introduce malformed and invalid input data.                                  |
| No-Fly Zone Compliance   | System        | Ensure the drone avoids all specified no-fly zones, even with dynamic updates.                         | Use simulated environments with varying no-fly zone configurations and monitor drone behavior.       |
| Path Optimization        | System        | Verify the shortest path is calculated for deliveries under valid constraints.                         | Perform regression tests with predefined paths to ensure optimization remains consistent.            |
| Invalid Order Handling   | System        | Confirm system rejects invalid orders based on various criteria (e.g., card number, format).           | Use equivalence partitioning to test invalid and valid order combinations.                           |
| Battery Conservation     | System        | Validate drone returns to Appleton Tower before battery depletion.                                     | Simulate delivery routes near battery limits and validate return functionality.                      |
| GeoJSON Output           | System        | Verify correct generation of GeoJSON files for drone path visualization.                               | Use automated file comparison tools to validate GeoJSON output structure and accuracy.               |
| Valid Compass Directions | System        | Ensure the drone only follows valid compass directions (16 directions + hover).                        | Generate test cases for all compass directions and verify drone behavior aligns with specifications. |
| A* Algorithm Integrity   | System        | Validate the route calculated by the A* algorithm adheres to no-fly zone and central area constraints. | Perform pathfinding verification tests against predefined scenarios with expected outputs.           |
| **Integration Tests**    |               |                                                                                                        |                                                                                                      |
| REST API Response        | Integration   | Test REST API interactions with valid and invalid data (e.g., 404 errors).                             | Use mock APIs and tools like Postman to test API behavior under various scenarios.                   |
| No REST API Response       | Integration   | Test REST API interactions when we are unable to connect to the server - no internet                             | Test the project without internet connection.                   |
| Data Handling            | Integration   | Ensure proper deserialization of order, restaurant, and no-fly zone data.                              | Perform deserialization tests with JSON samples to verify data parsing accuracy.                     |
| No-Fly Zone Avoidance    | Integration   | Validate integration between `AreaSingleton` and path calculation modules.                             | Simulate routes near no-fly zones and validate compliance through log analysis.                      |
| Credit Card Validation   | Integration   | Confirm validation logic integrates with the `Order` class for card data.                              | Use parameterized tests to validate card details across various valid and invalid input cases.       |
| GeoJSON Format           | Integration   | Test JSON serialization of drone paths using `FileWriter`.                                             | Validate output format with schema validation tools for GeoJSON compliance.                          |
| Restaurant Validation    | Integration   | Ensure orders are matched correctly to participating restaurants.                                      | Test restaurant assignment with edge cases involving overlapping and ambiguous data.                 |
| FlightPathPoint Mapping  | Integration   | Verify drone movements are correctly represented in GeoJSON outputs.                                   | Visual inspection of generated paths combined with automated coordinate validation.                  |
| Route Calculation        | Integration   | Test integration between `RouteCalculator`, `LngLat`, and `CompassDirection` for route generation.     | Simulate various start and end points to validate accurate route generation.                         |
| Central Area Validation  | Integration   | Validate that `AreaSingleton` correctly loads and applies central area boundary constraints.           | Use boundary tests with varying central area configurations to ensure proper functionality.          |
| **Unit Tests**           |               |                                                                                                        |                                                                                                      |
| Credit Card Validation   | Unit          | Test regex and Luhn algorithm in `CreditCardInformation` for valid/invalid numbers.                    | Write unit tests using boundary and invalid input cases for regex and algorithm validation.          |
| Expiry Date Parsing      | Unit          | Validate correct parsing and comparison of expiry dates in `Order`.                                    | Perform unit tests with edge cases for valid and invalid expiry dates.                               |
| CVV Validation           | Unit          | Ensure CVV is exactly three numeric digits.                                                            | Use parameterized unit tests to validate numeric and non-numeric inputs.                             |
| Menu Parsing             | Unit          | Test correct deserialization of menu data in `Menu`.                                                   | Write unit tests with predefined JSON samples for menu deserialization.                              |
| Pizza Validation         | Unit          | Confirm `containsInvalidPizza` correctly identifies invalid pizzas in `Order`.                         | Use mock restaurant menus to test invalid and valid pizza combinations.                              |
| Restaurant Location      | Unit          | Validate longitude and latitude accuracy in `Restaurant.Location`.                                     | Perform unit tests with edge cases for out-of-range geographic coordinates.                          |
| Order Outcome Assignment | Unit          | Ensure `Order` assigns the correct `OrderOutcome` based on validation rules.                           | Write unit tests for all possible outcomes using equivalence partitioning.                           |
| FileWriter Outputs       | Unit          | Test JSON and GeoJSON output generation for correctness.                                               | Use snapshot testing to validate generated files against expected outputs.                           |
| Singleton Behavior       | Unit          | Verify singleton behavior of `AreaSingleton` for consistent data access.                               | Use concurrency tests to validate thread safety of the singleton implementation.                     |
| Path Angle Calculation   | Unit          | Test angle calculations in `FlightPathPoint` for accuracy.                                             | Write unit tests for all compass directions to validate angle conversions.                           |
| Order Parsing            | Unit          | Confirm deserialization of `Order` JSON into valid objects.                                            | Perform unit tests with various valid and malformed JSON samples.                                    |
| Central Area Border      | Unit          | Validate correct loading of central area data from the REST API.                                       | Write unit tests to verify parsing and storage of central area borders.                              |
| Drone Coordinate Mapping | Unit          | Test `FlightPathPoint` for correct mapping of start and end coordinates.                               | Validate coordinate calculations through unit tests for sample flight paths.                         |
| Invalid Input Handling   | Unit          | Verify exceptions are correctly thrown for invalid data in `OrderRetriever`.                           | Use mock API responses with invalid data formats to validate exception handling.                     |
| Price Validation         | Unit          | Confirm `validatePriceTotal` in `Order` checks totals against menu prices accurately.                  | Write unit tests with both undercharged and overcharged totals to validate logic.                    |
| Direction Handling       | Unit          | Validate `CompassDirection` enum for accurate direction-to-angle mapping.                              | Use parameterized tests for all compass directions to validate accuracy.                             |
| Distance Tolerance       | Unit          | Test `LngLat.closeTo` for correct distance threshold application.                                      | Write unit tests with edge cases to validate distance threshold behavior.                            |
| No-Fly Zone Check        | Unit          | Verify `LngLat.inNoFlyZone` detects entry and intersection with no-fly zones accurately.               | Use mock no-fly zones to validate detection logic with various paths.                                |
| Node Heuristic Accuracy  | Unit          | Confirm A* `Node` heuristic correctly accounts for no-fly zones and central area restrictions.         | Validate heuristic calculations with predefined scenarios in unit tests.                             |
| Route Reconstruction     | Unit          | Test `RouteCalculator.reconstructPath` for accurate path rebuilding from A* nodes.                     | Simulate path reconstruction with known inputs and verify against expected results.                  |
| Hover Handling           | Unit          | Ensure hover action (`CompassDirection.HOVER`) correctly preserves drone's location.                   | Write unit tests to validate hover behavior without position changes.                                |


# Section 3: Appropriateness of Chosen Test Approach
The chosen testing approaches comprehensively address the requirements of the PizzaDronz project but also have identifiable limitations. System-level tests, such as load testing and runtime performance benchmarking, are appropriate for assessing high-level functionality and efficiency under stress conditions. However, they may overlook intermittent edge cases that could arise in rare operational scenarios. Integration tests ensure seamless communication between modules, including validation of REST API interactions and proper deserialization of external data. While effective, these tests assume correct implementation of external services and may not account for unexpected behaviors from those sources. Unit tests, including mutation testing for robustness, provide precise validation of core functionalities like geographic calculations and order validations but may lack the ability to detect larger systemic issues arising from module interactions.

Despite these potential deficiencies, the overall approach aligns well with project requirements. For example, fuzz testing in system-level tests mitigates risks of malformed inputs, and mock testing in integration tests addresses API reliability concerns. However, additional exploratory testing, such as chaos testing to simulate unexpected failures, could enhance robustness further. The test plan provides a solid foundation for quality assurance, detailed fully in the table above.
