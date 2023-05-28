package com.zgurski.controller.converters.create;

import com.zgurski.controller.converters.base.CalendarDayBaseConverter;
import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.domain.entities.CalendarDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CalendarDayCreateConverter extends CalendarDayBaseConverter<CalendarDayCreateRequest, CalendarDay> {

    @Override
    public CalendarDay convert(CalendarDayCreateRequest request) {
        //       TODO check

        CalendarDay calendarDay = new CalendarDay();

        calendarDay.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(calendarDay, request);
    }
}