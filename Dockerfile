FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY build/install/publisher/lib /app/lib
COPY build/install/publisher/bin /app/bin 
EXPOSE 50051
CMD ["./bin/publisher-server"]
