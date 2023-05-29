package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "RestaurantCreateRequest")
public class RestaurantCreateRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "La Fourchette", type = "string",
            description = "restaurant name")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 symbols.")
    @NotNull
    private String restaurantName;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "restaurant@gmail.com", type = "string",
            description = "restaurant email")
    @Email
    @Size(max = 200)
    @NotNull
    private String contactEmail;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "+375 29 000-00-00", type = "string",
            description = "restaurant phone")
    @Size(max = 20)
    private String phone;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "National Str. 10", type = "string",
            description = "restaurant address")
    @Size(min = 2, max = 200)
    private String address;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "84000", type = "string",
            description = "restaurant postal code")
    @Size(min = 2, max = 10)
    private String postalCode;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Rome", type = "string",
            description = "restaurant city")
    @Size(min = 2, max = 50)
    private String city;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "Italy", type = "string",
            description = "restaurant country")
    @Size(min = 2, max = 30)
    private String country;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://website.com", type = "string",
            description = "restaurant website")
    @Size(min = 2, max = 100)
    private String website;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "En", type = "string",
            description = "restaurant language")
    @Size(min = 2, max = 5)
    private String restaurantLanguage;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "GMT+3", type = "GMT+3",
            description = "restaurant timezone")
    @Size(min = 2, max = 10)
    private String restaurantTimezone;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://imageurl.com", type = "string",
            description = "restaurant image URL")
    @Size(min = 2, max = 500)
    private String imageURL;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://logourl.com", type = "string",
            description = "restaurant logo URL")
    @Size(min = 2, max = 500)
    private String logoURL;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "15", type = "integer",
            description = "default number of guests per time")
    @NotNull
    @Min(0)
    private Integer defaultTimeslotCapacity;
}