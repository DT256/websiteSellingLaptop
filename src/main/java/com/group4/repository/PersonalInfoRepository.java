package com.group4.repository;

import com.group4.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PersonalInfoRepository extends JpaRepository<UserEntity, Long> {
}

