package com.notify.repository;

import com.notify.entity.Notify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NotifyRepository extends JpaRepository<Notify, UUID> {
    @Query("SELECT n FROM Notify n WHERE n.userId = :userId AND n.type = 'NewMessage' AND n.readStatus = 'Unread'")
    List<Notify> findAllUnReadMessagesOptional(UUID userId);
}