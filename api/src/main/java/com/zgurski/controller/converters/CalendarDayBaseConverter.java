package com.zgurski.controller.converters;

import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import org.springframework.core.convert.converter.Converter;

public abstract class CalendarDayBaseConverter<S, T> implements Converter<S, T> {

    public CalendarDay doConvert(CalendarDay calendarDayForUpdate,
                                 CalendarDayCreateRequest request) {

        calendarDayForUpdate.setLocalDate(request.getLocalDate()); //TODO to check how with PathVariable {YYYY}{}{}
        calendarDayForUpdate.setIsOpen(request.getIsOpen());

        /* System fields filling */
        calendarDayForUpdate.setIsDeleted(false);

        return calendarDayForUpdate;
    }
}
