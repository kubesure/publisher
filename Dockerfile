FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY build/install/publisher/lib /app/lib
COPY build/install/publisher/bin /app/bin 
COPY grpc-health-probe /app/bin
EXPOSE 50051
ENTRYPOINT ["/app/bin/publisher-server"]
