package com.zgurski.controller.openapi.timeslot;

import com.zgurski.controller.exceptionhandle.ErrorContainer;
import com.zgurski.domain.entities.Timeslot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
        summary = "Gets all timeslots by restaurant id, calendar date and status.",
        description = "Status as query 'key-value' parameter.")
@Parameters({
        @Parameter(name = "isAvailable", required = true, in = ParameterIn.QUERY,
                schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true", type = "boolean",
                        description = "Timeslot status.")),
})
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully loaded Timeslots.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Timeslot.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input value(s).", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))}),
        @ApiResponse(responseCode = "404", description = "Restaurant by id or timeslot(s) not found.",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(implementation = ErrorContainer.class))})
})
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TimeslotFindAllByIsAvailableByDateOpenApi {
}