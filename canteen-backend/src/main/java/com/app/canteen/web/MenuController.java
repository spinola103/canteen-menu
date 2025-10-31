package com.app.canteen.web;

import com.app.canteen.model.Rating;
import com.app.canteen.model.MenuItem;
import com.app.canteen.repo.MenuItemRepo;
import com.app.canteen.repo.RatingRepo;
import com.app.canteen.service.MenuQueryService;
import com.app.canteen.web.dto.MenuItemOut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080", "http://localhost:3000"})
public class MenuController {

  private final MenuItemRepo menuRepo;
  private final RatingRepo ratingRepo;
  private final MenuQueryService menuQuery;

  public MenuController(MenuItemRepo menuRepo, RatingRepo ratingRepo, MenuQueryService menuQuery) {
    this.menuRepo = menuRepo;
    this.ratingRepo = ratingRepo;
    this.menuQuery = menuQuery;
  }

  public record MenuItemIn(
      @NotNull LocalDate date,
      @NotBlank String mealType,
      @NotBlank String name,
      String description,
      boolean veg,
      @Min(0) int price,
      List<String> tags
  ) {}

  // ✅ Create Menu Item
  @PostMapping
  public ResponseEntity<?> create(@Valid @RequestBody MenuItemIn in) {
    MenuItem m = new MenuItem();
    m.setDate(in.date());
    m.setMealType(normalizeMealType(in.mealType()));
    m.setName(in.name().trim());
    m.setDescription(in.description());
    m.setVeg(in.veg());
    m.setPrice(in.price());
    m.setTags(in.tags() == null ? List.of() : in.tags());
    var saved = menuRepo.save(m);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  // ✅ Get All Menu Items
  @GetMapping
  public Page<MenuItemOut> list(
      @RequestParam(required = false) LocalDate date,
      @RequestParam(required = false) String mealType,
      @RequestParam(required = false) Boolean veg,
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "8") int size,
      @RequestParam(defaultValue = "name,asc") String sort
  ) {
    LocalDate targetDate = (date != null ? date : LocalDate.now());
    String normalizedMeal = mealType == null || mealType.isBlank() ? null : normalizeMealType(mealType);

    Sort sortObj = parseSort(sort);
    Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sortObj);

    Page<MenuItem> raw = menuQuery.find(targetDate, normalizedMeal, veg, q, pageable);

    List<MenuItemOut> content = raw.getContent().stream().map(mi -> {
      var rs = ratingRepo.findByMenuItemId(mi.getId());
      double avg = rs.stream().mapToInt(Rating::getStars).average().orElse(0.0);
      double rounded = Math.round(avg * 10.0) / 10.0;
      return new MenuItemOut(
          mi.getId(), mi.getDate(), mi.getMealType(), mi.getName(), mi.getDescription(),
          mi.isVeg(), mi.getPrice(), rounded, rs.size(), mi.getTags()
      );
    }).collect(Collectors.toList());

    return new PageImpl<>(content, pageable, raw.getTotalElements());
  }

  // ✅ Get Top Rated Menu Items
  @GetMapping("/top")
  public List<MenuItemOut> top(
      @RequestParam(required = false) LocalDate date,
      @RequestParam(defaultValue = "5") @Min(1) @Max(50) int limit
  ) {
    var page = list(date, null, null, null, 0, 100, "name,asc");
    return page.getContent().stream()
        .sorted(Comparator.comparing(MenuItemOut::avgStars).reversed().thenComparing(MenuItemOut::price))
        .limit(limit).toList();
  }

  // ✅ Update Menu Item
  @PutMapping("/{id}")
  public ResponseEntity<?> update(
      @PathVariable String id,
      @RequestBody Map<String, Object> updates
  ) {
    return menuRepo.findById(id)
        .map(menu -> {
          if (updates.containsKey("name"))
            menu.setName((String) updates.get("name"));
          if (updates.containsKey("description"))
            menu.setDescription((String) updates.get("description"));
          if (updates.containsKey("mealType"))
            menu.setMealType(((String) updates.get("mealType")).toUpperCase());
          if (updates.containsKey("price"))
            menu.setPrice((int) updates.get("price"));
          if (updates.containsKey("veg"))
            menu.setVeg((boolean) updates.get("veg"));
          if (updates.containsKey("tags"))
            menu.setTags((List<String>) updates.get("tags"));
          menuRepo.save(menu);
          return ResponseEntity.ok(Map.of("message", "Menu item updated successfully"));
        })
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item not found")));
  }

  // ✅ Delete Menu Item
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable String id) {
    if (!menuRepo.existsById(id)) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Item not found"));
    }
    menuRepo.deleteById(id);
    return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully"));
  }

  // 🔧 Helper methods
  private String normalizeMealType(String mt) {
    String v = (mt == null ? "" : mt.trim().toUpperCase());
    return switch (v) {
      case "BREAKFAST", "LUNCH", "DINNER" -> v;
      default -> "LUNCH";
    };
  }

  private Sort parseSort(String sort) {
    try {
      String[] p = sort.split(",");
      String field = (p.length > 0 ? p[0] : "name");
      Sort.Direction dir = (p.length > 1 && "desc".equalsIgnoreCase(p[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
      return Sort.by(dir, field);
    } catch (Exception e) {
      return Sort.by("name").ascending();
    }
  }
}
