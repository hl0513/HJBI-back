package com.hhj.hjbi.bizmq;//package com.yupi.springbootinit.bizmq;
//
//import com.rabbitmq.client.Channel;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class MyMessageConsumer {
//
//
//    // 指定程序监听的消息队列和确认机制
//    @SneakyThrows
//    @RabbitListener(queues = {"laogou_queue"},ackMode = "MANUAL")
//    public void receiveMeassage(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
//        log.info("receiveMessage message = {}",message);
//        channel.basicAck(deliveryTag,false);
//    }
//
//}
