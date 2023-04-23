package com.friends.repository;

import com.friends.entity.BannedFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BannedFriendRepository extends JpaRepository<BannedFriend, UUID> , JpaSpecificationExecutor<BannedFriend> {
    BannedFriend findByMainIdAndAddedFriendId (String mainId, String addedFriendId);
    BannedFriend findByAddedFriendId(String id);
    List<BannedFriend> findAllByAddedFriendId (String id);
}