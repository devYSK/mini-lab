package com.yscorp.practice.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yscorp.practice.kafka.JsonDeserializer;
import com.yscorp.practice.kafka.model.JsonObject;

public class JsonConsumer {
	public static void main(String[] args) {
		// Kafka 컨슈머 설정
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "my-group");
		props.put("key.deserializer", StringDeserializer.class.getName());
		props.put("value.deserializer", JsonDeserializer.class.getName()); // JSON Deserializer
		props.put("value.deserializer.targetClass", JsonObject.class.getName());
		KafkaConsumer<String, JsonObject> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("my-topic"));

		//main thread
		Thread mainThread = Thread.currentThread();

		Runtime.getRuntime()
			.addShutdownHook(new Thread() {
				public void run() {
					System.out.println(" main program starts to exit by calling wakeup");
					consumer.wakeup();

					try {
						mainThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			});

		try {
			// 메시지 수신
			while (true) {
				ConsumerRecords<String, JsonObject> records = consumer.poll(Duration.ofMillis(1000));
				records.forEach(record -> {
					System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(),
						record.value());

				});
			}

		} catch (WakeupException e) {
			System.out.println("WakeupException occurred" + e.getMessage());
		} finally {
			consumer.close();
		}

	}
}
