package com.example.suzumechat.service.guest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.service.guest.dto.VisitorRequest;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.GuestFactory;
import com.example.suzumechat.utility.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class GuestServiceImplTests {
    @MockBean Hash hash;
    @MockBean Crypter crypter;
    @MockBean GuestRepository repository;
    @InjectMocks
    GuestServiceImpl service;

    @Autowired
    GuestFactory factory;

    @Autowired
    TestRandom testRandom;

    @Test
    public void createGuestAsVisitor_should_save_guest_as_visitor_and_return_visitor_request_info() throws Exception {
        val codename = testRandom.string.alphanumeric();
        val passphrase = testRandom.string.alphanumeric();
        val channelId = testRandom.string.alphanumeric();

        VisitorRequest result = service.createGuestAsVisitor(codename, passphrase, channelId);

        verify(repository, times(1)).save(any(Guest.class));
        assertThat(result.getCodename()).isEqualTo(codename);
        assertThat(result.getPassphrase()).isEqualTo(passphrase);
        assertThat(result.getIsAuthenticated().isEmpty()).isTrue();
    }

    @Test
    public void promoteToGuest_should_set_and_save_guest_id_and_isAuthenticated_true() throws Exception {
        val channelId = testRandom.string.alphanumeric();
        val visitorId = testRandom.string.alphanumeric();
        val visitorIdHashed = testRandom.string.alphanumeric();
        Guest guest = spy(new Guest());
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
        when(repository.findByVisitorIdHashed(visitorIdHashed)).thenReturn(guest);

        service.promoteToGuest(channelId, visitorId);

        verify(guest, times(1)).setIsAuthenticated(true);
        verify(repository, times(1)).save(any(Guest.class));
    }

    @Test
    public void updateStatus_should_set_isAuthenticated_to_specified_value() throws Exception {
        val visitorId = testRandom.string.alphanumeric();
        val isAuthenticated = testRandom.bool.nextBoolean();
        val visitorIdHashed = testRandom.string.alphanumeric();
        when(hash.digest(visitorId)).thenReturn(visitorIdHashed);
    
        service.updateStatus(visitorId, isAuthenticated);

        verify(repository, times(1)).updateIsAuthenticatedByVisitorIdHashed(visitorIdHashed, isAuthenticated);
    }

}
