package com.example.backendspring.dao;

import com.example.backendspring.model.BaseDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.String.format;

/**
 * В место БД, просто HashMap 😂🛴🗯
 * @param <T>
 */
public class BaseDao<T extends BaseDomain> {

  private Logger logger;

  Map<String, T> db = new HashMap<>();

  BaseDao(Class<T> clazz) {
    logger = LoggerFactory.getLogger(clazz);
  }

  public void save(final T entity) {
    if (entity == null) {
      logger.error("Entity is null");
      return;
    }
    if (db.containsKey(entity.getId())) {
      T oldEntity = db.get(entity.getId());
      db.replace(entity.getId(), oldEntity, entity);
    } else {
      db.put(entity.getId(), entity);
    }
  }

  public List<T> findAll(Integer limit) {
    logger.info(format("Find all with limit %s", limit));
    return new ArrayList<>(db.values());
  }

  public Optional<T> findById(String entityKey) {
    return Optional.of(db.get(entityKey));
  }
}
