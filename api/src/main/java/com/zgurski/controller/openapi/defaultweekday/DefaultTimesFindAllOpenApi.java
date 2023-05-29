package com.zgurski.controller.openapi.defaultweekday;

import com.zgurski.domain.entities.DefaultTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Operation(
        summary = "Gets all possible begin time values from 8:00 to 7:45.",
        description = "From 8:00 to 7:45 with a 15 minute interval from the reference table.",
        tags = "Hateoas"
)
@ApiResponse(responseCode = "200", description = "Successfully loaded default times.",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = DefaultTime.class)))
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DefaultTimesFindAllOpenApi {
}