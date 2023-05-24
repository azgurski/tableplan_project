package com.zgurski.controller.converters;

import com.zgurski.controller.requests.DefaultWeekDayCreateRequest;
import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DefaultWeekDayCreateConverter extends DefaultWeekDayBaseConverter<DefaultWeekDayCreateRequest, DefaultWeekDay>  {

    @Override
    public DefaultWeekDay convert(DefaultWeekDayCreateRequest request) {
        //       TODO check

        DefaultWeekDay weekDay = new DefaultWeekDay();

        weekDay.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        weekDay.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(weekDay, request);
    }
}