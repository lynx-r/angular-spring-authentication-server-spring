## О проекте

Пример аутентификации без фреймворков на Angular и Spring (server Spring)

## Профили

В приложении есть два профиля:

* `dev` - для разработки
* `prod` - для продакшна.

Чтобы отправлять запросы на веб-клиент, нужно указать `ORIGIN_URL` - Origin веб-клиента на Angular.

[Веб-клиент на Angular](https://github.com/lynx-r/angular-spring-authentication-web-angular)

# Детали

# Часть 2. Сервер на Spring

## О чем эта статья

В этой статье, я расскажу как написать простую аутентификацию без помощи готовых решений для данной задачи. Она может быть полезна для новичков, которые хотят написать своё AAA (Authentication, Authorization, and Accounting). [Репозиторий клиента на Angular и ngrx](https://github.com/lynx-r/angular-spring-authentication-web-angular) и [Репозиторий сервера на Spring](https://github.com/lynx-r/angular-spring-authentication-server-spring).

В данной статье я сделаю выдержки кода серверной части на Spring.

<cut/>

# Сервер аутентификации на Spring

## Структура проекта

    .
    └── backendspring
        ├── BackendspringApplication.java           # Spring приложение
        ├── config
        │   ├── AppProperties.java
        │   ├── AuthAuthority.java                  # Описание доступов для контроллера авторизации пользователей
        │   ├── CorsFilterAdapter.java              # Описание CORS
        │   ├── ErrorMessages.java
        │   ├── IAuthority.java                     # Интерфейс для описания доступов
        │   ├── RequestConstants.java
        │   ├── DefendedAuthority.java              # Описание доступов для защищенного контроллера
        │   └── SecurityConfig.java                 # Конфигурация CORS через Spring Security
        ├── controller
        │   ├── AuthController.java                 # Контроллер аутентификации
        │   └── ProtectedPingPongController.java    # Защищенный контроллер
        ├── dao
        │   ├── BaseDao.java
        │   └── SecureUserDao.java                  # DAO для хранящегося в базе информации о пользователе
        ├── exception
        │   ├── AuthException.java
        │   └── PingPongException.java
        ├── function
        │   ├── BaseHandlerFunc.java                # Рутинные методы аутентификации для функционального интерфейса ModelHandlerFunc.java
        │   ├── TrustedHandlerFunc.java             # Функциональный интерфейс обрабатывающий проверенные запросы клиента
        │   └── SecureHandlerFunc.java              # Функциональный интерфейс выполняющий проверку запросов клиента
        ├── model
        │   ├── Answer.java                         # Структура данных - ответ сервера
        │   ├── AuthUser.java                       # Данные о пользователе используемые для его аутентификации
        │   ├── BaseDomain.java
        │   ├── EnumAuthority.java                  # Enum с описание доступов на сайт
        │   ├── MessagePayload.java
        │   ├── MessageResponse.java
        │   ├── Payload.java                        # Интерфейс с описание классов для сериализации/десериализации в JSON
        │   ├── PingPayload.java                    # Данные, которые получаем от клиента
        │   ├── PongPayload.java                    # Данные, которые возвращаем клиенту
        │   ├── UserCredentials.java                # Информация для регистрации/аутентификации пользователя
        │   └── SecureUser.java                     # Хранимая в БД информация о пользователе
        └── service
            ├── PingPongService.java                # Сервис, которые обрабатывает запрос клиента и возвращает данные ответа
            ├── SecureUserService.java              # ***Собственно, сервис в котором производится аутентификация/авторизация***
            └── SecureUtils.java                    # Сервис для шифрования данных пользователя
    
## Сервис аутентификации/авторизации/регистрации (SecureUserService)

`SecureUserService` главный сервис данной статьи - то, ради чего она задумывалась.

В нем реализованы следующие методы:

`public Optional<AuthUser> register(UserCredentials usercredentials)` - Регистрация пользователя;

`public Optional<AuthUser> authorize(UserCredentials usercredentials)` - Авторизация или Логин пользователя;

`public Optional<AuthUser> authenticate(AuthUser authUser)` - Аутентификация  или Проверка прав пользователя;

`public Optional<AuthUser> logout(AuthUser authUser)` - Выход или Удаление информации о том, что пользователь сейчас на сайте.

Приведу код авторизации пользователя:

```
// Берем хеш учетных данных пользователя
String credentials = usercredentials.getCredentials();
String salt = secureUser.getSalt();
String clientDigest = SecureUtils.digest(credentials + salt);

// Сверяем с теми что хранятся в базе данных
if (clientDigest.equals(secureUser.getDigest())) {
  // Получаем зашифрованный AccessToken и сохраняем ключи шифрования в SecureUser
  TokenPair accessToken = getAccessToken(secureUser);
  
  // Присваиваем пользователю сессию
  String userSession = getUserSession();
  // Сохраняем AccessToken и сессию
  secureUser.setSecureToken(accessToken.secureToken);
  secureUser.setAccessToken(accessToken.accessToken);
  secureUser.setUserSession(userSession);
  secureUserDao.save(secureUser);

  // Возвращем AccessToken, сессию и открытые данные пользователя клиенту
  String userId = secureUser.getId();
  Set<EnumAuthority> authorities = secureUser.getAuthorities();
  AuthUser authUser = AuthUser.simpleUser(userId, username, accessToken.accessToken, userSession, authorities);
  return authUser;
}
```

В общем, стандартный алгоритм.

Да, для получения AccessToken’а я не использую никаких данных пользователя. Просто генерирую случайную строку и шифрую её стандартными алгоритмами шифрования javax.crypto.

## Контроллер авторизации клиента (AuthController)

Для формирования ответа клиенту я использовал способ описанный раннее в [этой статье](https://habr.com/post/352732/)

В этом примере я сделал некоторые упрощения. Но, здесь, все так же используется функциональные интерфейсы из Java SE 8:

Приведу пример того, как я отвечаю на запрос клиента после его авторизации на сайте:

```
@PostMapping("authorize")
public @ResponseBody
Answer authorize(@RequestBody UserCredentials usercredentials, HttpServletResponse response) {
  // обрабатываем не авторизованные запрос на авторизацию
  return ((TrustedHandlerFunc<UserCredentials>) (data) ->
      secureUserService.authorize(data)
          .map(Answer::ok)
          .orElseGet(Answer::forbidden))
      .handleAuthRequest(response, usercredentials);
}
```

Для обработки не авторизованных запросов я использую функциональный интерфейс `TrustedHandlerFunc`. Он содержит в себе метод `Answer process(T data)`. Этот метод реализуется в контроллере и в нём выполняется вызов метода `SecureUserService::authorize`. Ответ этого сервиса склеивается с методом `Answer::ok` в случае успешной авторизации или метод `Answer::forbidden` в случае неудачной авторизации. Также, в интерфейсе есть метод по умолчанию `TrustedHandlerFunc::handleRequest` и `TrustedHandlerFunc::handleAuthRequest`, которые выбирают для метода `Answer process(T data)` данные. Здесь это `UserCredentials`. Нужно уточнить, что первый метод `handleRequest` предполагает наличие проверенного токена `AuthUser`, а второй, `handleAuthRequest`, нужен только для контроллера `AuthController`.

## Контроллер обработки запросов клиента (ProtectedPingPongController)

Рассмотри обработчик пользовательских запросов. Назовем его `PingPongService`. По условию, этот контроллер не должен быть доступен для не авторизованных клиентов.

Приведу пример создания ответа на запрос `ping`:

```
@PostMapping("ping")
public @ResponseBody
Answer ping(@RequestBody PingPayload ping, HttpServletRequest request, HttpServletResponse response) {
  return authenticateRequestService
      .getAuthenticatedUser(request, DefendedAuthority.PING)
      .map(authUser -> // получаем авторизованного пользователя
          ((TrustedHandlerFunc<PingPayload>) (data) ->
              pingPongService.getPong(data, authUser) // обрабатываем запрос пользователя в сервисе
                  .map(Answer::ok)
                  .orElseGet(Answer::forbidden)
          ).handleRequest(response, ping, authUser) // обрабатываем запрос
      ).orElseThrow(AuthException::forbidden);
}
```

Здесь используются два функциональных интерфейса: `SecureHandlerFunc` и `TrustedHandlerFunc`. Первый проверяет пользовательские заголовки, пришедшие с клиента, создаёт из них "token" `AuthUser` и передаёт их в следующий метод интерфейса  `TrustedHandlerFunc`. Здесь, ожидается, что "токен" - авторизованный пользователь.

Детали реализации этих интерфейсов я приводить не буду, так как они уже описаны в статье указанной ранее. Скажу лишь, что отличие только в разбиение обязанностей на авторизацию данных пришедших в заголовках и отправки результата клиенту.

# Не обошлось без Spring Security

Нужно отметить, что всё же пришлось подключить Spring Security для работы с CORS.

Для добавления необходимых заголовков был использован и немного переработан код с StackOverflow. Он находится в классах `CorsFilterAdapter` и `SecurityConfig`.

# Заключение

В этой статье мы рассмотрели как сделать простую аутентификацию "своими руками".

# Ссылки

* [Часть 1. Клиент на Angular](https://habr.com/post/354860/)
* [Spring](http://spring.io/)
* [Создание простого RESTful API с Spark Framework](https://habr.com/post/352732/)

## UPD

Добавлена ветка improved-security с хешированием паролей перед отправкой на сервер.
Подробнее: https://eprint.iacr.org/2015/387.pdf
