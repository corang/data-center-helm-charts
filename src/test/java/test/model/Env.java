package test.model;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.collection.Array;
import io.vavr.control.Option;

import java.util.Map;

import static org.assertj.vavr.api.VavrAssertions.assertThat;
import static test.jackson.JsonNodeAssert.assertThat;

public class Env {
    private final JsonNode node;

    public Env(JsonNode node) {
        this.node = node;
    }

    public Env assertHasValue(String envName, String expectedValue) {
        assertThat(findEnv(envName))
                .describedAs("Expected env '%s' to have a value", envName)
                .hasValueSatisfying(node ->
                        assertThat(node.path("value"))
                                .describedAs("Expected env '%s' to have the expected value", envName)
                                .hasTextEqualTo(expectedValue));

        return this;
    }

    public Env assertHasSecretRef(String envName, String expectedSecretName, String expectedSecretKey) {
        assertThat(findEnv(envName))
                .describedAs("Expected env '%s' to have a value", envName)
                .hasValueSatisfying(node ->
                        assertThat(node.path("valueFrom").path("secretKeyRef"))
                                .describedAs("Expected env '%s' to have the expected value", envName)
                                .isObject(Map.of(
                                        "name", expectedSecretName,
                                        "key", expectedSecretKey)));

        return this;
    }

    private Option<JsonNode> findEnv(String envName) {
        return Array.ofAll(node)
                .find(node -> envName.equals(node.required("name").asText()));
    }
}