package com.acrabsoft.web.service.sjt.pc.operation.web.manager.service;


import com.acrabsoft.web.Configuration.SpringBeanUtil;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.dao.UsageDao;
import com.alibaba.fastjson.JSONObject;
import org.acrabsoft.common.model.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UsageService {
    @Autowired
    private UsageDao usageDao;

    public void getPeopleUseNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        String lastMonth = sdf.format(cal.getTime());
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        cal1.add(Calendar.MONTH, 0);
        String currentMonth = sdf.format(cal1.getTime());
        if (SpringBeanUtil.getApplicationContext().getEnvironment().getProperty("usageFlag").equals("0")) {
            currentMonth = "202007";
            lastMonth = "202006";
        }
    }


    public List<JSONObject> getSeriesObj(String module, String month) {
        List<JSONObject> series = new ArrayList<>();
        JSONObject seriesObj1 = new JSONObject();
        seriesObj1.put("name", "授权人数");
        seriesObj1.put("type", "bar");
        List<Integer> seriesData1 = this.usageDao.getData(module, "shouquan", month);
        seriesObj1.put("data", seriesData1);
        series.add(0, seriesObj1);

        JSONObject seriesObj2 = new JSONObject();
        seriesObj2.put("name", "使用人数");
        seriesObj2.put("type", "bar");
        List<Integer> seriesData2 = this.usageDao.getData(module, "shiyong", month);
        seriesObj2.put("data", seriesData2);
        series.add(1, seriesObj2);

        JSONObject seriesObj3 = new JSONObject();
        seriesObj3.put("name", "未使用人数");
        seriesObj3.put("type", "bar");
        List<Integer> seriesData3 = this.usageDao.getData(module, "weiyong", month);
        seriesObj3.put("data", seriesData3);
        series.add(2, seriesObj3);

        return series;
    }

    public Pagination getPeopleUseTable(String module, String month, String type, String name, int pageNum, int pageSize) {
        Pagination p = new Pagination(pageNum, pageSize);
        return this.usageDao.getPeopleUseTable(module, month, type, name, p);
    }
}
