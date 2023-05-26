package com.zgurski.controller.requests;

import com.zgurski.domain.enums.ReservationStatuses;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//TODO удалить
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Schema(description = "Search criteria object: search by country, text description, capacity")
public class ReservationSearchCriteria {


    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "NOT_CONFIRMED", type = "ReservationStatuses", description = "Reservation status")
    @NotNull
    private ReservationStatuses reservationStatus;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

