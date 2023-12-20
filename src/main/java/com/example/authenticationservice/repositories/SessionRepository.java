package com.example.authenticationservice.repositories;


import com.example.authenticationservice.models.Session;
import com.example.authenticationservice.models.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session save(Session session);
    Optional<Session> findByTokenAndAndUser_Id(String token, Long userId);


}
