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
    Friend findByAddedFriendIdAndMainId (String addedFriendId, String mainId);
    Friend findByAddedFriendId(String id);
    Friend findByMainId(String id);
    List<Friend> findAllByAddedFriendId (String id);
    boolean existsByMainIdAndAddedFriendId (String mainId, String addedFriendId);

}
