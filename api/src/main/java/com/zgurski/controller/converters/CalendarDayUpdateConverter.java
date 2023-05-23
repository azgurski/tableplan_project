package com.zgurski.controller.converters;

import com.zgurski.controller.requests.CalendarDayUpdateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.repository.CalendarDayRepository;
import com.zgurski.service.CalendarDayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CalendarDayUpdateConverter extends CalendarDayBaseConverter<CalendarDayUpdateRequest, CalendarDay> {

    private final CalendarDayRepository repository;

    private final CalendarDayService service;

    @Override
    public CalendarDay convert(CalendarDayUpdateRequest request) {

        service.checkIfCalendarDayExistsById(request.getCalendarDayId());
        Optional<CalendarDay> calendarDay = repository.findById(request.getCalendarDayId());

        calendarDay.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(calendarDay.get(), request);
    }
}
