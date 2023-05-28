package com.zgurski.controller.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "ReservationCreateRequest")
public class ReservationCreateRequest {

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "2023-06-16", type = "string", description = "date of visit")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate localDate;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "National Str. 10", type = "string", description = "time of visit")
    @JsonFormat(pattern="HH:mm")
    private LocalTime localTime;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "National Str. 10", type = "integer", description = "number of guests")
    @Min(1)
    private Integer partySize;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Alexia Melmy", type = "string", description = "full name of contact person")
    @Size(min = 2, max = 200)
    private String guestFullName;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "alexia@gmail.com", type = "string", description = "contact person email")
    @Email
    @Size(max = 200)
    private String guestEmail;

    @NotNull
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "+375 29 000-00-00", type = "string", description = "contact person phone")
    @Size(max = 20)
    private String guestPhone;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "table near window", type = "string", description = "special commentaires, wishes")
    @Size(max = 300)
    private String guestNote;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "Ru", type = "string", description = "guest language")
    @Size(max = 5)
    private String guestLanguage;
}
