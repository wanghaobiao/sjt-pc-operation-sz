package com.acrabsoft.web.service.sjt.pc.operation.web.outInterface.service;

import com.acrabsoft.web.dao.base.BaseDao;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.entity.AppLogPushEntity;
import com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service.HBaseService;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.HbaseRowkeyMarkService;
import com.acrabsoft.web.service.sjt.pc.operation.web.system.service.SequenceService;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.*;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableTable;
import org.acrabsoft.common.BuildResult;
import org.acrabsoft.common.model.Result;
import org.acrabsoft.common.model.ResultEnum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
* 应用日志表( appLogService )服务实现类
* @author wanghb
* @since 2020-11-23 14:34:51
*/
@Service("appMarketService")
public class AppMarketService extends BaseController {

    /**
     * @description 上周应用使用情况
     * @param id 主键id
     * @return 实体对象
     * @date 2021-4-26 10:48:57
     * @author wanghb
     * @edit
     */
    public Result lastWeekAppUseCount(String id) {
        Map<String, Object> chartsData = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        list.add( ImmutableBiMap.of("周一","1527") );
        list.add( ImmutableBiMap.of("周二","2457") );
        list.add( ImmutableBiMap.of("周三","1245") );
        list.add( ImmutableBiMap.of("周四","3572") );
        list.add( ImmutableBiMap.of("周五","2359") );
        list.add( ImmutableBiMap.of("周六","764") );
        list.add( ImmutableBiMap.of("周日","1125") );
        chartsData.put("data",list);
        chartsData.put("yMax","4000");
        chartsData.put("desc","单位:人");
        chartsData.put("title","上周使用情况统计");
        return BuildResult.buildOutResult( ResultEnum.SUCCESS,chartsData);
    }
}
