package com.app.canteen.config;

import com.app.canteen.model.MenuItem;
import com.app.canteen.repo.MenuItemRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class SampleData {
  @Bean
  CommandLineRunner seed(MenuItemRepo menuRepo){
    return args -> {
      if (menuRepo.count() > 0) return;

      var today = LocalDate.now();

      MenuItem a = new MenuItem();
      a.setDate(today);
      a.setMealType("BREAKFAST");
      a.setName("Masala Dosa");
      a.setDescription("Crispy dosa with masala.");
      a.setVeg(true);
      a.setPrice(50);
      a.setTags(List.of("south", "spicy"));

      MenuItem b = new MenuItem();
      b.setDate(today);
      b.setMealType("LUNCH");
      b.setName("Chicken Biryani");
      b.setDescription("Aromatic basmati rice with chicken.");
      b.setVeg(false);
      b.setPrice(120);
      b.setTags(List.of("rice", "biryani"));

      MenuItem c = new MenuItem();
      c.setDate(today);
      c.setMealType("DINNER");
      c.setName("Paneer Butter Masala");
      c.setDescription("Creamy tomato gravy with paneer.");
      c.setVeg(true);
      c.setPrice(110);
      c.setTags(List.of("north", "rich"));

      menuRepo.saveAll(List.of(a, b, c));
    };
  }
}
