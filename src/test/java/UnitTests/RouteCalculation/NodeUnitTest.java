package UnitTests.RouteCalculation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import RouteCalculation.AreaSingleton;
import RouteCalculation.CompassDirection;
import RouteCalculation.LngLat;
import RouteCalculation.Node;

public class NodeUnitTest {

        private static final LngLat START = new LngLat(-3.186874, 55.944494);
        private static final LngLat GOAL = new LngLat(-3.190000, 55.950000);
        private static final LngLat INTERMEDIATE = new LngLat(-3.188874, 55.947494);
        private AreaSingleton mockSingleton;

        @Before
        public void setupSingleton() {
                // Mock the AreaSingleton
                mockSingleton = mock(AreaSingleton.class);
                AreaSingleton.setInstance(mockSingleton);

                // Default behavior: No no-fly zones
                LngLat[][] noNoFlyZones = { {} };
                when(mockSingleton.getNoFlyZones()).thenReturn(noNoFlyZones);
        }

        @Test
        public void testNodeConstruction_NoParent() {
                Node startNode = new Node(START, GOAL);

                assertNotNull("Start node should not be null", startNode);
                assertEquals("Start node point mismatch", START, startNode.getPoint());
                assertEquals("Start node heuristic cost mismatch", START.distanceTo(GOAL), startNode.getHScore(),
                                0.0001);
        }

        @Test
        public void testNodeConstruction_WithParent() {
                Node startNode = new Node(START, GOAL);
                Node childNode = new Node(INTERMEDIATE, GOAL, startNode, CompassDirection.N);

                assertNotNull("Child node should not be null", childNode);
                assertEquals("Child node point mismatch", INTERMEDIATE, childNode.getPoint());
                assertEquals("Child node parent mismatch", startNode, childNode.getParent());
                assertEquals("Child node direction from parent mismatch", CompassDirection.N,
                                childNode.getDirectionFromParent());
        }

        @Test
        public void testHeuristicCalculation_StraightLine() {
                Node startNode = new Node(START, GOAL);

                assertEquals("Heuristic should be straight-line distance when no no-fly zones intersect",
                                START.distanceTo(GOAL), startNode.getHScore(), 0.0001);
        }

        @Test
        public void testHeuristicCalculation_WithNoFlyZone() {
                LngLat[][] noFlyZones = {
                                { new LngLat(-3.19, 55.945), new LngLat(-3.19, 55.946), new LngLat(-3.18, 55.946),
                                                new LngLat(-3.18, 55.945) }
                };
                when(mockSingleton.getNoFlyZones()).thenReturn(noFlyZones);

                LngLat start = new LngLat(-3.185, 55.945);
                Node startNode = new Node(start, GOAL);

                // Calculate expected heuristic manually
                double[] distances = new double[] {
                                start.distanceTo(new LngLat(-3.19, 55.945))
                                                + new LngLat(-3.19, 55.945).distanceTo(GOAL),
                                start.distanceTo(new LngLat(-3.19, 55.946))
                                                + new LngLat(-3.19, 55.946).distanceTo(GOAL),
                                start.distanceTo(new LngLat(-3.18, 55.946))
                                                + new LngLat(-3.18, 55.946).distanceTo(GOAL),
                                start.distanceTo(new LngLat(-3.18, 55.945)) + new LngLat(-3.18, 55.945).distanceTo(GOAL)
                };
                double expectedHeuristic = Arrays.stream(distances).min().orElse(Double.POSITIVE_INFINITY);

                assertEquals("Heuristic should consider the shortest detour around the no-fly zone",
                                expectedHeuristic, startNode.getHScore(), 0.0001);
        }

        @Test
        public void testFScoreCalculation() {
                Node startNode = new Node(START, GOAL);
                Node childNode = new Node(INTERMEDIATE, GOAL, startNode, CompassDirection.N);

                double expectedFScore = childNode.getGScore() + childNode.getHScore();
                assertEquals("F score calculation mismatch", expectedFScore, childNode.getFScore(), 0.0001);
        }

        @Test
        public void testSingletonIsolationBetweenTests() {
                // First test case with no no-fly zones
                Node startNode = new Node(START, GOAL);
                assertEquals("Heuristic should be straight-line distance with no no-fly zones",
                                START.distanceTo(GOAL), startNode.getHScore(), 0.0001);

                // Update singleton for a new test
                LngLat[][] noFlyZones = {
                                { new LngLat(-3.19, 55.945), new LngLat(-3.19, 55.946), new LngLat(-3.18, 55.946),
                                                new LngLat(-3.18, 55.945) }
                };
                when(mockSingleton.getNoFlyZones()).thenReturn(noFlyZones);

                LngLat updatedStart = new LngLat(-3.185, 55.945);
                Node nodeWithNoFlyZone = new Node(updatedStart, GOAL);

                // Dynamically calculate the expected heuristic
                double[] distances = new double[] {
                                updatedStart.distanceTo(new LngLat(-3.19, 55.945))
                                                + new LngLat(-3.19, 55.945).distanceTo(GOAL),
                                updatedStart.distanceTo(new LngLat(-3.19, 55.946))
                                                + new LngLat(-3.19, 55.946).distanceTo(GOAL),
                                updatedStart.distanceTo(new LngLat(-3.18, 55.946))
                                                + new LngLat(-3.18, 55.946).distanceTo(GOAL),
                                updatedStart.distanceTo(new LngLat(-3.18, 55.945))
                                                + new LngLat(-3.18, 55.945).distanceTo(GOAL)
                };
                double expectedHeuristic = Arrays.stream(distances).min().orElse(Double.POSITIVE_INFINITY);

                assertEquals("Heuristic should consider updated no-fly zones",
                                expectedHeuristic, nodeWithNoFlyZone.getHScore(), 0.0001);
        }

        @Test
        public void testGScoreCalculationMultipleParents() {
                Node startNode = new Node(START, GOAL);
                Node firstChild = new Node(INTERMEDIATE, GOAL, startNode, CompassDirection.N);
                Node secondChild = new Node(GOAL, GOAL, firstChild, CompassDirection.N);

                double expectedGScore = 2 * LngLat.getLengthOfMove();
                assertEquals("G score calculation mismatch for multiple parents",
                                expectedGScore, secondChild.getGScore(), 0.0001);
        }

        @Test
        public void testSingletonResetBetweenTests() {
                // Verify initial singleton behavior
                Node startNode = new Node(START, GOAL);
                assertEquals("Initial heuristic should be straight-line distance",
                                START.distanceTo(GOAL), startNode.getHScore(), 0.0001);

                // Modify singleton for new test
                LngLat[][] noFlyZones = {
                                { new LngLat(-3.19, 55.945), new LngLat(-3.19, 55.946), new LngLat(-3.18, 55.946),
                                                new LngLat(-3.18, 55.945) }
                };
                when(mockSingleton.getNoFlyZones()).thenReturn(noFlyZones);

                Node updatedNode = new Node(START, GOAL);
                assertNotEquals("Singleton should reset properly between tests",
                                START.distanceTo(GOAL), updatedNode.getHScore(), 0.0001);
        }

}
