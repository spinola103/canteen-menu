package com.app.canteen.web.dto;

import java.time.LocalDate;
import java.util.List;

public record MenuItemOut(
    String id,
    LocalDate date,
    String mealType,
    String name,
    String description,
    boolean veg,
    int price,
    double avgStars,
    int count,
    List<String> tags
) {}
