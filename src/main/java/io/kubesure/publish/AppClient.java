package io.kubesure.publish;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.kubesure.publish.PublisherProtos.Ack;
import io.kubesure.publish.PublisherProtos.Message;

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

    public void publish(String payload) {
        logger.info("Pay sent to publisher ");
        Message message = Message.newBuilder().setPayload(payload).build();
        Ack ack;
        try {
            ack = blockingStub.publish(message);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("is published: " + ack.getOk());
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