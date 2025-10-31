package com.app.canteen.repo;

import com.app.canteen.model.CollegeUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CollegeUserRepo extends MongoRepository<CollegeUser, String> {
  Optional<CollegeUser> findByCollegeId(String collegeId);
}
