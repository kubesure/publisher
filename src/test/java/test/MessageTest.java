package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.kubesure.publish.AppClient;

public class MessageTest {

    @Test
    public void testPublishMsg() throws Exception {
        AppClient client = new AppClient("localhost", 50051);
        String payload = "Musky";
        assertTrue("message not published", client.publish(payload,"users").getOk());
        client.shutdown();
    }
}