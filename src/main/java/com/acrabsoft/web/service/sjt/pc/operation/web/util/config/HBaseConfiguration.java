package com.acrabsoft.web.service.sjt.pc.operation.web.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

@Configuration
public class HBaseConfiguration {

    @Value("${hbase.zookeeper.quorum}")
    private String zookeeperQuorum;

    @Value("${hbase.zookeeper.property.clientPort}")
    private String clientPort;

    @Value("${zookeeper.znode.parent}")
    private String znodeParent;

    private  org.apache.hadoop.conf.Configuration hBaseConfiguration;

    public  org.apache.hadoop.conf.Configuration gethBaseConfiguration() {
        if (hBaseConfiguration == null) {
            org.apache.hadoop.conf.Configuration hBaseConfiguration = new org.apache.hadoop.conf.Configuration();
            hBaseConfiguration.set("hbase.zookeeper.quorum", zookeeperQuorum);
            hBaseConfiguration.set("hbase.zookeeper.property.clientPort", clientPort);
            hBaseConfiguration.set("zookeeper.znode.parent", znodeParent);
        }
        return hBaseConfiguration;
    }

    @Bean
    public HbaseTemplate hbaseTemplate() {
        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum);
        conf.set("hbase.zookeeper.property.clientPort", clientPort);
        conf.set("zookeeper.znode.parent", znodeParent);
        return new HbaseTemplate(conf);
    }
}
