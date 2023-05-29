package com.zgurski.controller.openapi.restaurant;

import com.zgurski.controller.exceptionhandle.ErrorContainer;
import com.zgurski.domain.entities.Restaurant;
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
        summary = "Gets all available restaurants.",
        description = "Gets all available restaurants."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully loaded restaurants.",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Restaurant.class))),
        @ApiResponse(responseCode = "404", description = "Restaurant not found.", content =
                {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorContainer.class))})
})
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RestaurantFindAllOpenApi {
}