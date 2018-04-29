package com.example.backendspring.dao;

import com.example.backendspring.model.SecureUser;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Optional;

@Service
public class SecureUserDao extends BaseDao<SecureUser> {

  public SecureUserDao() {
    super(SecureUser.class);
  }

  public Optional<SecureUser> findBySession(String session) {
    return findByAttributeIndex(session, "userSession");
  }

  public Optional<SecureUser> findByUsername(String username) {
    return findByAttributeIndex(username, "username");
  }

  private Optional<SecureUser> findByAttributeIndex(String attribute, String attributeName) {
    return db.values().stream().map((sUser)-> {
      try {
        Field declaredField = sUser.getClass().getDeclaredField(attributeName);
        boolean found = declaredField.get(sUser).equals(attribute);
        if (found) {
          return sUser;
        }
      } catch (NoSuchFieldException | IllegalAccessException e) {
        System.out.println("ERROR: Пользователь не найден " + sUser);
      }
      return null;
    }).findFirst();
  }
}
