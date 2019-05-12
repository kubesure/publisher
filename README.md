# publisher

<<<<<<< HEAD
set KAFKA_HEAP_OPTS="-Xmx1G -Xms1G" 
./zookeeper-server-stop.sh config/zookeeper.properties & 
./kafka-server-start.sh config/server.properties &  
./kafka-topics.bat --create --zookeeper 172.18.115.151:2181 --replication-factor 1 --partitions 1 --topic test 
./kafka-topics.bat --describe --zookeeper 172.18.115.151:2181 --topic policyissued 
./kafka-console-producer.bat --broker-list 172.18.115.151:9092 --timeout=100000 --topic policyissued 
./kafka-console-consumer.bat --bootstrap-server 172.18.115.151:9092 --topic policyissued --from-beginning 
=======
Publisher API to a Kafka or any broker

```
set KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"

./zookeeper-server-stop.sh config/zookeeper.properties &

./kafka-server-start.sh config/server.properties & 
>>>>>>> bbc71317309f9645c66a85f8eef5815adf3aa48d

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic policyissued

./kafka-topics.bat --describe --zookeeper localhost:2181 --topic policyissued

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic policyissued --from-beginning

./gradlew.bat clean

./gradlew.bat install

./build/install/publisher/bin/publisher-server.bat

./gradlew.bat test
