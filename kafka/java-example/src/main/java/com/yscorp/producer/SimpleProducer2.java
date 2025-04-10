package com.yscorp.producer;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public class SimpleProducer2 {

	public static void main(String[] args) {

		String topicName = "simple-topic";

		//KafkaProducer configuration setting
		// null, "hello world"

		Properties props = new Properties();
		//bootstrap.servers, key.serializer.class, value.serializer.class
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		//KafkaProducer object creation
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
		int count = 0;

		while (count <= 100) {
			count++;
			//ProducerRecord object creation
			ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName,
				"hello world " + new Random().nextInt(1000));
			// ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, "key001", "hello world 2");

			//KafkaProducer message send
			final Future<RecordMetadata> send = kafkaProducer.send(producerRecord, (metadata, exception) -> {
				if (exception == null) {
					System.out.println("\n ###### record metadata received ##### \n" +
						"partition:" + metadata.partition() + "\n" +
						"offset:" + metadata.offset() + "\n" +
						"timestamp:" + metadata.timestamp());
				} else {
					System.out.println("exception error from broker " + exception.getMessage());
				}
			});

			if (count > 100) {
				break;
			}
		}

		kafkaProducer.flush();
		kafkaProducer.close();
	}
}
