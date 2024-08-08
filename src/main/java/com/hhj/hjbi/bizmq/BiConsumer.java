package com.hhj.hjbi.bizmq;

import com.hhj.hjbi.common.ErrorCode;
import com.hhj.hjbi.constant.CommonConstant;
import com.hhj.hjbi.exception.BusinessException;
import com.hhj.hjbi.manager.AiManager;
import com.hhj.hjbi.model.entity.Chart;
import com.hhj.hjbi.service.ChartService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class BiConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME},ackMode = "MANUAL")
    public void receiveMeassage(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("receive message = {}" + message);
        if (StringUtils.isBlank(message)){
            // 如果失败，消息拒绝
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息不能为空");
        }
        long chartId = Long.parseLong(message);
        Chart chart = chartService.getById(chartId);
        if (chart == null){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"图表不存在");
        }
        // 先修改图表任务状态为 “执行中”。等执行成功后，修改为 “已完成”、保存执行结果；执行失败后，状态修改为 “失败”，记录任务失败信息。
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean b = chartService.updateById(updateChart);
        if (!b) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError("更新图表执行中状态失败",chart.getId());
            return;
        }
        // 调用 AI
        String result = aiManager.doChat(CommonConstant.BI_MODEL_ID, buildUserInput(chart));
        String[] splits = result.split("【【【【【");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError("AI 生成错误",chart.getId() );
            return;
        }
        String genChart = splits[1].trim();
        genChart = genChart.replaceAll("'", "\"");
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        // todo 建议定义状态为枚举值
        updateChartResult.setStatus("succeed");
        boolean updateResult = chartService.updateById(updateChartResult);
        if (!updateResult) {
            channel.basicNack(deliveryTag,false,false);
            handleChartUpdateError("更新图表成功状态失败",chart.getId());
        }
        // 消息确认
        channel.basicAck(deliveryTag,false);
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart){
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");

        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

    private  void handleChartUpdateError(String execMessage, Long chartId){
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        updateChartResult.setExecMessage("execMessage");
        boolean resultt = chartService.updateById(updateChartResult);
        if (!resultt){
            log.error("更新图表失败状态失败" + chartId + "=>" + execMessage);
        }
    }

}
