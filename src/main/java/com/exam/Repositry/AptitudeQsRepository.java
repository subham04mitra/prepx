package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.AptitudeQs;
import com.exam.Entity.DailyQs;

public interface AptitudeQsRepository  extends MongoRepository<AptitudeQs, String>{

	@Aggregation(pipeline = {
		    "{ $match: { category: ?0 } }",
		    "{ $sample: { size: ?1 } }"
		})
		List<AptitudeQs> findRandomByCategory(String category, int size);

	Optional<AptitudeQs> findByQsId(long qsId);
}
