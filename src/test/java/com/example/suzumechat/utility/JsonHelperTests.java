package com.example.suzumechat.utility;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.dto.message.ChatMessageCapsule;
import com.example.suzumechat.service.channel.dto.message.VisitorsAuthStatus;
import com.example.suzumechat.service.guest.dto.message.JoinRequest;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

@SpringJUnitConfig
@Import(TestConfig.class)
public class JsonHelperTests {

    @Autowired
    private TestRandom random;

    private JsonHelper helper;
    private ObjectMapper mapper;

    @BeforeEach
    private void setUp() {
        helper = new JsonHelper();
        mapper = new ObjectMapper();
    }

    @Test
    public void hasAllFieldsOf_should_return_true_if_json_has_all_fields_of_the_class()
        throws Exception {

        final ChatMessageCapsule chatMessageCapsule =
            new ChatMessageCapsule(random.string.alphanumeric());
        val chatMessageCapsuleJson = mapper.writeValueAsString(chatMessageCapsule);
        val chatMessageCapsuleResult = helper.hasAllFieldsOf(chatMessageCapsuleJson,
            ChatMessageCapsule.class);
        assertThat(chatMessageCapsuleResult).isTrue();

        final VisitorsAuthStatus visitorsAuthStatus =
            new VisitorsAuthStatus(random.string.alphanumeric(), false);
        val visitorsAuthStatusJson = mapper.writeValueAsString(visitorsAuthStatus);
        val visitorsAuthStatusResult = helper.hasAllFieldsOf(visitorsAuthStatusJson,
            VisitorsAuthStatus.class);
        assertThat(visitorsAuthStatusResult).isTrue();

        final JoinRequest joinRequest = new JoinRequest(random.string.alphanumeric(),
            random.string.alphanumeric(), random.string.alphanumeric());
        val joinRequestJson = mapper.writeValueAsString(joinRequest);
        val joinRequestResult =
            helper.hasAllFieldsOf(joinRequestJson, JoinRequest.class);
        assertThat(joinRequestResult).isTrue();
    }

    @Test
    public void hasAllFieldsOf_should_return_false_if_json_does_not_have_any_field_of_the_class()
        throws Exception {
        final ChatMessageCapsule chatMessageCapsule =
            new ChatMessageCapsule(random.string.alphanumeric());
        val chatMessageCapsuleJson = mapper.writeValueAsString(chatMessageCapsule);

        val comparedWithVisitorsAuthStatus = helper
            .hasAllFieldsOf(chatMessageCapsuleJson, VisitorsAuthStatus.class);
        assertThat(comparedWithVisitorsAuthStatus).isFalse();

        val comparedWithJoinRequest =
            helper.hasAllFieldsOf(chatMessageCapsuleJson, JoinRequest.class);
        assertThat(comparedWithJoinRequest).isFalse();

    }

}
