# Examples

https://github.com/MrDexII/spotify-api-integration/blob/ad9e3d448606c281881386b9a324757aa64fadb7/src/main/java/com/andrzej/spotifyapi/controller/HomeController.java
https://github.com/dipankardutta1/Oauth2Client/blob/32544ca79413a76a25d95ab193a6ddb664938bc2/ClientApp2/src/main/java/com/example/demo/config/UiSecurityConfig.java
https://github.com/kruti31/AppId/blob/4ef4ea3182493df9987fd62e80da25508adb6ec1/appid/src/main/java/com/example/appid/SecurityConfiguration.java
https://github.com/chenrujun/spring-security-oauth2-client-sample/blob/843e2d3ee3fce8750cfaefb4e4993d8118e96667/src/main/resources/application.yml
https://github.com/j551234/oauth2_prototype/blob/5f33b88503df6d434c56648bfb4c1d24d5aa7331/src/main/java/com/example/api/OAuth2Controller.java
https://github.com/Dendrocopos-kr/ModumE-Spring-boot/blob/91336844f5cc3fe56a0f6c813c92dc072b0c4fd7/src/main/java/com/amolrang/modume/api/CallApi.java
https://github.com/Kehrlann/spring-security-improve-oauth2login-login-authentication-filter/blob/7bde7ebf556a04a50f4e24c7ac05be675faa7da2/src/main/java/wf/garnier/oauth2example/SecurityConfiguration.java
https://github.com/sage1500/study-web-db/blob/32af66cdd9657ee8e2b6406d6c482061741777e6/demo-common/src/main/resources/application-h2.yml

## Testing

https://github.com/Saljack/spring-security-9477/blob/fa87deb834cfd30945c5870b4ca4ae37ef2617cc/src/test/java/com/saljack/springsecurity9477/SpringSecurity9477ApplicationTests.java

## Resources

https://leaks.wanari.com/2017/11/28/how-to-make-custom-usernamepasswordauthenticationfilter-with-spring-security

## Spring Security OAuth2 Login Flow (redirect-uri is /login/oauth2/code/{registrationId})

![Spring Security OAuth2 Login Flow](../docs/Spring%20Security%20OAuth2%20Login%20Flow.svg)

### Entry-point

The flow starts opening the following url in browser `/oauth2/authorization/{registrationId}`.

This is handeled in [`OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestRedirectFilter.java#L158)

The filter using [`OAuth2AuthorizationRequestResolver.resolve(HttpServletRequest request)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestResolver.java#L44)

Default implementation is [`DefaultOAuth2AuthorizationRequestResolver`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/DefaultOAuth2AuthorizationRequestResolver.java#L66)

### Redirection to the Authorization Server's Authorization Endpoint

The resolved matches the `/oauth2/authorization/{registrationId}` pattern and creates [`OAuth2AuthorizationRequest`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-core/src/main/java/org/springframework/security/oauth2/core/endpoint/OAuth2AuthorizationRequest.java#L54) responsible for creating the authorization request url for redirection.

Then redirection is made to the Authorization Server's Authorization Endpoint

### Redirection back to client after End-User (Resource Owner) has granted access

[`AbstractAuthenticationProcessingFilter.doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`](https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/authentication/AbstractAuthenticationProcessingFilter.java#L219) which calls [`OAuth2LoginAuthenticationFilter.attemptAuthentication(HttpServletRequest request, HttpServletResponse response)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2LoginAuthenticationFilter.java#L162)

[`OAuth2AuthorizationResponse`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-core/src/main/java/org/springframework/security/oauth2/core/endpoint/OAuth2AuthorizationResponse.java#L34) is created containing
- `redirectUri`
- `state`
- `code`
- `error`

Based on this [`OAuth2LoginAuthenticationToken`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/authentication/OAuth2LoginAuthenticationToken.java#L47) is created containing
- `OAuth2User principal`
- `ClientRegistration clientRegistration`
- `OAuth2AuthorizationExchange authorizationExchange` which contains authorization request and response
- `OAuth2AccessToken accessToken` is `null` yet
- `OAuth2RefreshToken refreshToken` is `null` yet


Then [`AuthenticationManager.authenticate(Authentication authentication)`](https://github.com/spring-projects/spring-security/blob/main/core/src/main/java/org/springframework/security/authentication/AuthenticationManager.java#L53) is  called with `OAuth2LoginAuthenticationToken`.

One of the following providers are called
- `AnonymousAuthenticationProvider` not called
- `OAuth2LoginAuthenticationProvider` calls [`OAuth2AuthorizationCodeAuthenticationProvider.authenticate(Authentication authentication)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/authentication/OAuth2AuthorizationCodeAuthenticationProvider.java#L72)
- `OidcAuthorizationCodeAuthenticationProvider` not called

### Requesting access token

`OAuth2AuthorizationCodeAuthenticationProvider` is checking first if the `state` matches in `authorizationResponse` and `authorizationRequest`.

Then using [`OAuth2AccessTokenResponseClient.getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/endpoint/OAuth2AccessTokenResponseClient.java#L56) requests access token from Authorization Server.

Default implementation is [`DefaultAuthorizationCodeTokenResponseClient`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/endpoint/DefaultAuthorizationCodeTokenResponseClient.java#L74)

### Handling succesful authentication

After succesful authentication using `AuthenticationManager` `OAuth2LoginAuthenticationFilter` creates [`OAuth2AuthorizedClient`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizedClient.java#L44).

`OAuth2AuthorizedClient` is saved using [`OAuth2AuthorizedClientRepository.saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizedClientRepository.java#L68).

Also `OAuth2LoginAuthenticationFilter` creates an [`OAuth2AuthenticationToken`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/authentication/OAuth2AuthenticationToken.java#L44) containing
- `OAuth2User principal`
- `String authorizedClientRegistrationId`

Later `AbstractAuthenticationProcessingFilter` sets the new `OAuth2AuthenticationToken` in `SecurityContextHolder`.

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authResult);
SecurityContextHolder.setContext(context);
```

As last step it calls [`SavedRequestAwareAuthenticationSuccessHandler.onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)`](https://github.com/spring-projects/spring-security/blob/main/web/src/main/java/org/springframework/security/web/authentication/SavedRequestAwareAuthenticationSuccessHandler.java#L73) which does a redirection to original request or to predefined `defaultSuccessUrl` configured in `SecurityFilterChain` `@Bean`. 

```java
http.oauth2Login()
  .defaultSuccessUrl(webConfig.getPublicAppUrl());
```

## Spring Security OAuth2 Client Flow (redirect-uri is /authorize/oauth2/code/{registrationId})

![Spring Security OAuth2 Client Flow](../docs/Spring%20Security%20OAuth2%20Client%20Flow.svg)

### Entry-point

[`RegisteredOAuth2AuthorizedClient`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/annotation/RegisteredOAuth2AuthorizedClient.java#L51) annotation.

```java
  @Controller
  public class MyController {
      @GetMapping("/authorized-client")
      public String authorizedClient(@RegisteredOAuth2AuthorizedClient("login-client") OAuth2AuthorizedClient authorizedClient) {
          // do something with authorizedClient
      }
  }
```
### AuthorizeRequest -> Manager.authorize

`RegisteredOAuth2AuthorizedClient` triggers [`OAuth2AuthorizedClientArgumentResolver.resolveArgument`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/method/annotation/OAuth2AuthorizedClientArgumentResolver.java#L107).

It creates [`OAuth2AuthorizeRequest`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizeRequest.java#L42) containing:
- `clientRegistrationId`
- `OAuth2AuthorizedClient` (is `null` in this case)
- `Authentication principal` (`SecurityContextHolder.getContext().getAuthentication()`)
- `HttpServletRequest`
- `HttpServletResponse`

And passes it to [`OAuth2AuthorizedClientManager.authorize(OAuth2AuthorizeRequest authorizeRequest)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizedClientManager.java#L66)

Default implementation is [`DefaultOAuth2AuthorizedClientManager`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/DefaultOAuth2AuthorizedClientManager.java#L138)

### Manager.authorize -> Provider(s).authorize

The manager is buidling the [`OAuth2AuthorizationContext`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizationContext.java#L40) containing:
- `ClientRegistration`
- `OAuth2AuthorizedClient` is loaded from [`OAuth2AuthorizedClientRepository.loadAuthorizedClient(String clientRegistrationId, Authentication principal,
			HttpServletRequest request)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizedClientRepository.java#L57)
- `Authentication principal`
- `HttpServletRequest`
- `HttpServletResponse`

If the client is not authorized yet it calls the chain of [`OAuth2AuthorizedClientProvider.authorize(OAuth2AuthorizationContext context)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizedClientProvider.java#L50)

The chain can contain the following providers:
- [`AuthorizationCodeOAuth2AuthorizedClientProvider`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/AuthorizationCodeOAuth2AuthorizedClientProvider.java#L33). This throws [`ClientAuthorizationRequiredException`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/ClientAuthorizationRequiredException.java#L29) if the client is not authorized.
- [`RefreshTokenOAuth2AuthorizedClientProvider`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/RefreshTokenOAuth2AuthorizedClientProvider.java#L76). This is responsible for requesting new access token using refresh token after access token has expired (or expires in one minute).

### Succesful client authorization

In case of success [`OAuth2AuthorizationSuccessHandler.onAuthorizationSuccess(OAuth2AuthorizedClient authorizedClient, Authentication principal, Map<String, Object> attributes)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizationSuccessHandler.java#L47) is called.

In default case this calls [`OAuth2AuthorizedClientRepository.saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizedClientRepository.java#L68)

As last step the authorized client is injected to parameter annotated with `@RegisteredOAuth2AuthorizedClient`.

### Failed client authorization

In case [`OAuth2AuthorizationException`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-core/src/main/java/org/springframework/security/oauth2/core/OAuth2AuthorizationException.java#L27) is thrown [`OAuth2AuthorizationFailureHandler.onAuthorizationFailure(OAuth2AuthorizationException  authorizationException, Authentication principal, Map<String, Object> attributes)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizationFailureHandler.java#L47) is called.

In default case this calls [`OAuth2AuthorizedClientRepository.removeAuthorizedClient(String clientRegistrationId, Authentication principal, HttpServletRequest request, HttpServletResponse response)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizedClientRepository.java#L79)

The exception is rethrown and catched in [`OAuth2AuthorizationRequestRedirectFilter.doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestRedirectFilter.java#L158)

### Redirection to the Authorization Server's Authorization Endpoint

In case of `ClientAuthorizationRequiredException` `OAuth2AuthorizationRequestRedirectFilter` calls [`OAuth2AuthorizationRequestResolver.resolve(HttpServletRequest request, String clientRegistrationId)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationRequestResolver.java#L54).

It creates [`OAuth2AuthorizationRequest`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-core/src/main/java/org/springframework/security/oauth2/core/endpoint/OAuth2AuthorizationRequest.java#L54) responsible for creating the authorization request url for redirection.

As last step the redirection is made.

### Redirection back to client after End-User (Resource Owner) has granted access

[`OAuth2AuthorizationCodeGrantFilter.doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizationCodeGrantFilter.java#L162) matches if the incomming request is an authorization request and if it exactly matches the request previously added to [`AuthorizationRequestRepository`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/AuthorizationRequestRepository.java#L40)

In case of match the process continues in similar way as the login flow until succesful authentication.

### Handling succesful authentication

After succesful authentication using `AuthenticationManager` `OAuth2AuthorizationCodeGrantFilter` creates [`OAuth2AuthorizedClient`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/OAuth2AuthorizedClient.java#L44).

`OAuth2AuthorizedClient` is saved using [`OAuth2AuthorizedClientRepository.saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal, HttpServletRequest request, HttpServletResponse response)`](https://github.com/spring-projects/spring-security/blob/main/oauth2/oauth2-client/src/main/java/org/springframework/security/oauth2/client/web/OAuth2AuthorizedClientRepository.java#L68).

As last step it redirects back to previous url.

### Resources:
- https://developer.withings.com/api-reference/
- https://developers.strava.com/docs/authentication/
- https://developers.strava.com/docs/reference/

# Kubetools version: 31
