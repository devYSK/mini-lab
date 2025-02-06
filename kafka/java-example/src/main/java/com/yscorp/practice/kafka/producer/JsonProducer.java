package com.yscorp.practice.kafka.producer;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.yscorp.practice.kafka.JsonSerializer;
import com.yscorp.practice.kafka.model.JsonObject;

public class JsonProducer {
	public static void main(String[] args) {
		// Kafka 프로듀서 설정
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", JsonSerializer.class.getName()); // JSON Serializer

		KafkaProducer<String, JsonObject> producer = new KafkaProducer<>(props);

		// 전송할 JSON 객체
		var obj = new JsonObject("key", new Random().nextInt(10000), "value");

		// Kafka 토픽으로 메시지 전송
		producer.send(new ProducerRecord<>("my-topic", obj));
		producer.close();
	}

}

