package pl.wut.airplane.parts.storage.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

  Optional<Employee> findUserByUsername(String username);
}
