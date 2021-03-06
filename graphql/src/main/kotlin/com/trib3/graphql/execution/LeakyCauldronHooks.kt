package com.trib3.graphql.execution

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.generator.hooks.FlowSubscriptionSchemaGeneratorHooks
import graphql.language.StringValue
import graphql.scalars.ExtendedScalars
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import io.dropwizard.auth.Authorizer
import org.threeten.extra.YearQuarter
import java.math.BigDecimal
import java.security.Principal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Year
import java.time.YearMonth
import java.util.UUID
import javax.annotation.Nullable
import javax.inject.Inject
import kotlin.reflect.KType

internal val YEAR_SCALAR = GraphQLScalarType.newScalar()
    .name("Year")
    .description("Year, for example 2019")
    .coercing(
        object : Coercing<Year, String> {
            private fun parse(input: String): Year {
                return try {
                    Year.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): Year {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): Year {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is Year -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val YEAR_MONTH_SCALAR = GraphQLScalarType.newScalar()
    .name("Month")
    .description("Year + Month, for example 2019-01")
    .coercing(
        object : Coercing<YearMonth, String> {
            private fun parse(input: String): YearMonth {
                return try {
                    YearMonth.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): YearMonth {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): YearMonth {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is YearMonth -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val YEAR_QUARTER_SCALAR = GraphQLScalarType.newScalar()
    .name("Quarter")
    .description("Year + Quarter, for example 2019-Q1")
    .coercing(
        object : Coercing<YearQuarter, String> {
            private fun parse(input: String): YearQuarter {
                return try {
                    YearQuarter.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): YearQuarter {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): YearQuarter {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is YearQuarter -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val LOCAL_DATETIME_SCALAR = GraphQLScalarType.newScalar()
    .name("LocalDateTime")
    .description(
        "Year + Month + Day Of Month + Time (Hour:Minute + Optional(Second:Milliseconds)), " +
            "for example 2019-10-31T12:31:45.129"
    )
    .coercing(
        object : Coercing<LocalDateTime, String> {
            private fun parse(input: String): LocalDateTime {
                return try {
                    LocalDateTime.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): LocalDateTime {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): LocalDateTime {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is LocalDateTime -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val LOCAL_DATE_SCALAR = GraphQLScalarType.newScalar()
    .name("LocalDate")
    .description("Year + Month + Day Of Month, for example 2019-10-31")
    .coercing(
        object : Coercing<LocalDate, String> {
            private fun parse(input: String): LocalDate {
                return try {
                    LocalDate.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): LocalDate {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): LocalDate {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is LocalDate -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val LOCAL_TIME_SCALAR = GraphQLScalarType.newScalar()
    .name("LocalTime")
    .description("Hour + Minute + Optional (Second + Millisecond) , for example 12:31:45.129")
    .coercing(
        object : Coercing<LocalTime, String> {
            private fun parse(input: String): LocalTime {
                return try {
                    LocalTime.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): LocalTime {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): LocalTime {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is LocalTime -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val OFFSET_DATETIME_SCALAR = GraphQLScalarType.newScalar()
    .name("OffsetDateTime")
    .description(
        "Year + Month + Day Of Month + Time (Hour:Minute + Optional(Second.Milliseconds)) " +
            "+ Offset, for example 2019-10-31T12:31:45.129-07:00"
    )
    .coercing(
        object : Coercing<OffsetDateTime, String> {
            private fun parse(input: String): OffsetDateTime {
                return try {
                    OffsetDateTime.parse(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): OffsetDateTime {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): OffsetDateTime {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is OffsetDateTime -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

internal val UUID_SCALAR = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("String representation of a UUID")
    .coercing(
        object : Coercing<UUID, String> {
            private fun parse(input: String): UUID {
                try {
                    return UUID.fromString(input)
                } catch (e: Exception) {
                    throw CoercingSerializeException("can't parse $input", e)
                }
            }

            override fun parseValue(input: Any): UUID {
                return parse(input.toString())
            }

            override fun parseLiteral(input: Any): UUID {
                return when (input) {
                    is StringValue -> parse(input.value)
                    else -> throw CoercingSerializeException("can't parse $input")
                }
            }

            override fun serialize(dataFetcherResult: Any): String {
                return when (dataFetcherResult) {
                    is UUID -> dataFetcherResult.toString()
                    else -> throw CoercingSerializeException("can't serialize ${dataFetcherResult::class}")
                }
            }
        }
    )
    .build()

/**
 * Schema generator hooks implementation that defines scalars for java.time (and threeten-extras) objects
 * and wires up the @[GraphQLAuth] directive.
 */
class LeakyCauldronHooks @Inject constructor(
    @Nullable authorizer: Authorizer<Principal>?,
    manualWiring: Map<String, KotlinSchemaDirectiveWiring>
) :
    FlowSubscriptionSchemaGeneratorHooks() {
    constructor() : this(null, emptyMap())

    override val wiringFactory = KotlinDirectiveWiringFactory(
        mapOf(
            "auth" to GraphQLAuthDirectiveWiring(authorizer)
        ) + manualWiring
    )

    override fun willGenerateGraphQLType(type: KType): GraphQLType? {
        // TODO: include more from ExtendedScalars?
        //       Use impls from ExtendedScalars instead of our own for date/time/datetimes?
        return when (type.classifier) {
            Year::class -> YEAR_SCALAR
            YearMonth::class -> YEAR_MONTH_SCALAR
            YearQuarter::class -> YEAR_QUARTER_SCALAR
            LocalDateTime::class -> LOCAL_DATETIME_SCALAR
            LocalDate::class -> LOCAL_DATE_SCALAR
            LocalTime::class -> LOCAL_TIME_SCALAR
            OffsetDateTime::class -> OFFSET_DATETIME_SCALAR
            UUID::class -> UUID_SCALAR
            BigDecimal::class -> ExtendedScalars.GraphQLBigDecimal
            else -> null
        }
    }
}
