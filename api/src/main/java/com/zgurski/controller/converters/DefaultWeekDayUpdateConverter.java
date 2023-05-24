package com.zgurski.controller.converters;

import com.zgurski.controller.requests.DefaultWeekDayUpdateRequest;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.repository.DefaultWeekDayRepository;
import com.zgurski.service.DefaultWeekDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultWeekDayUpdateConverter extends DefaultWeekDayBaseConverter<DefaultWeekDayUpdateRequest, DefaultWeekDay> {

    private final DefaultWeekDayRepository repository;

    private final DefaultWeekDayService service;

    @Override
    public DefaultWeekDay convert(DefaultWeekDayUpdateRequest request) {

        //       TODO check

        service.checkIfDefaultWeekDayExistsById(request.getDefaultWeekDayId());
        Optional<DefaultWeekDay> weekDay = repository.findById(request.getDefaultWeekDayId());

        weekDay.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(weekDay.get(), request);
    }
}
