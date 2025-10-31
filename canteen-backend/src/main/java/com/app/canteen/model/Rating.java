package com.app.canteen.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ratings")
@CompoundIndex(name = "unique_user_per_menuItem", def = "{'userId':1,'menuItemId':1}", unique = true)
public class Rating {
  @Id
  private String id;

  // reference by ID (no DBRef)
  private String menuItemId;

  private String userId;

  private int stars; // 1..5
  private String comment;

  private Instant createdAt = Instant.now();

  // getters/setters
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getMenuItemId() { return menuItemId; }
  public void setMenuItemId(String menuItemId) { this.menuItemId = menuItemId; }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public int getStars() { return stars; }
  public void setStars(int stars) { this.stars = stars; }

  public String getComment() { return comment; }
  public void setComment(String comment) { this.comment = comment; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
