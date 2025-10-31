package com.app.canteen.repo;

import com.app.canteen.model.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface MenuItemRepo extends MongoRepository<MenuItem, String> {
  List<MenuItem> findByDate(LocalDate date);
  List<MenuItem> findByDateAndMealType(LocalDate date, String mealType);
  List<MenuItem> findByDateAndVeg(LocalDate date, boolean veg);
  List<MenuItem> findByDateAndMealTypeAndVeg(LocalDate date, String mealType, boolean veg);

  // simple text-like search by name/description (we'll implement “contains” in service/controller)
  List<MenuItem> findByDateAndNameRegexIgnoreCase(LocalDate date, String nameRegex);
}
