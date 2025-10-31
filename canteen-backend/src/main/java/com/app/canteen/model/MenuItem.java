package com.app.canteen.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Document(collection = "menu_items")
@CompoundIndexes({
  @CompoundIndex(name = "unique_date_meal_name", def = "{'date':1,'mealType':1,'name':1}", unique = true)
})
public class MenuItem {
  @Id
  private String id;

  @Indexed
  private LocalDate date;

  // BREAKFAST / LUNCH / DINNER
  @Indexed
  private String mealType;

  @Indexed
  private String name;

  private String description;

  @Indexed
  private boolean veg = true;

  private int price = 0;

  // optional: tags to enhance filtering/search
  private List<String> tags;

  private Instant createdAt = Instant.now();

  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }

  public String getMealType() { return mealType; }
  public void setMealType(String mealType) { this.mealType = mealType; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public boolean isVeg() { return veg; }
  public void setVeg(boolean veg) { this.veg = veg; }

  public int getPrice() { return price; }
  public void setPrice(int price) { this.price = price; }

  public List<String> getTags() { return tags; }
  public void setTags(List<String> tags) { this.tags = tags; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
