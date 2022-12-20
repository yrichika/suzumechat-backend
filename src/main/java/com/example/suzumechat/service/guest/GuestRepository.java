package com.example.suzumechat.service.guest;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuestRepository extends JpaRepository<Guest, Integer> {

    public List<Guest> findAllByChannelIdOrderByIdDesc(String channelId);

    @Query("SELECT guest FROM Guest guest WHERE guest.channelId = :channelId AND guest.isAuthenticated = NULL")
    public List<Guest> getPendedVisitorsByChannelId(@Param("channelId") String channelId);

    public Optional<Guest> findByGuestIdHashed(String guestIdHashed);

    public Optional<Guest> findByVisitorIdHashed(String visitorIdHashed);

    @Modifying
    @Query("DELETE FROM Guest guest WHERE guest.channelId IN :channelIds")
    public int deleteByChannelIds(@Param("channelIds") List<String> channelIds);

    @Modifying
    @Query("update Guest guest set guest.isAuthenticated = :isAuthenticated where guest.visitorIdHashed = :visitorIdHashed")
    public int updateIsAuthenticatedByVisitorIdHashed(
        @Param("visitorIdHashed") String visitorIdHashed,
        @Param("isAuthenticated") Boolean isAuthenticated);
}
