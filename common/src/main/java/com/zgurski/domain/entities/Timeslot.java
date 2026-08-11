package com.zgurski.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@EqualsAndHashCode(exclude = {
        "calendarDay"
})
@ToString(exclude = {
        "calendarDay"
})
@Table(name = "timeslots")
public class Timeslot {

    @Id
    @GeneratedValue(generator = "timeslotIdGenerator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "timeslotIdGenerator", sequenceName = "enabled_generated_type_sequence",
            allocationSize = 1)
    @Column(name = "timeslot_id")
    private Long timeslotId;

    @Column(name = "local_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime localTime;

    @Column(name = "is_available")
    private Boolean isAvailable;

    @Column(name = "current_slot_capacity")
    @JsonIgnore
    private Integer currentSlotCapacity = 0;

    @Column(name = "max_slot_capacity")
    private Integer maxSlotCapacity;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp created;

    @Column
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Timestamp changed;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "calendar_day_id")
    @JsonBackReference
    private CalendarDay calendarDay;
}