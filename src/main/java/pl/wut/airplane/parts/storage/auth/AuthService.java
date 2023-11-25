package pl.wut.airplane.parts.storage.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
    Optional<User> response = this.userRepository.findUserByUsername(
        authenticationRequest.getUsername());
    if(response.isPresent() && passwordEncoder.matches(authenticationRequest.getPassword(), response.get().getPassword())){
      return new AuthenticationResponse(authenticationRequest.getUsername());
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  }
}