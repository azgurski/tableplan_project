package com.zgurski.controller.converters;

import com.zgurski.controller.requests.CalendarDayCreateRequest;
import com.zgurski.controller.requests.TimeslotCreateRequest;
import com.zgurski.domain.hibernate.CalendarDay;
import com.zgurski.domain.hibernate.Timeslot;
import org.springframework.core.convert.converter.Converter;

public abstract class TimeslotBaseConverter<S, T> implements Converter<S, T> {

    public Timeslot doConvert(Timeslot timeslotForUpdate,
                              TimeslotCreateRequest request) {

        timeslotForUpdate.setLocalTime(request.getLocalTime()); //TODO to check how with PathVariable {YYYY}{}{}
        timeslotForUpdate.setMaxSlotCapacity(request.getMaxSlotCapacity());
        timeslotForUpdate.setIsAvailable(request.getIsAvailable());

        /* System fields filling */
        timeslotForUpdate.setIsDeleted(false);

        return timeslotForUpdate;
    }
}
