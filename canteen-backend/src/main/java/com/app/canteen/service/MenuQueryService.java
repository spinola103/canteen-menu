package com.app.canteen.service;

import com.app.canteen.model.MenuItem;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MenuQueryService {

  private final MongoTemplate mongo;

  public MenuQueryService(MongoTemplate mongo) {
    this.mongo = mongo;
  }

  public Page<MenuItem> find(
      LocalDate date,
      String mealType,  // normalized to BREAKFAST/LUNCH/DINNER or null
      Boolean veg,      // null = any
      String q,         // search in name/description/tags
      Pageable pageable
  ) {
    var criteriaList = new ArrayList<Criteria>();
    criteriaList.add(Criteria.where("date").is(date));

    if (mealType != null) {
      criteriaList.add(Criteria.where("mealType").is(mealType));
    }
    if (veg != null) {
      criteriaList.add(Criteria.where("veg").is(veg));
    }
    if (q != null && !q.isBlank()) {
      var regex = "(?i).*" + Pattern.quote(q.trim()) + ".*";
      criteriaList.add(new Criteria().orOperator(
          Criteria.where("name").regex(regex),
          Criteria.where("description").regex(regex),
          Criteria.where("tags").regex(regex)
      ));
    }

    Query query = new Query(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));

    long total = mongo.count(query, MenuItem.class);
    if (pageable.getSort().isUnsorted()) {
      query.with(Sort.by(Sort.Direction.ASC, "name"));
    } else {
      query.with(pageable.getSort());
    }
    query.with(pageable);

    List<MenuItem> content = mongo.find(query, MenuItem.class);
    return new PageImpl<>(content, pageable, total);
  }
}
