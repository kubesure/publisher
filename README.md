# publisher
Publisher API to a Kafka or any broke

set KAFKA_HEAP_OPTS="-Xmx1G -Xms1G"
./zookeeper-server-stop.sh config/zookeeper.properties &
./kafka-server-start.sh config/server.properties & 
./kafka-topics.bat --create --zookeeper 172.18.115.151:2181 --replication-factor 1 --partitions 1 --topic test
./kafka-topics.bat --describe --zookeeper 172.18.115.151:2181 --topic policyissued
./kafka-console-producer.bat --broker-list 172.18.115.151:9092 --timeout=100000 --topic policyissued
./kafka-console-consumer.bat --bootstrap-server 172.18.115.151:9092 --topic policyissued --from-beginning

