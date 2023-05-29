package com.zgurski.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "reservations", "defaultWeekDays", "calendarDays"
})
@ToString(exclude = {
        "reservations", "defaultWeekDays", "calendarDays"
})
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column
    private String phone;

    @Column
    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    @Column
    private String city;

    @Column
    private String country;

    @Column
    private String website;

    @Column(name = "restaurant_language")
    private String restaurantLanguage;

    @Column(name = "restaurant_timezone")
    private String restaurantTimezone;

    @Column(name = "image_url")
    private String imageURL;

    @Column(name = "logo_url")
    private String logoURL;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp created;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp changed;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "default_timeslot_capacity")
    private Integer defaultTimeslotCapacity;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<Reservation> reservations = Collections.emptySet();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<DefaultWeekDay> defaultWeekDays = Collections.emptySet();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private Set<CalendarDay> calendarDays = Collections.emptySet();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}