package com.example.suzumechat.service.guest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuestRepository extends JpaRepository<Guest, Integer> {
    
    public Guest findByChannelId(String channelId);

    public Guest findByGuestIdHashed(String guestIdHashed);

    public Guest findByVisitorIdHashed(String visitorIdHashed);

    public void deleteByChannelIdIn(List<String> channelIds);

    @Modifying
    @Query("update Guest guest set guest.isAuthenticated = :isAuthenticated where guest.visitorIdHashed = :visitorIdHashed")
    public int updateIsAuthenticatedByVisitorIdHashed(
        @Param("visitorIdHashed") String visitorIdHashed,
        @Param("isAuthenticated") Boolean isAuthenticated
    );
}
