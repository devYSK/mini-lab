package com.yscorp.javaexconsumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class ConsumerWakeup {

	public static final Logger logger = LoggerFactory.getLogger(ConsumerWakeup.class.getName());

	public static void main(String[] args) {
		logger.atLevel(org.slf4j.event.Level.INFO);

		String topicName = "pizza-topic";

		Properties props = new Properties();
		props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group_01");

		//        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "group-01-static");
		//        props.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "3");
		props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


		KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
		kafkaConsumer.subscribe(List.of(topicName));

		//main thread
		Thread mainThread = Thread.currentThread();

		//main thread 종료시 별도의 thread로 KafkaConsumer wakeup()메소드를 호출하게 함.
		Runtime.getRuntime()
			.addShutdownHook(new Thread(() -> {
				logger.info(" main program starts to exit by calling wakeup");
				kafkaConsumer.wakeup();

				try {
					mainThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}));

		System.out.println("consumer is starting");
		System.out.println("Subscribed topics: " + kafkaConsumer.subscription());

		try {
			while (true) {
				ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
				// System.out.println("consumerRecords count:" + consumerRecords.count());

				for (ConsumerRecord record : consumerRecords) {
					logger.info("record key:{},  partition:{}, record offset:{} record value:{}",
						record.key(), record.partition(), record.offset(), record.value());
				}
				Thread.sleep(1000);
			}
		} catch (WakeupException e) {
			logger.error("wakeup exception has been called");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			logger.info("finally consumer is closing");
			kafkaConsumer.close();
		}

	}
}
