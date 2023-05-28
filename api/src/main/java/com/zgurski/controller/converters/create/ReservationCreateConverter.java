package com.zgurski.controller.converters.create;

import com.zgurski.controller.converters.base.ReservationBaseConverter;
import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.domain.entities.Reservation;
import com.zgurski.util.RandomValuesGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReservationCreateConverter extends ReservationBaseConverter<ReservationCreateRequest, Reservation> {

    private final RandomValuesGenerator randomValuesGenerator;

    @Override
    public Reservation convert(ReservationCreateRequest request) {
        //       TODO check

        Reservation reservation = new Reservation();

        reservation.setPnr(randomValuesGenerator.generateReservationCode());


        /* System fields filling */
        reservation.setCreated(Timestamp.valueOf(LocalDateTime.now()));
        reservation.setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(reservation, request);
    }
}
