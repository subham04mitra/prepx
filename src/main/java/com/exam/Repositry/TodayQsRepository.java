package com.exam.Repositry;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.TodayQs;

public interface TodayQsRepository extends MongoRepository<TodayQs, String> {

	List<TodayQs> findByDateBetween(Instant start, Instant end);
}
