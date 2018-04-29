package com.example.backendspring.dao;

import com.example.backendspring.model.BaseDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;

public class BaseDao<T extends BaseDomain> {

  protected Logger logger;
  private final Class<T> clazz;

  protected Map<String, T> db = new HashMap<>();

  protected BaseDao(Class<T> clazz) {
    this.clazz = clazz;

    logger = LoggerFactory.getLogger(clazz);
    }

  public void save(final T entity) {
    if (entity == null) {
      logger.error("Entity is null");
      return;
    }
    db.put(entity.getId(), entity);
  }

  public List<T> findAll(Integer limit) {
    logger.info(format("Find all with limit %s", limit));
    return new ArrayList<>(db.values());
  }

  public Optional<T> findById(String entityKey) {
    return Optional.of(db.get(entityKey));
  }
}
