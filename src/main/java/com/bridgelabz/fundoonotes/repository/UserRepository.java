package com.bridgelabz.fundoonotes.repository;

import com.bridgelabz.fundoonotes.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    @Query(value="SELECT * FROM fundonotes.user where email_id=:emailId",nativeQuery=true)
    Optional<UserModel> findByEmail(String emailId);
}
