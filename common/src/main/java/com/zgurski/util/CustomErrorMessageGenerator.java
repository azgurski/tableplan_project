package com.zgurski.util;

import com.zgurski.domain.enums.ReservationStatuses;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class CustomErrorMessageGenerator {

    public String createNotFoundByIdMessage(Class clazz, String idValue) {
        return clazz.getSimpleName() + " with {id=" + idValue + "} not found.";
    }

    public String createNoCorrectOwnerMessage(Class parentClazz, Class childClazz, String value) {
        return childClazz.getSimpleName() + " {" + value + "} not found at selected " +
                parentClazz.getSimpleName() + ".";
    }

    public String createNoEntityFoundMessage(Class clazz) {
        return "No " + clazz.getSimpleName().toString().toLowerCase() + "(s) found.";
    }

    public String createNoEntityFoundByLocalDateMessage(Class clazz, LocalDate localDate) {
        return "Availability not scheduled for " + clazz.getSimpleName() + "" +
                "={" + localDate + "}.";
    }

    public String createEntityIsUnavailableMessage(Class clazz, LocalDate localDate) {
        return clazz.getSimpleName() + " is closed on day={" + localDate + "}.";
    }

    public String createNoEntityFoundByLocalTimeMessage(Class clazz, LocalTime localTime) {
        return clazz.getSimpleName() + "={" + localTime + "} not scheduled on this day.";
    }

    public String createNoDuplicatesAllowedByLocalTimeMessage(Class clazz, String value) {
        return clazz.getSimpleName() + " of value {" + value + "} already exists for this restaurant. " +
                "Try to update it.";
    }

    public String createEntityNotAvailableByTimeMessage(Class clazz, LocalDate localDate, LocalTime localTime) {
        return clazz.getSimpleName() + " is full on " + localDate + " at {" + localTime + "}. Try another slot.";
    }

    public String createEmailNotSentMessage(String emailAddress) {
        return "Failed to send an email to {" + emailAddress + "}.";
    }

    public String createImpossibleToUpdateEntity(Class clazz, ReservationStatuses status) {
        return "Impossible to update " + clazz.getSimpleName().toLowerCase() + " with status ={" + status + "}.";
    }
}