package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageTest {

    @Test
    public void testPublishMsg() throws Exception {
        AppClient client = new AppClient("localhost", 50051);
        String payload = "supplies to mars";
        assertTrue("message not published", client.publish(payload).getOk());
        client.shutdown();
    }
}