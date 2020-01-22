package io.kubesure.publish;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
    private KafkaProducer<Integer, String> producer = null;
    private String topic = null;
    private Boolean isAsync = null;
    private String message = null;
    private long offset;

    public KafkaMessage(MessageMetaData message) {
        try {
            logger.info(System.getenv("CC_USERNAME"));
            logger.info(System.getenv("CC_PASSWORD"));
            String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
            String jaasCfg = String.format(jaasTemplate, System.getenv("CC_USERNAME"), System.getenv("CC_PASSWORD"));
            Properties props = getConfigProperties();
            // props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            // message.getKafkaBrokerUrl());
            // props.put(ProducerConfig.CLIENT_ID_CONFIG, "KubesureProducer");
            logger.info("Kafka Broker " +  props.getProperty("bootstrap.servers"));
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put("sasl.jaas.config", jaasCfg);
            producer = new KafkaProducer<>(props);
            this.topic = message.getTopic();
            this.isAsync = message.getIsAsync();
            this.message = message.getMessage();
        } catch (Exception e) {
            logger.severe(e.toString());
        }
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

    private Properties getConfigProperties() throws IOException {
        try {
            String appConfigLocation = System.getenv("APP_CONFIG_FILE");
            logger.info(appConfigLocation);
            Properties appProps = new Properties();
            InputStream in = null;
            if (appConfigLocation != null && appConfigLocation.length() != 0) {
                FileReader reader = new FileReader(appConfigLocation);
                appProps.load(reader);
            } else {
                in = this.getClass().getClassLoader().getResourceAsStream("application.properties");
                appProps = new Properties();
                appProps.load(in);
                
            }
            //logger.info("host " + appProps.getProperty("host"));
            //logger.info("port " + appProps.getProperty("port"));
            //InetAddress address = InetAddress.getByName(appProps.getProperty("host"));
            //logger.info("ip of confluent " + address.getHostAddress());
            //appProps.setProperty("bootstrap.servers", address.getHostAddress()+ ":" + appProps.getProperty("port"));
            //logger.info("broker " + appProps.getProperty("bootstrap.servers"));
            return appProps;
        } catch (IOException e) {
            logger.severe("error loading properties file from classpath");
            e.printStackTrace();
            throw e;
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