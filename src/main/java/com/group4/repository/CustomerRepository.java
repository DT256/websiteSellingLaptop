package com.group4.repository;
import com.group4.entity.CustomerEntity;
import com.group4.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<UserEntity, Long> {
    List<CustomerEntity> findByNameOrEmail(String name, String email);

    @Query("SELECT c FROM CustomerEntity c WHERE c.email = :email")
    Optional<CustomerEntity> findCustomerByEmail(@Param("email") String email);

    @Query("SELECT c FROM CustomerEntity c")
    List<CustomerEntity> findAllCustomers();

    @Query("SELECT c FROM CustomerEntity c WHERE c.userID = :id")
    Optional<CustomerEntity> findCustomerById(@Param("id") Long id);
    Optional<CustomerEntity> findByEmail(String email);
}
