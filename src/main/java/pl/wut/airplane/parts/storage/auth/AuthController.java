package pl.wut.airplane.parts.storage.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AuthController {

  private final AuthService authService;

  @PostMapping("login")
  public AuthenticationResponse login(@RequestBody AuthenticationRequest user){
    return authService.login(user);
  }
}
