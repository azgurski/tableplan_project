package com.zgurski.util.email;

import com.zgurski.domain.hibernate.Reservation;
import com.zgurski.domain.hibernate.Restaurant;
import com.zgurski.exception.EmailNotSentException;
import com.zgurski.util.CustomErrorMessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final CustomErrorMessageGenerator messageGenerator;

    private MimeMessageHelper helper;

    @Value("${email.subject.reservation.confirmed}")
    private String confirmationSubject;

    @Value("${email.subject.reservation.not-confirmed}")
    private String nonConfirmationSubject;

    @Value("${email.subject.reservation.cancelled}")
    private String annulationSubject;

    @Value("${email.template-path.reservation.confirmed}")
    private String confirmationPath;

    @Value("${email.template-path.reservation.not-confirmed}")
    private String nonConfirmationPath;

    @Value("${email.template-path.reservation.cancelled}")
    private String annulationPath;

    @Value("${email.pattern.date}")
    private String datePattern;

    @Value("${email.address-details.from}")
    private String addressFrom;

    @Value("${email.address-details.to}")
    private String addressTo;

    @Value("${email.address-details.recipient-email}")
    private String recipientEmail;

    @Value("${email.address-details.language}")
    private String languageEmail;

    public void prepareConfirmedEmail(Restaurant restaurant, Reservation reservation) {
        createEmailContextAndSend(restaurant, reservation, confirmationSubject, confirmationPath);
    }

    public void prepareNotConfirmedEmail(Restaurant restaurant, Reservation reservation) {
        createEmailContextAndSend(restaurant, reservation, nonConfirmationSubject, nonConfirmationPath);
    }

    public void prepareCancelledEmail(Restaurant restaurant, Reservation reservation) {
        createEmailContextAndSend(restaurant, reservation, annulationSubject, annulationPath);
    }

    public void createEmailContextAndSend(Restaurant restaurant, Reservation reservation, String subject, String templatePath) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern, Locale.UK);
        String formattedDate = formatter.format(reservation.getLocalDate());

        Map<String, Object> thymeleafVariables = Map.of(
                "restaurantName", restaurant.getRestaurantName(),
                "restaurantEmail", restaurant.getContactEmail(),
                "restaurantPhone", restaurant.getPhone(),
                "website", restaurant.getWebsite(),
                "pnr", reservation.getPnr(),
                "guestFullName", reservation.getGuestFullName(),
                "reservationDate", formattedDate,
                "reservationTime", reservation.getLocalTime(),
                "partySize", reservation.getPartySize());


        EmailContext emailContext = EmailContext.builder()
                .from(addressFrom)
                .to(addressTo)
                .subject(subject)
                .email(recipientEmail)
                .attachment(null)
                .fromDisplayName(restaurant.getRestaurantName())
                .emailLanguage(languageEmail)
                .displayName(reservation.getGuestFullName())
                .templateLocation(templatePath)
                .context(thymeleafVariables)
                .build();

        sendMail(emailContext);
    }

    public void sendMail(EmailContext email) {

        MimeMessage message = emailSender.createMimeMessage();

        try {

            helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(email.getContext());
            String emailContent = templateEngine.process(email.getTemplateLocation(), context);

            helper.setTo(email.getTo());
            helper.setFrom(email.getFrom());
            helper.setSubject(email.getSubject());
            helper.setText(emailContent, true);

            emailSender.send(message);

        } catch (MessagingException ex) {
            throw new EmailNotSentException(messageGenerator.createEmailNotSentMessage(email.getTo()));
        }
    }


//    @Override
//    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
//
//        MimeMessage message = emailSender.createMimeMessage();
//
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//
//            helper.setFrom("noreply@tableplan.fr.com");
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(text);
//
//            FileSystemResource file
//                    = new FileSystemResource(new File(pathToAttachment)); //"path/to/file"
//            helper.addAttachment("Reservation_" +
////                pnr
//                    "", file);
//
//            emailSender.send(message);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }

}