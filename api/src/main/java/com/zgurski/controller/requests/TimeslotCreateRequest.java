package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "Timeslot CreateRequest")
public class TimeslotCreateRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "12:30", type = "string",
            description = "Local time")
    @NotNull
    private LocalTime localTime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true", type = "boolean",
            description = "True, if timeslot is available")
    @NotNull
    private Boolean isAvailable;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "20", type = "integer",
            description = "Maximum guests per time")
    @NotNull
    @Min(0)
    private Integer maxSlotCapacity;
}