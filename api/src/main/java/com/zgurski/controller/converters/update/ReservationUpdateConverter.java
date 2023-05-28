package com.zgurski.controller.converters.update;

import com.zgurski.controller.converters.base.ReservationBaseConverter;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.entities.Reservation;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationUpdateConverter extends ReservationBaseConverter<ReservationUpdateRequest, Reservation> {

    private final ReservationRepository repository;

    private final ReservationService service;

    @Override
    public Reservation convert(ReservationUpdateRequest request) {

        //       TODO check

        service.checkIfReservationExistsById(request.getReservationId());

        Optional<Reservation> reservation = repository.findByReservationId(request.getReservationId());


        reservation.get().setChanged(Timestamp.valueOf(LocalDateTime.now()));

        return doConvert(reservation.get(), request);
    }
}
