package com.evaluacion.tvmaze.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.evaluacion.tvmaze.document.CachedShow;

public interface CachedShowRepository extends MongoRepository<CachedShow, Long> {
}
