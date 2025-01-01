package RouteCalculation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Wrapper class for deserializing the central area JSON.
 */
public class CentralArea {
    @JsonProperty("name")
    private String name;

    @JsonProperty("vertices")
    private List<LngLat> vertices;

    public List<LngLat> getVertices() {
        return vertices;
    }
}
