package com.zgurski.util.email;

import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;

public interface EmailService {

//    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);

    void prepareConfirmedEmail(Restaurant restaurant, Reservation reservation);

    void prepareNotConfirmedEmail(Restaurant restaurant, Reservation reservation);

    void prepareCancelledEmail(Restaurant restaurant, Reservation reservation);

    void createEmailContextAndSend(Restaurant restaurant, Reservation reservation, String subject, String templatePath);

    void sendMail(EmailContext email);
    }