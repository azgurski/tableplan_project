package com.zgurski.repository;

import com.zgurski.domain.hibernate.DefaultTime;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

public interface DefaultTimeRepository extends JpaRepository<DefaultTime, Long>,
        PagingAndSortingRepository<DefaultTime, Long>, CrudRepository<DefaultTime, Long> {

    @Cacheable(value = "All default times") //TODO cacheable над query или сервисом?
    @Query(value = "select dt from DefaultTime dt order by dt.defaultTimeId")
    List<DefaultTime> findAllTimes();
}