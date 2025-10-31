package com.app.canteen.web;

import com.app.canteen.model.CollegeUser;
import com.app.canteen.model.Role;
import com.app.canteen.repo.CollegeUserRepo;
import com.app.canteen.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"http://localhost:5173", "http://localhost:8080", "http://localhost:3000"},
    allowCredentials = "true"
)
public class AuthController {

  private final CollegeUserRepo userRepo;
  private final JwtService jwtService;

  public AuthController(CollegeUserRepo userRepo, JwtService jwtService) {
    this.userRepo = userRepo;
    this.jwtService = jwtService;
  }

  // ---------- DTOs ----------

  public record LoginRequest(
      @NotBlank String collegeId,
      @NotBlank String password
  ) {}

  public record RegisterRequest(
      @NotBlank String collegeId,
      @NotBlank String fullName,
      @NotBlank String password,   // DOB as password for now
      String role                  // "ADMIN" or "STUDENT" (defaults to STUDENT)
  ) {}

  // ---------- Endpoints ----------

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest in) {
    Optional<CollegeUser> opt = userRepo.findByCollegeId(in.collegeId());
    if (opt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "User not found"));
    }

    CollegeUser user = opt.get();

    // Plain compare (change to password encoder later)
    if (!user.getPasswordHash().equals(in.password())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Invalid credentials"));
    }

    String token = jwtService.generateToken(user.getCollegeId(), user.getRole());
    return ResponseEntity.ok(Map.of(
        "token", token,
        "role",  user.getRole().name(),
        "name",  user.getFullName()
    ));
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
    if (userRepo.findByCollegeId(req.collegeId()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("message", "User already exists"));
    }

    CollegeUser u = new CollegeUser();
    u.setCollegeId(req.collegeId());
    u.setFullName(req.fullName());
    u.setPasswordHash(req.password()); // Store DOB-as-password for now

    // ✅ Convert String → Enum safely
    Role roleEnum;
    try {
      roleEnum = (req.role() == null || req.role().isBlank())
          ? Role.USER // default
          : Role.valueOf(req.role().trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("message", "Invalid role. Must be ADMIN or STUDENT."));
    }

    u.setRole(roleEnum);

    CollegeUser saved = userRepo.save(u);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
        "collegeId", saved.getCollegeId(),
        "fullName",  saved.getFullName(),
        "role",      saved.getRole().name()
    ));
  }
}
