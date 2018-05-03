package com.example.backendspring.service;

import com.example.backendspring.config.AppProperties;
import com.example.backendspring.dao.SecureUserDao;
import com.example.backendspring.model.AuthUser;
import com.example.backendspring.model.EnumAuthority;
import com.example.backendspring.model.SecureUser;
import com.example.backendspring.model.UserCredentials;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Set;

import static com.example.backendspring.config.RequestConstants.SESSION_LENGTH;
import static com.example.backendspring.service.Utils.getRandomString20;

/**
 * Created by Aleksey Popryadukhin on 16/04/2018.
 */
@Service
public class SecureUserService {

  private Logger logger = LoggerFactory.getLogger(SecureUserService.class);
  private SecureUserDao secureUserDao;
  private AppProperties appProperties;

  public SecureUserService(SecureUserDao secureUserDao, AppProperties appProperties) {
    this.secureUserDao = secureUserDao;
    this.appProperties = appProperties;
  }

  public Optional<AuthUser> register(UserCredentials userCredentials) {
    try {
      String username = userCredentials.getUsername();
      boolean duplicateName = secureUserDao.findByUsername(username).isPresent();
      if (duplicateName) {
        return Optional.empty();
      }
      SecureUser secureUser = new SecureUser();
      secureUser.setId(Utils.getRandomUUID());
      secureUser.setUsername(username);

      secureUser.setTokenLength(appProperties.getTokenLength());

      // hash credentials
      hashCredentials(userCredentials, secureUser);

      // encrypt random token
      TokenPair accessToken = getAccessToken(secureUser);

      // save encrypted token and userSession
      String userSession = getUserSession();
      secureUser.setSecureToken(accessToken.secureToken);
      secureUser.setAccessToken(accessToken.accessToken);
      secureUser.setUserSession(userSession);
      secureUser.addAuthority(EnumAuthority.USER);
      secureUserDao.save(secureUser);

      // send access token and userSession
      AuthUser authUser = AuthUser.simpleUser(secureUser.getId(), username, accessToken.accessToken, userSession, secureUser.getAuthorities());
      return Optional.of(authUser);
    } catch (Exception e) {
      logger.error("UNREGISTERED: " + userCredentials, e.getMessage());
      return Optional.empty();
    }
  }

  public Optional<AuthUser> authorize(UserCredentials userCredentials) {
    String username = userCredentials.getUsername();
    return secureUserDao.findByUsername(username)
        .map(secureUser -> {
          try {
            // hash credentials
            String credentials = userCredentials.getCredentials();
            String salt = secureUser.getSalt();
            String clientDigest = SecureUtils.digest(credentials + salt);

            if (clientDigest.equals(secureUser.getDigest())) {
              // encrypt random token
              TokenPair accessToken = getAccessToken(secureUser);

              // save encrypted token and userSession
              String userSession = getUserSession();
              secureUser.setSecureToken(accessToken.secureToken);
              secureUser.setAccessToken(accessToken.accessToken);
              secureUser.setUserSession(userSession);
              secureUserDao.save(secureUser);

              // send access token and userSession
              String userId = secureUser.getId();
              Set<EnumAuthority> authorities = secureUser.getAuthorities();
              AuthUser authUser = AuthUser.simpleUser(userId, username, accessToken.accessToken, userSession, authorities);
              logger.info("AUTHORIZED: " + authUser);
              return authUser;
            }
          } catch (Exception e) {
            logger.error("UNAUTHORIZED: " + userCredentials, e.getMessage());
          }
          return null;
        });
  }

  public Optional<AuthUser> authenticate(AuthUser authUser) {
    if (authUser.getAuthorities().contains(EnumAuthority.ANONYMOUS)) {
      return Optional.empty();
    }
    String session = authUser.getUserSession();
    String accessToken = authUser.getAccessToken();
    Optional<SecureUser> secureUserOptional = secureUserDao.findBySession(session);
    return secureUserOptional.map((secureUser) -> {
      boolean isAuth = isAuthed(accessToken, secureUser);
      if (isAuth) {
        TokenPair updatedAccessToken = getAccessToken(secureUser);
        secureUser.setAccessToken(updatedAccessToken.accessToken);
        secureUser.setSecureToken(updatedAccessToken.secureToken);
        secureUserDao.save(secureUser);

        authUser.setAccessToken(updatedAccessToken.accessToken);
        authUser.setUsername(secureUser.getUsername());
        authUser.setUserId(secureUser.getId());
        authUser.setAuthorities(secureUser.getAuthorities());
        logger.info("AUTHENTICATED: " + authUser);
        return authUser;
      }
      logger.info("Unsuccessful authentication attempt " + authUser);
      return null;
    });
  }

  public Optional<AuthUser> logout(AuthUser authUser) {
    String session = authUser.getUserSession();
    String accessToken = authUser.getAccessToken();
    Optional<SecureUser> secureUserOptional = secureUserDao.findBySession(session);
    return secureUserOptional.map((secureUser) -> {
      boolean isAuth = isAuthed(accessToken, secureUser);
      if (isAuth) {
        secureUser.setSecureToken("");
        secureUser.setAccessToken("");
        secureUser.setUserSession("");
        secureUserDao.save(secureUser);
      }
      return AuthUser.anonymous();
    });
  }

  private boolean isAuthed(String accessToken, SecureUser secureUser) {
    String key = secureUser.getKey();
    String initVector = secureUser.getInitVector();
    try {
      String tokenDecrypted = SecureUtils.decrypt(key, initVector, accessToken);
      return secureUser.getSecureToken().equals(tokenDecrypted);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      logger.error(String.format("Unable to decrypt accessToken %s", accessToken));
      return false;
    }
  }

  private String getUserSession() {
    return Utils.getRandomString(SESSION_LENGTH);
  }

  private void hashCredentials(UserCredentials userCredentials, SecureUser secureUser) throws NoSuchAlgorithmException {
    String credentials = userCredentials.getCredentials();
    String salt = getRandomString20();
    secureUser.setSalt(salt);
    String digest = SecureUtils.digest(credentials + salt);
    secureUser.setDigest(digest);
  }

  /**
   * Set params for encryption generate secure token and encrypt it
   *
   * @param secureUser user
   * @return access and secure token
   */
  private TokenPair getAccessToken(SecureUser secureUser) {
    String secureToken = Utils.getRandomString(secureUser.getTokenLength());
    int encLength = 16;
    String initVector = Utils.getRandomString(encLength);
    String key = Utils.getRandomString(encLength);
    secureUser.setInitVector(initVector);
    secureUser.setKey(key);
    String accessToken = SecureUtils.encrypt(key, initVector, secureToken);
    logger.info("Emit new access token: " + accessToken);
    return new TokenPair(accessToken, secureToken);
  }

  private static class TokenPair {
    String accessToken;
    String secureToken;

    TokenPair(String accessToken, String secureToken) {
      this.accessToken = accessToken;
      this.secureToken = secureToken;
    }

    @Override
    public String toString() {
      return new ToStringBuilder(this)
          .append("accessToken", accessToken)
          .toString();
    }
  }
}
