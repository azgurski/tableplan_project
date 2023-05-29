package com.zgurski.controller.openapi.calendarday;

import com.zgurski.controller.exceptionhandle.ErrorContainer;
import com.zgurski.domain.entities.CalendarDay;
import io.swagger.v3.oas.annotations.Operation;
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
        summary = "Creates a new calendar date if open or not.",
        description = "Creates a new calendar date availability. For example, is open for 2023/06/07."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully saved calendar date.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CalendarDay.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input value(s).", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))}),
        @ApiResponse(responseCode = "404", description = "Restaurant not found by id.", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))})
})
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CalendarDaySaveOpenApi {
}