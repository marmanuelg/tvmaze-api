package com.evaluacion.tvmaze.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.evaluacion.tvmaze.document.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
}
