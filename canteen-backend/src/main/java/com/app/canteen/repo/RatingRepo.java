package com.app.canteen.repo;

import com.app.canteen.model.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepo extends MongoRepository<Rating, String> {
  List<Rating> findByMenuItemId(String menuItemId);
  Optional<Rating> findByMenuItemIdAndUserId(String menuItemId, String userId);
}
