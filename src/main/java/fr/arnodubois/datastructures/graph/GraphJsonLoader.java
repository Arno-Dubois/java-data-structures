package fr.arnodubois.datastructures.graph;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * Reads a graph of strings from JSON. The expected format is a list of edges:
 *
 * <pre>
 * [
 *   {"weight": 4, "nodes": ["Edinburgh", "London"]},
 *   {"weight": 2, "nodes": ["Amsterdam", "London"]}
 * ]
 * </pre>
 */
public final class GraphJsonLoader {
    private record JsonEdge(int weight, String[] nodes) {
    }

    private GraphJsonLoader() {
    }

    /**
     * Reads a graph from a classpath resource.
     *
     * @param resourceName the name of the resource, e.g. {@code "europe-rail-network.json"}
     * @return the graph
     * @throws NoSuchElementException if the resource does not exist
     * @throws JsonParseException     if the content is not a valid list of edges
     * @throws UncheckedIOException   if the resource cannot be read
     */
    public static Graph<String> fromResource(final String resourceName) {
        final InputStream stream = GraphJsonLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (stream == null) throw new NoSuchElementException("Resource not found: " + resourceName);
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return GraphJsonLoader.fromJson(reader);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads a graph from JSON.
     *
     * @param reader the JSON source; it is not closed
     * @return the graph
     * @throws JsonParseException if the content is not a valid list of edges
     */
    public static Graph<String> fromJson(final Reader reader) {
        final JsonEdge[] jsonEdges = new Gson().fromJson(reader, JsonEdge[].class);
        if (jsonEdges == null) throw new JsonParseException("The JSON is empty");

        final Graph<String> graph = new Graph<>();
        for (final JsonEdge jsonEdge : jsonEdges) {
            if (jsonEdge == null || jsonEdge.nodes() == null || jsonEdge.nodes().length != 2
                    || jsonEdge.nodes()[0] == null || jsonEdge.nodes()[1] == null) {
                throw new JsonParseException("Each edge needs exactly two node names: " + jsonEdge);
            }
            graph.addEdge(jsonEdge.nodes()[0], jsonEdge.nodes()[1], jsonEdge.weight());
        }
        return graph;
    }
}
