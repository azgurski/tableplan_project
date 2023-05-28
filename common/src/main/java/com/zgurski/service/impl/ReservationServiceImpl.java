package com.zgurski.service.impl;

import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.entities.Reservation;
import com.zgurski.domain.entities.Restaurant;
import com.zgurski.exception.EntityIncorrectOwnerException;
import com.zgurski.exception.EntityNotAddedException;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.exception.InvalidInputValueException;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.service.ReservationService;
import com.zgurski.service.RestaurantService;
import com.zgurski.service.TimeslotService;
import com.zgurski.util.CustomErrorMessageGenerator;
import com.zgurski.util.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final RestaurantService restaurantService;

    public final TimeslotService timeslotService;

    private final EmailService emailService;

    private final CustomErrorMessageGenerator messageGenerator;

    public List<Reservation> findAll() {

        List<Reservation> allReservations = reservationRepository.findAll();
        return checkIfReservationListIsNotEmpty(allReservations);
    }

    public Page<Reservation> findAllPageable(Pageable pageable) {

        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
        return checkIfPageReservationIsNotEmpty(reservationPage);
    }

    public List<Reservation> findReservationsByRestaurantId(Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<Reservation> allReservations = reservationRepository
                .findReservationsByRestaurant_RestaurantIdOrderByLocalDateAscLocalTimeAsc(restaurantId);

        checkIfReservationListIsNotEmpty(allReservations);

        return allReservations;
    }

    public Optional<Reservation> findByReservationIdAndRestaurantId(Long reservationId, Long restaurantId) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);
        checkIfReservationExistsById(reservationId);
        checkBelongingReservationToRestaurant(restaurantId, reservationId);

        return reservationRepository.findById(reservationId);
    }

    public List<Reservation> findByStatus(Long restaurantId, ReservationStatuses status) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<Reservation> reservations = reservationRepository
                .findReservationsByRestaurant_RestaurantIdAndStatusOrderByLocalDateAscLocalTimeAsc(restaurantId, status);

        return checkIfReservationListIsNotEmpty(reservations);
    }

    public List<Reservation> findAllByDateAndRestaurantId(Long restaurantId, int year, int month, int day) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        LocalDate localDate = LocalDate.of(year, month, day);

        return checkIfReservationListIsNotEmpty(
                reservationRepository.findAllByDateAndRestaurant(localDate, restaurant));
    }

    public List<Reservation> findAllByDateStatusAndRestaurantId(
            Long restaurantId, ReservationStatuses status, int year, int month, int day) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        LocalDate localDate = LocalDate.of(year, month, day);

        return checkIfReservationListIsNotEmpty(
                reservationRepository.findAllByDateStatusAndRestaurantId(localDate, status, restaurant));
    }

    public List<Object> getOccupancyByHour(Long restaurantId, int year, int month, int day) {

        restaurantService.checkIfRestaurantExistsById(restaurantId);

        List<Object> occupancyByHour = reservationRepository.getOccupancyByHour(restaurantId, year, month, day);

        if (occupancyByHour.isEmpty()) {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Reservation.class));
        }

        return occupancyByHour;
    }

    public Reservation save(Long restaurantId, Reservation reservation) {

        if (reservation.getLocalDate().isBefore(LocalDate.now())){
            throw new InvalidInputValueException();
        }

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        reservation.setRestaurant(restaurant);

        timeslotService.checkTimeslotCapacity(
                reservation.getPartySize(), reservation.getLocalDate(), reservation.getLocalTime(), restaurant);

        return reservationRepository.save(reservation);
    }

    public Reservation update(Long restaurantId, Reservation reservationToUpdate, int initialPartySize) {

        if (reservationToUpdate.getLocalDate().isBefore(LocalDate.now())){
            throw new InvalidInputValueException();
        }

        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        Integer incrementPartySize = reservationToUpdate.getPartySize() - initialPartySize;


        timeslotService.checkTimeslotCapacity(
                incrementPartySize, reservationToUpdate.getLocalDate(), reservationToUpdate.getLocalTime(), restaurant);

        timeslotService.updateTimeslotCapacity(-initialPartySize, reservationToUpdate.getLocalDate(),
                reservationToUpdate.getLocalTime(), restaurant);

        reservationToUpdate.setStatus(ReservationStatuses.UNREAD);
        reservationToUpdate.setRestaurant(restaurant);

        return reservationRepository.saveAndFlush(reservationToUpdate);
    }

    public Reservation updateStatus(Long restaurantId, Long reservationId, ReservationStatuses newStatus) {

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        Reservation reservation = findByReservationIdAndRestaurantId(reservationId, restaurantId).get();

        ReservationStatuses currentStatus = reservation.getStatus();
        Integer partySize = reservation.getPartySize();

        /* unread -> cancelled, not confirmed -> just change rsv.status and send email */
        /* unread -> confirmed -> increase current timeslot capacity, change rsv.status and send email */
        /* confirmed -> cancelled, not confirmed -> decrease capacity, change rsv.status and send email */

        switch (newStatus) {

            case CONFIRMED -> {

                checkIfWasUnreadUpdateTimeslotAndReservation(newStatus, restaurant, reservation, currentStatus, partySize);
                emailService.prepareConfirmedEmail(restaurant, reservation);
            }

            case NOT_CONFIRMED -> {

                checkIfWasConfirmedUpdateTimeslotAndReservation(
                        newStatus, restaurant, reservation, currentStatus, partySize);
                emailService.prepareNotConfirmedEmail(restaurant, reservation);
            }

            case CANCELLED -> {

                checkIfWasConfirmedUpdateTimeslotAndReservation(
                        newStatus, restaurant, reservation, currentStatus, partySize);
                emailService.prepareCancelledEmail(restaurant, reservation);
            }

            default -> reservationRepository.updateStatus(reservationId, newStatus);
        }

        return reservationRepository.findByReservationId(reservationId).get();
    }

    private void checkIfWasUnreadUpdateTimeslotAndReservation(ReservationStatuses newStatus, Restaurant restaurant, Reservation reservation, ReservationStatuses currentStatus, Integer partySize) {

        if (currentStatus == ReservationStatuses.UNREAD) {
            timeslotService.updateTimeslotCapacity(partySize, reservation.getLocalDate(),
                    reservation.getLocalTime(), restaurant);

            reservation.setStatus(newStatus);
            reservationRepository.saveAndFlush(reservation);

        } else {
            throw new EntityNotAddedException(messageGenerator
                    .createImpossibleToUpdateEntity(Reservation.class, currentStatus));
        }
    }

    private void checkIfWasConfirmedUpdateTimeslotAndReservation(ReservationStatuses newStatus, Restaurant restaurant, Reservation reservation, ReservationStatuses currentStatus, Integer partySize) {

        if (currentStatus == ReservationStatuses.CONFIRMED) {
            timeslotService.updateTimeslotCapacity(-partySize, reservation.getLocalDate(),
                    reservation.getLocalTime(), restaurant);
        }

        reservation.setStatus(newStatus);
        reservationRepository.saveAndFlush(reservation);
    }

    public Long deleteSoft(Long restaurantId, Long reservationId) {

        checkBelongingReservationToRestaurant(restaurantId, reservationId);
        reservationRepository.deleteSoft(reservationId);

        findByReservationIdAndRestaurantId(reservationId, restaurantId);

        return findByReservationIdAndRestaurantId(reservationId, restaurantId).get().getReservationId();
    }


    /* Verifications, custom exceptions */
    public Boolean checkBelongingReservationToRestaurant(Long restaurantId, Long reservationId) {

        Optional<Reservation> reservation = reservationRepository
                .findReservationByReservationIdAndRestaurant_RestaurantId(reservationId, restaurantId);

        if (reservation.isPresent()) {
            return true;
        } else {
            throw new EntityIncorrectOwnerException(messageGenerator
                    .createNoCorrectOwnerMessage(Restaurant.class, Reservation.class, reservationId.toString()));
        }
    }

    public Boolean checkIfReservationExistsById(Long id) {

        if (reservationRepository.existsReservationByReservationId(id)) {
            return true;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Reservation.class, id.toString()));
        }
    }

    private List<Reservation> checkIfReservationListIsNotEmpty(List<Reservation> allReservations) {

        if (!allReservations.isEmpty()) {
            return allReservations;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNoEntityFoundMessage(Reservation.class));
        }
    }

    private Page<Reservation> checkIfPageReservationIsNotEmpty(Page<Reservation> reservationPage) {

        if (!reservationPage.isEmpty()) {
            return reservationPage;

        } else {
            throw new EntityNotFoundException(messageGenerator
                    .createNotFoundByIdMessage(Page.class, reservationPage.toString()));
        }
    }

}