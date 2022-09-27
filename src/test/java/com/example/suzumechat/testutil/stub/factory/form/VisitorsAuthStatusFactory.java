package com.example.suzumechat.testutil.stub.factory.form;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.suzumechat.service.channel.form.VisitorsAuthStatus;
import com.example.suzumechat.testutil.TestHelper;
import com.example.suzumechat.testutil.random.TestRandom;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(fluent = true, chain = true)
@Setter
public class VisitorsAuthStatusFactory {
    @Autowired
    private TestRandom random;

    private String visitorId = null;
    private Boolean isAuthenticated = null;

    public VisitorsAuthStatus make() {
        val visitorId = TestHelper.getOrDefault(this.visitorId,
                random.string.alphanumeric());
        val isAuthenticated = TestHelper.getOrDefault(this.isAuthenticated,
                random.bool.nextBoolean());

        val form = new VisitorsAuthStatus();
        form.setVisitorId(visitorId);
        form.setIsAuthenticated(isAuthenticated);
        reset();
        return form;
    }

    private void reset() {
        visitorId = null;
        isAuthenticated = null;
    }
}
