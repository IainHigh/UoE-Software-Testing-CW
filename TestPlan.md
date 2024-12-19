# System-Level Requirements for PizzaDronz Service

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


# Level of Requirements: System, Integration, and Unit Tests

| **Attribute**           | **Test Used**              | **Notes**                                                                                     |
|--------------------------|----------------------------|-----------------------------------------------------------------------------------------------|
| **System-Level Tests**   |                            |                                                                                               |
| Runtime Statistic        | Performance               | Ensure total execution time for daily deliveries does not exceed 60 seconds.                 |
| Load Test                | Performance               | Simulate high load with 200 concurrent orders to ensure no performance degradation.           |
| Normal Test Data         | System                    | Verify no errors occur during normal operation with valid input.                             |
| Extreme Test Data        | System                    | Test with maximum values (e.g., order count, no-fly zones) to ensure stability.               |
| Exceptional Test Data    | System                    | Verify expected errors are thrown for invalid inputs like malformed JSON or invalid coordinates. |
| No-Fly Zone Compliance   | System                    | Ensure the drone avoids all specified no-fly zones, even with dynamic updates.                |
| Path Optimization        | System                    | Verify the shortest path is calculated for deliveries under valid constraints.                |
| Invalid Order Handling   | System                    | Confirm system rejects invalid orders based on various criteria (e.g., card number, format).  |
| Battery Conservation     | System                    | Validate drone returns to Appleton Tower before battery depletion.                           |
| GeoJSON Output           | System                    | Verify correct generation of GeoJSON files for drone path visualization.                     |
| Valid Compass Directions | System                    | Ensure the drone only follows valid compass directions (16 directions + hover).              |
| A* Algorithm Integrity   | System                    | Validate the route calculated by the A* algorithm adheres to no-fly zone and central area constraints. |
| **Integration Tests**    |                            |                                                                                               |
| REST API Response        | Integration               | Test REST API interactions with valid and invalid data (e.g., 404 errors).                   |
| Data Handling            | Integration               | Ensure proper deserialization of order, restaurant, and no-fly zone data.                    |
| No-Fly Zone Avoidance    | Integration               | Validate integration between `AreaSingleton` and path calculation modules.                   |
| Credit Card Validation   | Integration               | Confirm validation logic integrates with the `Order` class for card data.                    |
| GeoJSON Format           | Integration               | Test JSON serialization of drone paths using `FileWriter`.                                   |
| Restaurant Validation    | Integration               | Ensure orders are matched correctly to participating restaurants.                            |
| FlightPathPoint Mapping  | Integration               | Verify drone movements are correctly represented in GeoJSON outputs.                         |
| Route Calculation        | Integration               | Test integration between `RouteCalculator`, `LngLat`, and `CompassDirection` for route generation. |
| Central Area Validation  | Integration               | Validate that `AreaSingleton` correctly loads and applies central area boundary constraints.  |
| **Unit Tests**           |                            |                                                                                               |
| Credit Card Validation   | Unit                      | Test regex and Luhn algorithm in `CreditCardInformation` for valid/invalid numbers.          |
| Expiry Date Parsing      | Unit                      | Validate correct parsing and comparison of expiry dates in `Order`.                          |
| CVV Validation           | Unit                      | Ensure CVV is exactly three numeric digits.                                                  |
| Menu Parsing             | Unit                      | Test correct deserialization of menu data in `Menu`.                                         |
| Pizza Validation         | Unit                      | Confirm `containsInvalidPizza` correctly identifies invalid pizzas in `Order`.               |
| Restaurant Location      | Unit                      | Validate longitude and latitude accuracy in `Restaurant.Location`.                           |
| Order Outcome Assignment | Unit                      | Ensure `Order` assigns the correct `OrderOutcome` based on validation rules.                 |
| FileWriter Outputs       | Unit                      | Test JSON and GeoJSON output generation for correctness.                                      |
| Singleton Behavior       | Unit                      | Verify singleton behavior of `AreaSingleton` for consistent data access.                     |
| Path Angle Calculation   | Unit                      | Test angle calculations in `FlightPathPoint` for accuracy.                                   |
| Order Parsing            | Unit                      | Confirm deserialization of `Order` JSON into valid objects.                                  |
| Central Area Border      | Unit                      | Validate correct loading of central area data from the REST API.                             |
| Drone Coordinate Mapping | Unit                      | Test `FlightPathPoint` for correct mapping of start and end coordinates.                     |
| Invalid Input Handling   | Unit                      | Verify exceptions are correctly thrown for invalid data in `OrderRetriever`.                 |
| Price Validation         | Unit                      | Confirm `validatePriceTotal` in `Order` checks totals against menu prices accurately.         |
| Direction Handling       | Unit                      | Validate `CompassDirection` enum for accurate direction-to-angle mapping.                    |
| Distance Tolerance       | Unit                      | Test `LngLat.closeTo` for correct distance threshold application.                            |
| No-Fly Zone Check        | Unit                      | Verify `LngLat.inNoFlyZone` detects entry and intersection with no-fly zones accurately.     |
| Node Heuristic Accuracy  | Unit                      | Confirm A* `Node` heuristic correctly accounts for no-fly zones and central area restrictions. |
| Route Reconstruction     | Unit                      | Test `RouteCalculator.reconstructPath` for accurate path rebuilding from A* nodes.           |
| Hover Handling           | Unit                      | Ensure hover action (`CompassDirection.HOVER`) correctly preserves drone's location.         |