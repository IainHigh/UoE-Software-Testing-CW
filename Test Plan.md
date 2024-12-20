# Section 1: Test Plan

## Overview
The PizzaDronz project followed a waterfall development methodology rather than a test-driven development (TDD) approach due to the coursework originally being completed two years ago. Consequently, the test plan was constructed post-development and aligned to validate existing functionalities rather than evolve in tandem with iterative development. However, due to the requirement for detailed documentation to be produced for this coursework - a waterfall method aligns more closely than an agile approach where minimal documentation is preferred.

This document maps testing for each requirement to the software lifecycle, drawing from the approaches discussed in Chapter 20 of the course slides. It outlines the testing strategy, identifies the lifecycle stages each test fits into, and considers how the test set evolves to address new and existing functionality.

---

## Testing Strategy Across the Lifecycle

### Requirements and Design
- **System Requirements:** Ensure that high-level requirements such as delivery path optimization and no-fly zone compliance are correctly defined and traceable to tests.
- **Performance and Scalability:** Performance benchmarks and load testing were designed based on early requirements to ensure scalability.
- **Security:** Requirements related to input validation and robustness against malformed data were addressed in this phase.

### Implementation
- **Unit Testing:** Unit tests were created for individual methods such as `Order.validateOrder` and `LngLat.distanceTo`. These tests validate core functionalities and edge cases.
- **Integration Testing:** Integration tests focus on interactions between modules like `OrderRetriever` and the REST API or `RouteCalculator` and `AreaSingleton`.
- **Mutation Testing:** During implementation, mutation testing was applied to assess the robustness of unit tests and ensure comprehensive coverage.

### Testing and Debugging
- **System Testing:** High-level functionalities, such as load handling and no-fly zone avoidance, were validated using synthetic test scenarios.
- **Fuzz Testing:** Introduced malformed inputs to assess the robustness of the system against unexpected data.
- **Regression Testing:** Ensured that new code additions or fixes did not break existing functionality.

### Deployment
- **End-to-End Testing:** Validated that the final system meets all functional requirements and works seamlessly from order retrieval to delivery.
- **Exploratory Testing:** Conducted exploratory testing to identify rare edge cases or systemic issues not covered by formal test cases.

---

## How Testing Addressed Evolving Needs
### Example: Addition of Timestamped Logging
When the requirement for timestamped logging was identified to improve debugging, additional unit tests were added to validate time format and accuracy. Integration tests ensured timestamps propagated correctly through the system.

### Example: Handling High Order Volumes
The original system tests focused on a moderate number of orders. When performance requirements shifted to handle 200 simultaneous orders, the load tests were updated, and additional integration tests were introduced to ensure seamless API communication under stress.

---

## Limitations and Future Considerations
1. **Static Test Plan:** Unlike TDD, the waterfall approach resulted in limited adaptability of the test set to emerging functionality.
2. **Exploratory Gaps:** While exploratory testing mitigated this, some edge cases (e.g., overlapping no-fly zones creating impassable paths) were discovered late.
3. **Limited Monitoring:** Instrumentation was not included in early stages, reducing visibility into performance issues during development.

---

## Diagram Request
Please generate a diagram illustrating the mapping of test types (unit, integration, system) to the software lifecycle stages (requirements, implementation, testing, deployment). Show example tests and their relationships to lifecycle phases.

# Section 2: Evaluation of the Quality of the Test Plan

### Strengths
1. **Comprehensive Coverage:** The test plan provides a broad spectrum of tests, including unit, integration, system, and exploratory testing, ensuring multiple aspects of the system are validated.
2. **Alignment with Lifecycle:** Mapping tests to lifecycle phases ensures logical progression and accountability for each stage of development.
3. **Stress and Performance Testing:** Load and performance tests effectively address scalability and runtime requirements.

### Weaknesses
1. **Instrumentation Gaps:** Limited use of instrumentation, such as diagnostic logs or assertions, reduced the ability to monitor runtime behaviour and pinpoint faults dynamically.
2. **Reactive Approach:** The post-development creation of the test plan limited its adaptability to emerging requirements.
3. **Limited Security Testing:** While input validation was robust, additional tests for injection vulnerabilities or other security risks could have been beneficial.

### Potential Omissions or Vulnerabilities
- **Rare Edge Cases:** Overlapping no-fly zones or unexpected API behaviour could still result in unhandled scenarios.
- **Real-Time Monitoring:** Absence of runtime monitoring tools reduced visibility into operational issues, such as delays in delivery calculations.

### Adequacy of the Plan
The test plan is well-suited for validating the PizzaDronz project’s core requirements and functionality. However, addressing instrumentation gaps and adding dynamic monitoring could enhance future test plans. The outlined recommendations ensure the plan evolves effectively to meet high-quality standards.

---

## Instrumentation of the Code

### Instrumentation Justification
The PizzaDronz project did not include code instrumentation. This decision was based on the project requirements, which emphasised thorough pre-deployment testing over runtime monitoring. The nature of the requirements and the post-development testing approach made instrumentation unnecessary.

### Adequacy of the Approach
1. **No Immediate Need for Instrumentation:** Comprehensive unit, integration, and system tests were sufficient to identify and address most issues during pre-deployment testing.
2. **Alignment with Requirements:** The lack of instrumentation did not compromise the ability to meet functional or non-functional requirements.

### Limitations
1. **Reduced Runtime Insights:** Without runtime logs or diagnostic outputs, troubleshooting in live scenarios could be more challenging.
2. **Missed Opportunities for Monitoring:** Instrumentation could have provided valuable insights into performance bottlenecks or rare runtime issues.

### Recommendations
While not necessary for this project, future iterations should incorporate basic instrumentation such as diagnostic logging and assertions. These additions would enhance visibility, aid in real-time fault detection, and improve debugging during maintenance or scaling efforts.
