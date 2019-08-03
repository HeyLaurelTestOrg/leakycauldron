package com.trib3.server.resources

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.hasRootCause
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.prop
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import com.fasterxml.jackson.module.kotlin.readValue
import com.trib3.json.ObjectMapperProvider
import com.trib3.server.graphql.CustomDataFetcherExceptionHandler
import com.trib3.server.graphql.GraphRequest
import com.trib3.server.graphql.RequestIdInstrumentation
import com.trib3.server.graphql.SanitizedGraphQLError
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphQLError
import graphql.execution.AsyncExecutionStrategy
import org.eclipse.jetty.websocket.servlet.WebSocketCreator
import org.testng.annotations.Test

class TestQuery : GraphQLQueryResolver {
    fun test(value: String): String {
        return value
    }

    fun error(): String {
        throw IllegalArgumentException("an error was thrown")
    }

    fun unknownError(): String {
        throw IllegalArgumentException()
    }
}

class GraphQLResourceTest {
    val resource =
        GraphQLResource(
            GraphQL.newGraphQL(
                toSchema(
                    SchemaGeneratorConfig(
                        listOf(this::class.java.packageName)
                    ),
                    listOf(TopLevelObject(TestQuery())),
                    listOf(),
                    listOf()
                )
            )
                .queryExecutionStrategy(AsyncExecutionStrategy(CustomDataFetcherExceptionHandler()))
                .instrumentation(RequestIdInstrumentation())
                .build(),
            WebSocketCreator { _, _ -> null }
        )
    val objectMapper = ObjectMapperProvider().get()
    @Test
    fun testNotConfigured() {
        val notConfiguredResource = GraphQLResource(null, WebSocketCreator { _, _ -> null })
        assertThat {
            notConfiguredResource.graphQL(GraphRequest("", null, null))
        }.isFailure().message().isNotNull().contains("not configured")
    }

    @Test
    fun testSimpleQuery() {
        val result = resource.graphQL(GraphRequest("query {test(value:\"123\")}", null, null))
        val graphQLResult = result.entity as ExecutionResult
        assertThat(graphQLResult.getData<Map<String, String>>()["test"]).isEqualTo("123")
    }

    @Test
    fun testVariablesQuery() {
        val result = resource.graphQL(
            GraphRequest(
                "query(${'$'}val:String!) {test(value:${'$'}val)}",
                mapOf("val" to "123"),
                null
            )
        )
        val graphQLResult = result.entity as ExecutionResult
        assertThat(graphQLResult.getData<Map<String, String>>()["test"]).isEqualTo("123")
    }

    @Test
    fun testErrorQuery() {
        val result = resource.graphQL(GraphRequest("query {error}", mapOf(), null))
        val graphQLResult = result.entity as ExecutionResult
        assertThat(graphQLResult.errors.first()).prop(GraphQLError::getMessage).isEqualTo("an error was thrown")
        assertThat(graphQLResult.errors.first()).isInstanceOf(SanitizedGraphQLError::class)
        assertThat((graphQLResult.errors.first() as SanitizedGraphQLError).exception)
            .hasRootCause(IllegalArgumentException("an error was thrown"))
        val serializedError = objectMapper.writeValueAsString(graphQLResult.errors.first())
        assertThat(objectMapper.readValue<Map<String, *>>(serializedError).keys).doesNotContain("exception")
    }

    @Test
    fun testUnknownErrorQuery() {
        val result = resource.graphQL(GraphRequest("query {unknownError}", mapOf(), null))
        val graphQLResult = result.entity as ExecutionResult
        assertThat(graphQLResult.errors.first()).prop(GraphQLError::getMessage)
            .contains("Exception while fetching data")
        val serializedError = objectMapper.writeValueAsString(graphQLResult.errors.first())
        assertThat(objectMapper.readValue<Map<String, *>>(serializedError).keys).doesNotContain("exception")
    }
}