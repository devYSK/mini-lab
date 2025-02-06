package com.yscorp.practice.kafka.producer;

import com.yscorp.practice.kafka.event.EventHandler;
import com.yscorp.practice.kafka.event.FileEventHandler;
import com.yscorp.practice.kafka.event.FileEventSource;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public class FileAppendProducer {
	public static final Logger logger = LoggerFactory.getLogger(FileAppendProducer.class.getName());

	public static void main(String[] args) {
		String topicName = "file-topic";

		//KafkaProducer configuration setting
		// null, "hello world"

		Properties props = new Properties();
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		//KafkaProducer object creation
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
		boolean sync = false;
		String absolutePath = System.getProperty("user.dir") +
			File.separator + "java-example" + File.separator +
			"src" + File.separator +
			"main" + File.separator +
			"resources" + File.separator +
			"pizza_append.txt";
		File file = new File(absolutePath);

		EventHandler eventHandler = new FileEventHandler(kafkaProducer, topicName, sync);
		FileEventSource fileEventSource = new FileEventSource(100, file, eventHandler);
		Thread fileEventSourceThread = new Thread(fileEventSource);
		fileEventSourceThread.start();

		try {
			fileEventSourceThread.join();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		} finally {
			kafkaProducer.close();
		}

	}
}
