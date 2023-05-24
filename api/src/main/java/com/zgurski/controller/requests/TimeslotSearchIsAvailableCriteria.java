package com.zgurski.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Schema(description = "Gets the availability on a specific time.")
public class TimeslotSearchIsAvailableCriteria {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "true", type = "boolean", description = "True, if to search available timeslot(s).")
    @NotNull
    private Boolean isAvailable;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}