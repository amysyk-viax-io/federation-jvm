package com.apollographql.federation.graphqljava;

import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;

import java.util.Map;

import static graphql.ExecutionInput.newExecutionInput;
import static graphql.GraphQL.newGraphQL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class SchemaUtils {

    private final static String DIRECTIVES_EXCLUDE = "\"Directs the executor to include this field or fragment only when the `if` argument is true\"\n" +
            "directive @include(\n" +
            "    \"Included when true.\"\n" +
            "    if: Boolean!\n" +
            "  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT\n" +
            "\n" +
            "\"Directs the executor to skip this field or fragment when the `if`'argument is true.\"\n" +
            "directive @skip(\n" +
            "    \"Skipped when true.\"\n" +
            "    if: Boolean!\n" +
            "  ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT\n" +
            "\n" +
            "\"Marks the field or enum value as deprecated\"\n" +
            "directive @deprecated(\n" +
            "    \"The reason for the deprecation\"\n" +
            "    reason: String = \"No longer supported\"\n" +
            "  ) on FIELD_DEFINITION | ENUM_VALUE\n" +
            "\n";

    private SchemaUtils() {
    }

    static String printSchema(GraphQLSchema schema) {
        return new SchemaPrinter().print(schema);
    }

    static ExecutionResult execute(GraphQLSchema schema, String query) {
        return newGraphQL(schema).build().execute(newExecutionInput().query(query).build());
    }

    static void assertSDL(GraphQLSchema schema, String expected) {
        final ExecutionResult inspect = execute(schema, "{_service{sdl}}");
        assertEquals(0, inspect.getErrors().size(), "No errors");
        final Map<String, Object> data = inspect.getData();
        assertNotNull(data);
        @SuppressWarnings("unchecked") final Map<String, Object> _service = (Map<String, Object>) data.get("_service");
        assertNotNull(_service);
        final String sdl = (String) _service.get("sdl");
        assertEquals(expected.trim(), removeBuiltInDirectives(sdl).replaceAll("\n\n\n", "\n").trim());
    }

    static String removeBuiltInDirectives(String sdl) {
        return sdl.replace(DIRECTIVES_EXCLUDE, "");
    }
}
