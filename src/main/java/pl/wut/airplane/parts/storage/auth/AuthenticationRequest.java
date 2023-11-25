package pl.wut.airplane.parts.storage.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationRequest {
String username;
String password;
}
