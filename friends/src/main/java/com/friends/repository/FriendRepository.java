package com.friends.repository;

import com.friends.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, UUID> , JpaSpecificationExecutor<Friend> {
    Friend findByMainIdAndAddedFriendId (String mainId, String addedFriendId);
    Friend findByAddedFriendId(String id);
    List<Friend> findAllByAddedFriendId (String id);
}
