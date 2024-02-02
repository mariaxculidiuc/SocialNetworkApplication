package com.example.demo.repository.paging;

import com.example.demo.domain.Entity;
import com.example.demo.repository.Repository;

public interface PagingRepository<ID ,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator
}