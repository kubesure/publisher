package io.kubesure.publish;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.kubesure.publish.PublisherGrpc;
import io.kubesure.publish.PublisherProtos.Ack;
import io.kubesure.publish.PublisherProtos.Message;
import io.kubesure.publish.PublisherProtos.Message.Builder;

/**
* Test client
*/
public class AppClient {

    private static final Logger logger = Logger.getLogger(AppClient.class.getName());
    private final ManagedChannel channel;
    private final PublisherGrpc.PublisherBlockingStub blockingStub;

    public AppClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS
                // to avoid
                // needing certificates.
                .usePlaintext().build());
    }

    AppClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = PublisherGrpc.newBlockingStub(channel);
    }

    public Ack publish(String payload) {
        logger.info("Payload sent to publisher ");
        Builder builder = Message.newBuilder();
        builder.setPayload(payload);
        builder.setVersion("v1");
        builder.setType("Policy");
        builder.setDestination("policyissued");
        Message message = builder.build();
        try {
            Ack ack = blockingStub.publish(message);
            logger.info("is published: " + ack.getOk());
            return ack;
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return Ack.newBuilder().setOk(false).build(); 
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        AppClient client = new AppClient("localhost", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String payload = "supplies to mars";
            if (args.length > 0) {
                payload = args[0]; /* Use the arg as the name to greet if provided */
            }
            client.publish(payload);
        } finally {
            client.shutdown();
        }
    }
}