package com.example.suzumechat.service.scheduled;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.service.ChannelService;
import com.example.suzumechat.service.guest.service.GuestService;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
@MockitoSettings
public class DbCleanerTests {
    @MockBean
    private ChannelService channelService;
    @MockBean
    private GuestService guestService;
    @MockBean
    private Environment env;

    @InjectMocks
    private DbCleaner dbCleaner;

    @Autowired
    TestRandom testRandom;
    @Autowired
    ChannelFactory channelFactory;
    @Autowired
    GuestFactory guestFactory;

    @Test
    public void clean_should_call_repositories_deleteByChannelIds() {
        val channel = channelFactory.make();
        val hour = testRandom.integer.nextInt(6);
        when(env.getProperty("db-clean.hour-old")).thenReturn(hour.toString());
        when(channelService.getItemsOrderThan(hour)).thenReturn(Arrays.asList(channel));

        dbCleaner.clean();

        verify(channelService, times(1)).deleteByChannelIds(Arrays.asList(channel.getChannelId()));
        verify(guestService, times(1)).deleteByChannelIds(Arrays.asList(channel.getChannelId()));
    }
}
