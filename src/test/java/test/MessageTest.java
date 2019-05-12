package test;

import org.junit.Test;

import io.kubesure.publish.AppClient;

public class MessageTest {

    @Test
    public void testPublishMsg() throws Exception {
        AppClient client = new AppClient("localhost", 50051);
        String payload = "supplies to mars";
        //assertTrue("message not published", client.publish(payload).getOk());
        client.shutdown();
    }
}