package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "CalendarDay (Availability for Date) CreateRequest")
public class CalendarDayCreateRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-05-31", type = "string", description = "Calendar date")
    @NotNull
    private LocalDate localDate;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true", type = "boolean", description = "True, if restaurant is open on this day.")
    @NotNull
    private Boolean isOpen;
}
