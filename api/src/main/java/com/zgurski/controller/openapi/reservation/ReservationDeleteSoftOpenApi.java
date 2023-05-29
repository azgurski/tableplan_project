package com.zgurski.controller.openapi.reservation;

import com.zgurski.controller.exceptionhandle.ErrorContainer;
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
        summary = "Updates status isDeleted from false to true.",
        description = "Without drop from database."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservation with {id} is softDeleted successfully.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema())),
        @ApiResponse(responseCode = "400", description = "Invalid input value(s).", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))}),
        @ApiResponse(responseCode = "404", description = "Restaurant or reservation not found by id.", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))})
})
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReservationDeleteSoftOpenApi {
}