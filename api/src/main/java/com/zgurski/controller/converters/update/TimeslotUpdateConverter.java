package com.zgurski.controller.converters.update;

import com.zgurski.controller.converters.base.TimeslotBaseConverter;
import com.zgurski.controller.requests.TimeslotUpdateRequest;
import com.zgurski.domain.entities.Timeslot;
import com.zgurski.repository.TimeslotRepository;
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

        service.checkIfTimeslotExistsById(request.getTimeslotId());
        Optional<Timeslot> timeslot = repository.findById(request.getTimeslotId());

        timeslot.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(timeslot.get(), request);
    }
}