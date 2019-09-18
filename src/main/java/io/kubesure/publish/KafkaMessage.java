package io.kubesure.publish;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

/**
* Represent a Kafka message. Used by the service only.   
*/
public class KafkaMessage extends Thread {

    private static final Logger logger = Logger.getLogger(KafkaMessage.class.getName());
    private final KafkaProducer<Integer, String> producer;
    private final String topic;
    private final Boolean isAsync;
    private final String message;
    private long offset;

    public KafkaMessage(MessageMetaData message) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, message.getKafkaBrokerUrl());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KubesureProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
        this.topic = message.getTopic();
        this.isAsync = message.getIsAsync();
        this.message = message.getMessage();
    }

    /**
     * @return the offset
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void run() {
        int key = 12121;
        logger.info("isAsync" + isAsync);
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(topic, key, this.message),
                    new PolicyIssued(System.currentTimeMillis(), key, this.message));
        } else { // Send synchronously
            try {
                RecordMetadata metadata = producer.send(new ProducerRecord<>(topic, key, this.message)).get();
                this.setOffset(metadata.offset());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}

class PolicyIssued implements Callback {

    private static final Logger logger = Logger.getLogger(KafkaMessage.class.getName());
    private final long startTime;
    private final int key;
    private final String message;

    public PolicyIssued(long startTime, int key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of
     * request completion. This method will be called when the record sent to the
     * server has been acknowledged. When exception is not null in the callback,
     * metadata will contain the special -1 value for all fields except for
     * topicPartition, which will be valid.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the
     *                  partition and offset). Null if an error occurred.
     * @param exception The exception thrown during processing of this record. Null
     *                  if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            logger.info("message(" + key + ", " + message + ") sent to partition(" + metadata.partition() + "), "
                    + "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            logger.info(exception.getMessage());
            exception.printStackTrace();
        }
    }
}