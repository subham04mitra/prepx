package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.DailyQs;

public interface DailyQsRepositry extends MongoRepository<DailyQs, String> {

	@Aggregation(pipeline = { "{ $match: { lang: ?0 } }", "{ $sample: { size: 1 } }" })
	List<DailyQs> findRandomOneByLang(String lang);

	Optional<DailyQs> findByQsId(long qsId);

}
