package com.example.backendspring.dao;

import com.example.backendspring.model.DeepClone;
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

  /**
   * Немного магии с Reflection
   * @param attribute
   * @param attributeName
   * @return
   */
  private Optional<SecureUser> findByAttributeIndex(String attribute, String attributeName) {
    Optional<SecureUser> secureUserOptional = db.values()
        .stream()
        .filter((sUser) -> {
          try {
            Field declaredField = sUser.getClass().getDeclaredField(attributeName);
            declaredField.setAccessible(true);
            boolean found = declaredField.get(sUser).equals(attribute);
            if (found) {
              return true;
            }
          } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("ERROR: Не верное поле " + attributeName + " " + attribute);
          }
          return false;
        })
        .findFirst();
    return secureUserOptional
        .map(DeepClone::deepClone);
  }
}
