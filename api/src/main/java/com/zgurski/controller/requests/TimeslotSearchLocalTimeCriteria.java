package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Schema(description = "Gets the availability on a specific time.")
public class TimeslotSearchLocalTimeCriteria {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "12-30", type = "string", description = "Search timeslot by Local time.")
    @Pattern(regexp = "^(2[0-3]|[01]?[0-9])_([0-5]?[0-9])$")
    @NotNull
    private String localTime;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}