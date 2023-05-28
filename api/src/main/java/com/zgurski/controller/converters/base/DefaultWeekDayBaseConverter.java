package com.zgurski.controller.converters.base;

import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.domain.entities.DefaultWeekDay;
import org.springframework.core.convert.converter.Converter;

public abstract class DefaultWeekDayBaseConverter<S, T> implements Converter<S, T> {

    public DefaultWeekDay doConvert(DefaultWeekDay weekDayForUpdate,
                                    DefaultWeekDayCreateRequest request) {

        weekDayForUpdate.setIsOpen(request.getIsOpen());
        weekDayForUpdate.setDayOfWeek(request.getDayOfWeek());

        /* System fields filling */
        weekDayForUpdate.setIsDeleted(false);

        return weekDayForUpdate;
    }
}
