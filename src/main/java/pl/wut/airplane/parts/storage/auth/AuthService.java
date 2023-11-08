package pl.wut.airplane.parts.storage.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

  private final EmployeeRepository employeeRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
    this.employeeRepository = employeeRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
    Optional<Employee> response = this.employeeRepository.findUserByUsername(
        authenticationRequest.getUsername());
    if(response.isPresent() && passwordEncoder.matches(authenticationRequest.getPassword(), response.get().getPassword())){
      return new AuthenticationResponse(authenticationRequest.getUsername());
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  }
}