# publisher

Publisher API to a Kafka or any broker

```
set KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"

./zookeeper-server-stop.sh config/zookeeper.properties &

./kafka-server-start.sh config/server.properties & 

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic policyissued

./kafka-topics.bat --describe --zookeeper localhost:2181 --topic policyissued

./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic policyissued --from-beginning

./gradlew.bat clean

./gradlew.bat install

./build/install/publisher/bin/publisher-server.bat

./gradlew.bat test
