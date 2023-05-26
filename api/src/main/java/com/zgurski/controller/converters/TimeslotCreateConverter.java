package com.zgurski.controller.converters;

import com.zgurski.controller.converters.TimeslotBaseConverter;
import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.controller.requests.TimeslotCreateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Timeslot;
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