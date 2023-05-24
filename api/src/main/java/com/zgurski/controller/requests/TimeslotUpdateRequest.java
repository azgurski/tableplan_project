package com.zgurski.controller.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeslotUpdateRequest extends TimeslotCreateRequest {
    @NotNull
    private Long timeslotId;
}
