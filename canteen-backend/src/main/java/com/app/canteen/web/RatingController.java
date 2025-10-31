package com.app.canteen.web;

import com.app.canteen.model.MenuItem;
import com.app.canteen.model.Rating;
import com.app.canteen.repo.MenuItemRepo;
import com.app.canteen.repo.RatingRepo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:8080","http://localhost:3000"})
public class RatingController {

  private final RatingRepo ratingRepo;
  private final MenuItemRepo menuRepo;

  public RatingController(RatingRepo ratingRepo, MenuItemRepo menuRepo) {
    this.ratingRepo = ratingRepo;
    this.menuRepo = menuRepo;
  }

  // NOTE: userId is NOT taken from client anymore. We read the logged-in user from request/security context.
  public record RatingIn(
      @NotBlank String menuItemId,
      @Min(1) @Max(5) int stars,
      @Size(max = 280) String comment
  ) {}

  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody RatingIn in) {
    // 1) Require login
    String currentUser = resolveCurrentUser();
    if (currentUser == null || currentUser.isBlank()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("message", "Login required"));
    }

    // 2) Ensure menu exists
    Optional<MenuItem> menu = menuRepo.findById(in.menuItemId());
    if (menu.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("message", "Menu item not found"));
    }

    // 3) Block duplicate user rating for the same menu item
    if (ratingRepo.findByMenuItemIdAndUserId(in.menuItemId(), currentUser).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(Map.of("message", "You already rated this dish."));
    }

    // 4) Save rating
    Rating r = new Rating();
    r.setMenuItemId(in.menuItemId());
    r.setUserId(currentUser);
    r.setStars(in.stars());
    r.setComment(in.comment());

    Rating saved = ratingRepo.save(r);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @GetMapping("/menu/{menuItemId}")
  public ResponseEntity<List<Rating>> listByMenu(@PathVariable String menuItemId) {
    if (menuRepo.findById(menuItemId).isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.ok(ratingRepo.findByMenuItemId(menuItemId));
  }
  
  private String resolveCurrentUser() {
    // Try request attribute first (JwtFilter puts it there)
    var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attrs != null) {
      Object v = attrs.getRequest().getAttribute("collegeId");
      if (v instanceof String s && !s.isBlank()) {
        return s;
      }
    }

    // Fallback: Spring Security
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()) {
      Object principal = auth.getPrincipal();
      if (principal instanceof org.springframework.security.core.userdetails.User u) {
        return u.getUsername();
      }
      if (principal instanceof String s && !s.isBlank()) {
        return s;
      }
    }
    return null;
  }
}
