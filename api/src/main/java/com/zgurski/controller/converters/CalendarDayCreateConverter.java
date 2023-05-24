package com.zgurski.controller.converters;

import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CalendarDayCreateConverter extends CalendarDayBaseConverter<CalendarDayCreateRequest, CalendarDay>  {

    @Override
    public CalendarDay convert(CalendarDayCreateRequest request) {
        //       TODO check

        CalendarDay calendarDay = new CalendarDay();

        calendarDay.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        calendarDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(calendarDay, request);
    }
}