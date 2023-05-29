package com.zgurski.controller.converters.base;

import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.domain.entities.CalendarDay;
import org.springframework.core.convert.converter.Converter;

public abstract class CalendarDayBaseConverter<S, T> implements Converter<S, T> {

    public CalendarDay doConvert(CalendarDay calendarDayForUpdate,
                                 CalendarDayCreateRequest request) {

        calendarDayForUpdate.setLocalDate(request.getLocalDate());
        calendarDayForUpdate.setIsOpen(request.getIsOpen());

        /* System fields filling */
        calendarDayForUpdate.setIsDeleted(false);

        return calendarDayForUpdate;
    }
}
