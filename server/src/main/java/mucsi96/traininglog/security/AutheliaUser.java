package mucsi96.traininglog.security;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

public class AutheliaUser extends User {

  @Getter
  private final String displayName;

  @Getter
  private final String email;

  public AutheliaUser(
      String username,
      String groups,
      String displayName,
      String email) {
    super(username, "N/A", Stream.of(groups.split(",")).map(group -> {
      return (GrantedAuthority) () -> "ROLE_" + group;
    }).collect(Collectors.toList()));
    this.displayName = displayName;
    this.email = email;
  }
}
