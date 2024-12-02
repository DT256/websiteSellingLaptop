package com.group4.repository;

import com.group4.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SpringBootApplication
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    public UserEntity save(UserEntity user);


    //    public UserDto save(UserDto userDto);
    Optional<UserEntity> findByEmail(String email);


//    @Modifying
//    @Transactional
//    @Query("UPDATE UserEntity as u set u.password =?1 where u.email=?2")
//    int updatePassword(String password, String email);

    @Query("select u.password from UserEntity u where u.email=?1")
    String getUserEntitiesByUserId(String email);

    boolean existsByEmailAndPasswordAndActive(String email, String password, Boolean active);


    @Query(value = "SELECT u.userID FROM UserEntity u " +
            "WHERE u.email = :email"
    )
    Long findIdByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndActive(String email, Boolean active);

}
