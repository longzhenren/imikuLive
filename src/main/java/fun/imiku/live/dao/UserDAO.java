package fun.imiku.live.dao;

import fun.imiku.live.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {
    // https://docs.spring.io/spring-data/jpa/docs/2.2.x/reference/html/#repositories.query-methods
    List<User> findByEmail(String email);
}