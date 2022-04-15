/*
 * Copyright (C) 2022 Operacon.
 *
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/Operacon/imikuLive/blob/main/LICENSE
 */
package fun.imiku.live.dao;

import fun.imiku.live.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDAO extends JpaRepository<User, Integer> {
    // https://docs.spring.io/spring-data/jpa/docs/2.2.x/reference/html/#repositories.query-methods
    List<User> findByEmail(String email);

    List<User> findById(int id);

    List<User> findByNickname(String nickname);
}
