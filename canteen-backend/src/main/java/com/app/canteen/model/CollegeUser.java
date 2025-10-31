package com.app.canteen.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "college_users")
public class CollegeUser {
  @Id
  private String id;

  @Indexed(unique = true)
  private String collegeId;      // login username (e.g., college roll number)

  private String fullName;
  private Role role;             // ADMIN or STUDENT
  private String passwordHash;   // BCrypt hash

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }

  public String getCollegeId() { return collegeId; }
  public void setCollegeId(String collegeId) { this.collegeId = collegeId; }

  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
