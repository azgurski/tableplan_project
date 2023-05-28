package com.zgurski.controller.converters.base;

import com.zgurski.controller.requests.TimeslotCreateRequest;
import com.zgurski.domain.entities.Timeslot;
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
