package com.zgurski.controller.converters;

import com.zgurski.controller.requests.ReservationCreateRequest;
import com.zgurski.controller.requests.ReservationUpdateRequest;
import com.zgurski.domain.hibernate.Reservation;
import org.springframework.core.convert.converter.Converter;

public abstract class ReservationBaseConverter<S, T> implements Converter<S, T> {

    public Reservation doConvert(Reservation reservationForUpdate,
                                 ReservationCreateRequest request) {

        reservationForUpdate.setLocalDate(request.getLocalDate());
        reservationForUpdate.setLocalTime(request.getLocalTime());
        reservationForUpdate.setPartySize(request.getPartySize());
        reservationForUpdate.setGuestFullName(request.getGuestFullName());
        reservationForUpdate.setGuestEmail(request.getGuestEmail());
        reservationForUpdate.setGuestPhone(request.getGuestPhone());
        reservationForUpdate.setGuestNote(request.getGuestNote());
        reservationForUpdate.setGuestLanguage(request.getGuestLanguage());

        /* System fields filling */
        reservationForUpdate.setIsDeleted(false);

        return reservationForUpdate;
    }
}