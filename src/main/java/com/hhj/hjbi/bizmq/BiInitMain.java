package com.hhj.hjbi.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化消息队列,只用在程序启动前启动一次
 */
public class BiInitMain {
    public static void main(String[] args) {
        try{
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("39.106.21.37");
            factory.setPort(5672);
            factory.setUsername("admin");
            factory.setPassword("admin");

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            String BI_DEAD_EXCHANGE = BiMqConstant.BI_DEAD_EXCHANGE_NAME;
            String EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;

            // 声明一个直连交换机，一个死信交换机（其实就是普通交换机）
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.exchangeDeclare(BI_DEAD_EXCHANGE, "direct");

            // 创建队列
            String queueName = BiMqConstant.BI_QUEUE_NAME;
            // 通过设置 x-message-ttl 属性，设置队列中消息的过期时间
            Map<String,Object> queueArgs = new HashMap<>();
            queueArgs.put("x-message-ttl", 60000);
            // 参数解释：queueDeclare(String queue, boolean durable, boolean exclusive,
            //                      boolean autoDelete, Map<String, Object> arguments)
            // durable: 持久化队列（重启后依然存在）
            // exclusive: 排他性队列（仅限此连接可见，连接关闭后队列删除）
            // autoDelete: 自动删除队列（无消费者时自动删除）
            channel.queueDeclare(queueName, true, false, false, queueArgs);

            String deadLetterRoutingKey = "";// 表示所有过期的消息都会被转发到死信队列

            Map<String,Object> deadArgs = new HashMap<>();
            deadArgs.put("x-dead-letter-exchange", BI_DEAD_EXCHANGE);
            deadArgs.put("x-dead-letter-routing-key", deadLetterRoutingKey);
            // 将队列绑定到交换机
            channel.queueBind(queueName, EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, deadArgs);
            // 创建死信队列
            String deadQueueName = BiMqConstant.BI_DEAD_QUEUE_NAME;
            // 声明死信队列，并将其绑定到死信交换机。
            channel.queueDeclare(deadQueueName, true, false, false, null);
            channel.queueBind(deadQueueName, BI_DEAD_EXCHANGE, deadLetterRoutingKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
