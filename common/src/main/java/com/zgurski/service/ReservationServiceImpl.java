package com.zgurski.service;

import com.zgurski.domain.enums.ReservationStatuses;
import com.zgurski.domain.hibernate.DefaultWeekDay;
import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.EntityIncorrectOwnerException;
import com.zgurski.exception.EntityNotFoundException;
import com.zgurski.repository.ReservationRepository;
import com.zgurski.util.CustomErrorMessageGenerator;
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

    private final RestaurantService restaurantService;

    private final ReservationRepository reservationRepository;

    private final CustomErrorMessageGenerator messageGenerator;

    public final TimeslotService timeslotService;

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

        Restaurant restaurant = restaurantService.findById(restaurantId).get();
        reservation.setRestaurant(restaurant);

        timeslotService.checkTimeslotCapacity(
                reservation.getPartySize(), reservation.getLocalDate(), reservation.getLocalTime(), restaurant);

        return reservationRepository.save(reservation);
    }

    public Reservation update(Long restaurantId, Reservation reservation) {

        Long reservationId = reservation.getReservationId();
        Restaurant restaurant = restaurantService.findById(restaurantId).get();

        checkIfReservationExistsById(reservationId);
        checkBelongingReservationToRestaurant(restaurantId, reservationId);

        timeslotService.checkTimeslotCapacity(
                reservation.getPartySize(), reservation.getLocalDate(), reservation.getLocalTime(), restaurant);

        reservation.setRestaurant(restaurant);

        return reservationRepository.save(reservation);
    }

    public Long deleteSoft(Long restaurantId, Long reservationId) {

        findByReservationIdAndRestaurantId(reservationId, restaurantId);
        reservationRepository.deleteSoft(reservationId);

        return reservationId;
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