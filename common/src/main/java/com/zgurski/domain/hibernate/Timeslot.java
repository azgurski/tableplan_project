package com.zgurski.domain.hibernate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalTime;

@Data
@Entity
@EqualsAndHashCode(exclude = {
        "calendarDay"
})
@ToString(exclude = {
        "calendarDay"
})
@Table(name = "timeslots")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//@NamedQuery(name = "m_restaurant_multiple_ids_search", query = "select r from Restaurant where r.id = :restaurantIds)
//@Cacheable
public class Timeslot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    private Long timeslotId;

    @Column(name = "local_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime localTime;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "current_slot_capacity")
    @JsonIgnore
    private Integer currentSlotCapacity;

    @Column(name = "max_slot_capacity")
    @JsonIgnore
    private Integer maxSlotCapacity;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp created;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp changed;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "calendar_day_id")
    @JsonBackReference
    private CalendarDay calendarDay;
}