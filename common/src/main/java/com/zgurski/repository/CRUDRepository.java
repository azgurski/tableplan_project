package com.zgurski.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<K,T> {

    Optional<T> findOne(K id);

    List<T> findAll();

    Page<T> findAllPageable(Pageable pageable);

    T create(T object);

    T update(T object);

    Long deleteSoft(K id);
}