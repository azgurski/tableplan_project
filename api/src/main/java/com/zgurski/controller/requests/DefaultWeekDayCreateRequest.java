package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "DefaultDay(Schedule)CreateRequest")
public class DefaultWeekDayCreateRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "MONDAY", type = "DayOfWeek", description = "Day of week")
    @NotNull
    private DayOfWeek dayOfWeek;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true", type = "boolean", description = "True, if restaurant is open on this day.")
    @NotNull
    private Boolean isOpen;
}
