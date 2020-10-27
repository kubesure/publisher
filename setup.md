# Publisher API to a Kafka or any broker 

### publisher dev setup 

Create topic policyissued

Development relies SCRAM auth. For a quick dev & test create a Kafka broker on Confluent Cloud

create topic policyissued

export CC_USERNAME=Confluent cloud api key
export CC_PASSWORD=Confluent cloud api secret

configure application.properties

make build
make run

gradle test

or


grpcurl -plaintext -d @ publishersvc:50051 Publisher/Publish <<EOM
{
  "version" : "v1",
  "type" : "policy",
  "payload" : "Istio grp curl message",
  "destination" : "policyissued"
}
EOM