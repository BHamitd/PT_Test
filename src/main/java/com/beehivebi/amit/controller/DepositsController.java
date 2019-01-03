package com.beehivebi.amit.controller;


import com.beehivebi.amit.model.PlayerDeposit;
import com.beehivebi.amit.service.DepositService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

@Controller
public class DepositsController {
    private static final String KAFKA_SERVERS = "localhost:9092";
    private static final String DEPOSIT_TOPIC = "deposit.events";
    private Integer playerId = null;


    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private DepositService depositService;

    @MessageMapping("/playerDeposit")
    public void PlayerDeposit(PlayerDeposit playerDeposit)
    {
        playerId = playerDeposit.getPlayerId();

        playerDeposit.setDate(new Date());
        depositService.create(playerDeposit);
        double aggDeposits = depositService.getAggregaetdDepositAmountByPlayer(playerDeposit.getPlayerId());
        SendToKafka(aggDeposits);
        //ConsumeAggDepositsPerPlayer();
    }

    public void SendToKafka(double aggDeposits)
    {
        Properties props = new Properties();
        props.put("bootstrap.servers" ,KAFKA_SERVERS);
        props.put("key.serializer" ,"org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer" ,"org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer kfProducer = new KafkaProducer<String, String>(props);

        try {
            kfProducer.send(new ProducerRecord<>(DEPOSIT_TOPIC, Double.toString(aggDeposits)));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            kfProducer.close();
        }
    }

    @MessageMapping("/ConsumeAggDepositsPerPlayer")
    private void ConsumeAggDepositsPerPlayer()
    {
        Properties props = new Properties();
        props.put("bootstrap.servers" ,KAFKA_SERVERS);
        props.put("key.deserializer" ,"org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer" ,"org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id" ,"consumerGroup_1");
        KafkaConsumer kfConsumer = new KafkaConsumer(props);
        ArrayList<String> topics = new ArrayList<>();
        topics.add(DEPOSIT_TOPIC);
        kfConsumer.subscribe(topics);
        try{
            while (true) {
                ConsumerRecords<String, String> aggDeposits = kfConsumer.poll(100);
                for(ConsumerRecord record: aggDeposits)
                {
                    double depositsAgg = new Double(record.value().toString()).doubleValue();
                    messagingTemplate.convertAndSend("/topic/depositAggregation/"+playerId,new PlayerDeposit(playerId,depositsAgg));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            kfConsumer.close();
        }
    }
}
