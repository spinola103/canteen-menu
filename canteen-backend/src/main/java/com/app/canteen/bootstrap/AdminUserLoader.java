package com.app.canteen.bootstrap;

import com.app.canteen.model.CollegeUser;
import com.app.canteen.model.Role;
import com.app.canteen.repo.CollegeUserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class AdminUserLoader implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(AdminUserLoader.class);

  private final CollegeUserRepo userRepo;

  @Value("${app.admin.collegeId:admin}")
  private String adminCollegeId;

  @Value("${app.admin.fullName:Administrator}")
  private String adminFullName;

  @Value("${app.admin.password:admin123}")
  private String adminPassword;

  public AdminUserLoader(CollegeUserRepo userRepo) {
    this.userRepo = userRepo;
  }

  @Override
  public void run(String... args) {
    userRepo.findByCollegeId(adminCollegeId).ifPresentOrElse(
        u -> log.info("Admin user already exists: {}", u.getCollegeId()),
        () -> {
          CollegeUser admin = new CollegeUser();
          admin.setCollegeId(adminCollegeId);
          admin.setFullName(adminFullName);
          admin.setPasswordHash(adminPassword); // plain for now; hash later if needed
          admin.setRole(Role.ADMIN);
          userRepo.save(admin);
          log.info("Created default ADMIN user: {}", adminCollegeId);
        }
    );
  }
}
