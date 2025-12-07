package com.exam.Repositry;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.exam.Entity.Leaderboard;

public interface LeaderboardRepository extends MongoRepository<Leaderboard, String>{

	
	Optional<Leaderboard> findByUuid(String uuid);
	
	List<Leaderboard> findAllByOrderByScoreDesc(Pageable pageable);

}
