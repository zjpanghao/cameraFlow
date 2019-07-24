package i221.service.impl;

import i221.entity.TrackMessage;
import i221.service.MessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaMessageHandler implements MessageService{
    static private Log logger = LogFactory.getLog(KafkaMessageHandler.class);
    private String kafkaTopic;
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public KafkaMessageHandler(String kafkaTopic, KafkaTemplate<Object, Object> kafkaTemplate) {
        this.kafkaTopic = kafkaTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(TrackMessage trackMessage) {
        try {
            //logger.info("send message to:" + kafkaTopic);
            kafkaTemplate.send(kafkaTopic, trackMessage);
        }catch (Exception e) {
            logger.error("send kafka message error:" + e.getMessage());
        }
    }
}
