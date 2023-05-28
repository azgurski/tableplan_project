package com.zgurski.controller.converters.create;

import com.zgurski.controller.converters.base.DefaultWeekDayBaseConverter;
import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.domain.entities.DefaultWeekDay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DefaultWeekDayCreateConverter extends DefaultWeekDayBaseConverter<DefaultWeekDayCreateRequest, DefaultWeekDay> {

    @Override
    public DefaultWeekDay convert(DefaultWeekDayCreateRequest request) {
        //       TODO check

        DefaultWeekDay weekDay = new DefaultWeekDay();

        weekDay.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        weekDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(weekDay, request);
    }
}