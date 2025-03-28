package com.yscorp.practice.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class FileProducer {
	public static final Logger logger = LoggerFactory.getLogger(FileProducer.class.getName());

	public static void main(String[] args) {

		String topicName = "file-topic";

		//KafkaProducer configuration setting
		// null, "hello world"

		Properties props = new Properties();
		//bootstrap.servers, key.serializer.class, value.serializer.class
		//props.setProperty("bootstrap.servers", "127.0.0.1:9092");
		props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		//KafkaProducer object creation
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);
		String filePath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "pizza_append.txt";


		//KafkaProducer객체 생성->ProducerRecords생성 -> send() 비동기 방식 전송
		sendFileMessages(kafkaProducer, topicName, filePath);
		kafkaProducer.close();

	}

	private static void sendFileMessages(KafkaProducer<String, String> kafkaProducer, String topicName,
		String filePath) {
		String line = "";
		final String delimiter = ",";

		try {
			FileReader fileReader = new FileReader(filePath);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null) {
				String[] tokens = line.split(delimiter);
				String key = tokens[0];
				StringBuffer value = new StringBuffer();

				for (int i = 1; i < tokens.length; i++) {
					if (i != (tokens.length - 1)) {
						value.append(tokens[i].trim() + ",");
					} else {
						value.append(tokens[i].trim());
					}
				}
				//"ord0, P001, Cheese Pizza, Erick Koelpin, (235) 592-3785 x9190, 6373 Gulgowski Path, 2022-07-14 12:09:33"
				sendMessage(kafkaProducer, topicName, key, value.toString());

			}

		} catch (IOException e) {
			logger.info(e.getMessage());
		}
	}

	//"ord0, P001, Cheese Pizza, Erick Koelpin, (235) 592-3785 x9190, 6373 Gulgowski Path, 2022-07-14 12:09:33"
	private static void sendMessage(
		KafkaProducer<String, String> kafkaProducer,
		String topicName,
		String key,
		String value
	) {
		ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, key, value);
		logger.info("key:{}, value:{}", key, value);
		//kafkaProducer message send
		kafkaProducer.send(producerRecord, (metadata, exception) -> {
			if (exception == null) {
				logger.info("\n ###### record metadata received ##### \npartition:{}\noffset:{}\ntimestamp:{}",
					metadata.partition(), metadata.offset(), metadata.timestamp());
			} else {
				logger.error("exception error from broker {}", exception.getMessage());
			}
		});

	}

}