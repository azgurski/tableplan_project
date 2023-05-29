package com.zgurski.controller.converters.create;

import com.zgurski.controller.converters.base.TimeslotBaseConverter;
import com.zgurski.controller.requests.TimeslotCreateRequest;
import com.zgurski.domain.entities.Timeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TimeslotCreateConverter extends TimeslotBaseConverter<TimeslotCreateRequest, Timeslot> {

    @Override
    public Timeslot convert(TimeslotCreateRequest request) {

        Timeslot timeslot = new Timeslot();

        timeslot.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        timeslot.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(timeslot, request);
    }
}