package com.yscorp.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiBrokerProducer {
	public static final Logger logger = LoggerFactory.getLogger(MultiBrokerProducer.class.getName());

	public static void main(String[] args) throws InterruptedException {
		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092, 127.0.0.1:9094, 127.0.0.1:9096");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		String topicName = "topic-p2r3";
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);

		final ProducerRecord<String, String> test1 = new ProducerRecord<>(topicName, "test1");

		kafkaProducer.send(test1, (recordMetadata, e) -> {
			if (e != null) {
				logger.info("Exception occurred: {}", e.getMessage(), e);
			} else {
				logger.info("RecordMetadata: partition={}, offset={}, timestamp={}",
						recordMetadata.partition(), recordMetadata.offset(), recordMetadata.timestamp());
			}
		});

		Thread.sleep(3000);
		kafkaProducer.close();
	}
}
