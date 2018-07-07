package com.example.backendspring.service;

import com.example.backendspring.config.AppProperties;
import com.example.backendspring.dao.SecureUserDao;
import com.example.backendspring.exception.RequestException;
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

  public AuthUser authRequest(UserCredentials userCredentials) {
    return secureUserDao.findByUsername(userCredentials.getEmail())
        .map(username -> {
          // используем конкретную сигму пользователя
          String data = username.getEmail() + appProperties.getDomain() + username.getSigma();
          String salt = SecureUtils.digest(data);
          return AuthUser.authRequest(salt, appProperties.getCost(), username.getMisc());
        })
        .orElseGet(() -> {
          // используем системную сигму
          String data = userCredentials.getEmail() + appProperties.getDomain() + appProperties.getSysSigma();
          String salt = SecureUtils.digest(data);
          return AuthUser.authRequest(salt, appProperties.getCost(), appProperties.getMisc());
        });
  }

  public AuthUser register(UserCredentials userCredentials) {
    String username = userCredentials.getEmail();
    boolean duplicateName = secureUserDao.findByUsername(username).isPresent();
    if (duplicateName) {
      throw RequestException.forbidden();
    }
    SecureUser secureUser = new SecureUser();
    secureUser.setId(Utils.getRandomUUID());
    secureUser.setEmail(username);

    secureUser.setTokenLength(appProperties.getTokenLength());
    // устанавливаем сигму, которая будет использоваться вместо системного значения
    secureUser.setSigma(Utils.getRandomString32());
    secureUser.setCost(appProperties.getCost());
    secureUser.setMisc(appProperties.getMisc());

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
    return AuthUser.simpleUser(secureUser.getId(), username, accessToken.accessToken, userSession,
        secureUser.getAuthorities());
  }

  public AuthUser postRegister(UserCredentials userCredentials) {
    String username = userCredentials.getEmail();
    return secureUserDao.findByUsername(username)
        .map(secureUser -> {
          String passwordHash = userCredentials.getPasswordHash();
          passwordHash = SecureUtils.digest(passwordHash);
          secureUser.setPasswordHash(passwordHash);

          // encrypt random token
          TokenPair accessToken = getAccessToken(secureUser);

          // save encrypted token and userSession
          secureUser.setSecureToken(accessToken.secureToken);
          secureUser.setAccessToken(accessToken.accessToken);
          secureUserDao.save(secureUser);
          // send access token and userSession
          return AuthUser.simpleUser(secureUser.getId(), username, accessToken.accessToken, secureUser.getUserSession(),
              secureUser.getAuthorities());
        })
        .orElseThrow(RequestException::forbidden);
  }

  public AuthUser authorize(UserCredentials userCredentials) {
    String username = userCredentials.getEmail();
    return secureUserDao.findByUsername(username)
        .map(secureUser -> {
          try {
            // hash credentials
            String passwordHash = userCredentials.getPasswordHash();
            passwordHash = SecureUtils.digest(passwordHash);

            if (passwordHash.equals(secureUser.getPasswordHash())) {
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
              AuthUser authUser = AuthUser.simpleUser(userId, "", accessToken.accessToken, userSession, authorities);
              logger.info("AUTHORIZED: " + authUser);
              return authUser;
            }
          } catch (Exception e) {
            logger.error("UNAUTHORIZED: " + userCredentials, e.getMessage());
          }
          return null;
        })
        .orElseThrow(RequestException::forbidden);
  }

  public AuthUser authenticate(AuthUser authUser) {
    if (authUser.getAuthorities().contains(EnumAuthority.ANONYMOUS)) {
      throw RequestException.forbidden();
    }
    String session = authUser.getUserSession();
    String accessToken = authUser.getAccessToken();
    Optional<SecureUser> secureUserOptional = secureUserDao.findBySession(session);
    return secureUserOptional
        .map((secureUser) -> {
          boolean isAuth = isAuthed(accessToken, secureUser);
          if (isAuth) {
            TokenPair updatedAccessToken = getAccessToken(secureUser);
            secureUser.setAccessToken(updatedAccessToken.accessToken);
            secureUser.setSecureToken(updatedAccessToken.secureToken);
            secureUserDao.save(secureUser);

            authUser.setAccessToken(updatedAccessToken.accessToken);
            authUser.setEmail(secureUser.getEmail());
            authUser.setUserId(secureUser.getId());
            authUser.setAuthorities(secureUser.getAuthorities());
            logger.info("AUTHENTICATED: " + authUser);
            return authUser;
          }
          logger.info("Unsuccessful authentication attempt " + authUser);
          return null;
        })
        .orElseThrow(RequestException::forbidden);
  }

  public AuthUser logout(AuthUser authUser) {
    String session = authUser.getUserSession();
    String accessToken = authUser.getAccessToken();
    Optional<SecureUser> secureUserOptional = secureUserDao.findBySession(session);
    return secureUserOptional
        .map((secureUser) -> {
          boolean isAuth = isAuthed(accessToken, secureUser);
          if (isAuth) {
            secureUser.setSecureToken("");
            secureUser.setAccessToken("");
            secureUser.setUserSession("");
            secureUserDao.save(secureUser);
          }
          return AuthUser.anonymous();
        })
        .orElseThrow(RequestException::forbidden);
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

  private void hashCredentials(UserCredentials userCredentials, SecureUser secureUser) {
    String credentials = userCredentials.getCredentials();
    String salt = getRandomString20();
    secureUser.setSalt(salt);
    String digest = SecureUtils.digest(credentials + salt);
//    secureUser.setDigest(digest);
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
