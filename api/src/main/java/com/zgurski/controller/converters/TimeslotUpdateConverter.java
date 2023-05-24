package com.zgurski.controller.converters;

import com.zgurski.controller.requests.CalendarDayUpdateRequest;
import com.zgurski.controller.requests.TimeslotUpdateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Timeslot;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.repository.TimeslotRepository;
import com.zgurski.service.CalendarDayService;
import com.zgurski.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TimeslotUpdateConverter extends TimeslotBaseConverter<TimeslotUpdateRequest, Timeslot> {

    private final TimeslotRepository repository;

    private final TimeslotService service;

    @Override
    public Timeslot convert(TimeslotUpdateRequest request) {

//       TODO active service.checkIfTimeslotExistsById(request.getTimeslotId());
        service.checkIfTimeslotExistsById(request.getTimeslotId());
        Optional<Timeslot> timeslot = repository.findById(request.getTimeslotId());

        timeslot.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(timeslot.get(), request);
    }
}
