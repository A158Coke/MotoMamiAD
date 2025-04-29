package com.motomami.jpa.repo;

import com.motomami.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  UserRepository extends JpaRepository<UserEntity,Long> {
}
