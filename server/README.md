# Examples

https://github.com/MrDexII/spotify-api-integration/blob/ad9e3d448606c281881386b9a324757aa64fadb7/src/main/java/com/andrzej/spotifyapi/controller/HomeController.java
https://github.com/dipankardutta1/Oauth2Client/blob/32544ca79413a76a25d95ab193a6ddb664938bc2/ClientApp2/src/main/java/com/example/demo/config/UiSecurityConfig.java
https://github.com/kruti31/AppId/blob/4ef4ea3182493df9987fd62e80da25508adb6ec1/appid/src/main/java/com/example/appid/SecurityConfiguration.java
https://github.com/chenrujun/spring-security-oauth2-client-sample/blob/843e2d3ee3fce8750cfaefb4e4993d8118e96667/src/main/resources/application.yml
https://github.com/j551234/oauth2_prototype/blob/5f33b88503df6d434c56648bfb4c1d24d5aa7331/src/main/java/com/example/api/OAuth2Controller.java
https://github.com/Dendrocopos-kr/ModumE-Spring-boot/blob/91336844f5cc3fe56a0f6c813c92dc072b0c4fd7/src/main/java/com/amolrang/modume/api/CallApi.java


## Testing

https://github.com/Saljack/spring-security-9477/blob/fa87deb834cfd30945c5870b4ca4ae37ef2617cc/src/test/java/com/saljack/springsecurity9477/SpringSecurity9477ApplicationTests.java

## Spring Security classes

### class OAuth2AuthorizationRequestRedirectFilter

Package: `org.springframework.security.oauth2.client.web`

This Filter initiates the authorization code grant or implicit grant flow by redirecting the End-User's user-agent to the Authorization Server's Authorization Endpoint.
It builds the OAuth 2.0 Authorization Request, which is used as the redirect URI to the Authorization Endpoint. The redirect URI will include the client identifier, requested scope(s), state, response type, and a redirection URI which the authorization server will send the user-agent back to once access is granted (or denied) by the End-User (Resource Owner).
By default, this Filter responds to authorization requests at the URI `/oauth2/authorization/{registrationId}` using the default `OAuth2AuthorizationRequestResolver`. The URI template variable `{registrationId}` represents the registration identifier of the client that is used for initiating the OAuth 2.0 Authorization Request.
The default base URI `/oauth2/authorization` may be overridden via the constructor `OAuth2AuthorizationRequestRedirectFilter(ClientRegistrationRepository, String)`, or alternatively, an `OAuth2AuthorizationRequestResolver` may be provided to the constructor `OAuth2AuthorizationRequestRedirectFilter(OAuth2AuthorizationRequestResolver)` to override the resolving of authorization requests.

### class OAuth2AuthorizationCodeGrantFilter

Package: `org.springframework.security.oauth2.client.web`

A Filter for the OAuth 2.0 Authorization Code Grant, which handles the processing of the OAuth 2.0 Authorization Response.
The OAuth 2.0 Authorization Response is processed as follows:
- Assuming the End-User (Resource Owner) has granted access to the Client, the Authorization Server will append the code and state parameters to the redirect_uri (provided in the Authorization Request) and redirect the End-User's user-agent back to this Filter (the Client).
- This Filter will then create an OAuth2AuthorizationCodeAuthenticationToken with the code received and delegate it to the AuthenticationManager to authenticate.
- Upon a successful authentication, an Authorized Client is created by associating the client to the access token and current Principal and saving it via the `OAuth2AuthorizedClientRepository`.

### interface ClientRegistrationRepository

Package: `org.springframework.security.oauth2.client.registration`

A repository for OAuth 2.0 / OpenID Connect 1.0 ClientRegistration(s).
NOTE: Client registration information is ultimately stored and owned by the associated Authorization Server. Therefore, this repository provides the capability to store a sub-set copy of the primary client registration information externally from the Authorization Server.

### interface OAuth2AuthorizedClientRepository

Package: `org.springframework.security.oauth2.client.web`

Implementations of this interface are responsible for the persistence of Authorized Client(s) between requests.
The primary purpose of an Authorized Client is to associate an Access Token credential to a Client and Resource Owner, who is the Principal that originally granted the authorization.

### interface AuthenticationManager

Package: `org.springframework.security.authentication`

Attempts to authenticate the passed `Authentication` object, returning a fully populated `Authentication` object (including granted authorities) if successful.
An AuthenticationManager must honour the following contract concerning exceptions:
- A `DisabledException` must be thrown if an account is disabled and the `AuthenticationManager` can test for this state.
- A `LockedException` must be thrown if an account is locked and the `AuthenticationManager` can test for account locking.
- A `BadCredentialsException` must be thrown if incorrect credentials are presented. Whilst the above exceptions are optional, an `AuthenticationManager` must always test credentials.
Exceptions should be tested for and if applicable thrown in the order expressed above (i.e. if an account is disabled or locked, the authentication request is immediately rejected and the credentials testing process is not performed). This prevents credentials being tested against disabled or locked accounts.
Params:
authentication – the authentication request object
Returns:
a fully authenticated object including credentials
Throws:
`AuthenticationException` – if authentication fails

### OAuth2ClientConfigurer

Package: `org.springframework.security.config.annotation.web.configurers.oauth2.client`

An `AbstractHttpConfigurer` for OAuth 2.0 Client support.
The following configuration options are available:
`authorizationCodeGrant()` - support for the OAuth 2.0 Authorization Code Grant
Defaults are provided for all configuration options with the only required configuration being `clientRegistrationRepository(ClientRegistrationRepository)`. Alternatively, a `ClientRegistrationRepository` `@Bean` may be registered instead.
