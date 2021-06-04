package com.acrabsoft.web.service.sjt.pc.operation.web.appManage.service;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import com.acrabsoft.web.service.sjt.pc.operation.web.util.PowerUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class HBaseService {
    private Logger logger = LoggerFactory.getLogger( this.getClass() );
    //默认所有列族为i
    String columnFamily = "i";

    @Value("${hbase.zookeeper.quorum}")
    public String zookeeperQuorum;
    @Value("${hbase.zookeeper.property.clientPort}")
    public String zookeeperPort;

    @Autowired
    private HbaseTemplate hbaseTemplate;


    /**
     * @description  懒汉式线程安全的获取Connection
     * @param
     * @return  返回结果
     * @date  2021-4-15 10:51
     * @author  wanghb
     * @edit
     */
    public Connection getCreateConnection() {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zookeeperQuorum );
        config.set("hbase.zookeeper.property.clientPort", zookeeperPort);
        Connection connection = null;
        if (connection == null) {
            try {
                connection = ConnectionFactory.createConnection( config );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * @description  懒汉式线程安全的获取AggregationClient
     * @param
     * @return  返回结果
     * @date  2021-4-15 10:51
     * @author  wanghb
     * @edit
     */
    public AggregationClient getAggregationClient() {
        AggregationClient aggregationClient = null;
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", zookeeperQuorum );
        config.set("hbase.zookeeper.property.clientPort", zookeeperPort);
        if (aggregationClient == null) {
            aggregationClient = new AggregationClient(config);
        }
        return aggregationClient;
    }


    public <T> T getBeanByRowKey(String tableName, String rowKey, final Class<T> beanType) {
        return hbaseTemplate.get(tableName, rowKey, new RowMapper<T>() {
            @Override
            public T mapRow(Result result, int rowNum) throws Exception {
                Map<byte[], byte[]> map = result.getFamilyMap(Bytes.toBytes(columnFamily));
                T t = beanType.newInstance();
                BeanWrapper beanWrapper = new BeanWrapperImpl(t);
                for(Entry<byte[], byte[]> entry : map.entrySet()){
                    beanWrapper.setPropertyValue(Bytes.toString(entry.getKey()),Bytes.toString(entry.getValue()));
                }
                return t;
            }
        });
    }


    /**
     * @description 获取map
     * @param  tableName
     * @param  rowKey
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public Map<String, String> getMapByRowKey(String tableName, String rowKey) {
        return hbaseTemplate.get(tableName, rowKey, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {
                Map<String, String> map = new HashMap<String, String>();
                if(result.listCells()!=null && result.listCells().size()>0){
                    Map<byte[], byte[]> mapColumn = result.getFamilyMap(Bytes.toBytes(columnFamily));
                    for(Entry<byte[], byte[]> entry : mapColumn.entrySet()){
                        map.put(Bytes.toString(entry.getKey()),Bytes.toString(entry.getValue()));
                    }
                }
                return map;
            }
        });
    }


    /**
     * @description  获取所有数据
     * @param  tableName
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public List<Map<String, String>> getListMap(String tableName) {
        Scan scan = new Scan();
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {
                Map<String, String> map = new HashMap<>();
                String  rowkey = "";
                if(result.listCells()!=null && result.listCells().size()>0){
                    for (Cell cell : result.listCells()) {
                        rowkey =Bytes.toString( cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value =Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString( cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                        if(columnFamily.equals(family)) {
                            map.put(quali,value);
                        }
                    }
                    map.put("rowkey",rowkey);
                }
                return map;
            }
        });
    }


    /**
     * @description  通过前缀模糊查询
     * @param  tableName
     * @param  rowkey
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public List<Map<String, String>> getListMapByBefore(String tableName,String rowkey) {
        Scan scan = new Scan();
        scan.setFilter(new PrefixFilter(rowkey.getBytes()));
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {
                Map<String, String> map = new HashMap<>();
                String  rowkey = "";
                if(result.listCells()!=null && result.listCells().size()>0){
                    for (Cell cell : result.listCells()) {
                        rowkey =Bytes.toString( cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value =Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString( cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                        if(columnFamily.equals(family)) {
                            map.put(quali,value);
                        }
                    }
                    map.put("rowkey",rowkey);
                }
                return map;
            }
        });
    }


    /**
     * @description  通过后缀模糊查询
     * @param  tableName
     * @param  rowkey
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public List<Map<String, String>> getListMapByAfter(String tableName,String rowkey) {
        Scan scan = new Scan();
        Filter filter = new RowFilter(CompareOp.EQUAL,new RegexStringComparator(".*" + rowkey));
        scan.setFilter(filter);
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {
                Map<String, String> map = new HashMap<String, String>();
                String  rowkey = "";
                if(result.listCells()!=null && result.listCells().size()>0){
                    for (Cell cell : result.listCells()) {
                        rowkey =Bytes.toString( cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value =Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString( cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                        if(columnFamily.equals(family)) {
                            map.put(quali,value);
                        }
                    }
                    map.put("rowkey",rowkey);
                }
                return map;
            }
        });
    }




    /**
     * @description  通过范围查询
     * @param  tableName
     * @param  startRow
     * @param  stopRow
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public List<Map<String, String>> getListMap(String tableName,String regex, String startRow, String stopRow) {
        Scan scan = new Scan();
        if (PowerUtil.isNotNull( regex )) {
            Filter filter = new RowFilter( CompareOp.EQUAL, new RegexStringComparator( regex ) );
            scan.setFilter( filter );
        }
        if (PowerUtil.isNotNull( startRow )) {
            scan.setStartRow(Bytes.toBytes(startRow));
        } else {
            scan.setStartRow(Bytes.toBytes(""));
        }
        if (PowerUtil.isNotNull( stopRow )) {
            scan.setStopRow(Bytes.toBytes(stopRow));
        } else {
            scan.setStopRow(Bytes.toBytes(""));
        }

        Filter pf = new PrefixFilter(Bytes.toBytes(startRow));
        scan.setFilter(pf);
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {

                Map<String, String> map = new HashMap<String, String>();
                String  rowkey = "";
                if(result.listCells()!=null && result.listCells().size()>0){
                    for (Cell cell : result.listCells()) {
                        rowkey =Bytes.toString( cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value =Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString( cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                        if(columnFamily.equals(family)) {
                            map.put(quali,value);
                        }
                    }
                    map.put("rowkey",rowkey);
                }
                return map;
            }
        });
    }




    /**
     * @description  通过通配符查询
     * @param  tableName
     * @param  regex  ".*20210331.*"  前后模糊查询   ".*20210331"   后缀模糊查询   "20210331.*"  前缀模糊查询
     * @return  返回结果
     * @date  2021-3-25 14:44
     * @author  wanghb
     * @edit
     */
    public List<Map<String, String>> getListMap(String tableName,String regex) {
        Scan scan = new Scan();
        Filter filter = new RowFilter(CompareOp.EQUAL,new RegexStringComparator(regex));
        scan.setFilter(filter);
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(Result result, int rowNum) throws Exception {

                Map<String, String> map = new HashMap<String, String>();
                String  rowkey = "";
                if(result.listCells()!=null && result.listCells().size()>0){
                    for (Cell cell : result.listCells()) {
                        rowkey =Bytes.toString( cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value =Bytes.toString( cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                        String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString( cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                        if(columnFamily.equals(family)) {
                            map.put(quali,value);
                        }
                    }
                    map.put("rowkey",rowkey);
                }
                return map;
            }
        });
    }


    /**
     * @description  保存
     * @param  tableName
     * @param  rowKey
     * @return  返回结果
     * @date  2021-3-25 14:43
     * @author  wanghb
     * @edit
     */
    public void save(String tableName, String rowKey, Map<String,Object> temp) throws IOException {

        Connection connection = getCreateConnection();
        try {
            BufferedMutator mutator = connection.getBufferedMutator(TableName.valueOf(tableName));
            Put put = new Put(Bytes.toBytes(rowKey));
            for(Entry<String, Object> entry : temp.entrySet()){
                String mapKey = entry.getKey();
                String mapValue = PowerUtil.getString( entry.getValue() );
                if(!"rowKey".equals( mapKey )){
                    put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(mapKey), Bytes.toBytes(mapValue));
                }
            }
            // 插入数据
            mutator.mutate(put);
            // 将缓存在本地的数据   请求Hbase插入
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    /**
     * @description  批量插入
     * @param  tableName
     * @param  list
     * @return  返回结果
     * @date  2021-3-30 11:03
     * @author  wanghb
     * @edit
     */
    public void batchSave(String tableName,List<Map<String,Object>> list){
        Connection connection = getCreateConnection();
        try {
            BufferedMutator mutator = connection.getBufferedMutator(TableName.valueOf(tableName));
            List<Put> listPut = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> temp = list.get( i );
                String rowKey = PowerUtil.getString( temp.get( "rowKey" ) );
                Put put = new Put(Bytes.toBytes(rowKey));
                for(Entry<String, Object> entry : temp.entrySet()){
                    String mapKey = entry.getKey();
                    String mapValue = PowerUtil.getString( entry.getValue() );
                    if(!"rowKey".equals( mapKey )){
                        put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(mapKey), Bytes.toBytes(mapValue));
                    }
                }
                listPut.add(put);
            }
            // 插入数据
            mutator.mutate(listPut);
            // 将缓存在本地的数据   请求Hbase插入
            mutator.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }


    /**
     * @description  删除
     * @param  tableName  表名称
     * @param  rowKey
     * @return  返回结果
     * @date  2021-3-25 14:43
     * @author  wanghb
     * @edit
     */
    public void delete(String tableName, final String rowKey) {
        hbaseTemplate.execute(tableName, new TableCallback<Boolean>() {
            @Override
            public Boolean doInTable(HTableInterface table) throws Throwable {
                boolean flag = false;
                try{
                    List<Delete> list = new ArrayList<Delete>();
                    Delete d1 = new Delete(rowKey.getBytes());
                    list.add(d1);
                    table.delete(list);
                    flag = true;
                }catch(Exception e){
                    e.printStackTrace();
                }
                return flag;
            }
        });
    }


    /**
     * @description  创建表
     * @param  tableName  表名称
     * @return  返回结果
     * @date  2021-3-25 14:43
     * @author  wanghb
     * @edit
     */
    public void createTable(String tableName) {
        Admin admin = null;
        Connection connection = getCreateConnection();
        try {
            admin = connection.getAdmin();
            if (!admin.isTableAvailable(TableName.valueOf(tableName))) {
                HTableDescriptor hbaseTable = new HTableDescriptor(TableName.valueOf(tableName));
                hbaseTable.addFamily(new HColumnDescriptor(columnFamily));
                admin.createTable(hbaseTable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (admin != null) {
                    admin.close();
                }

                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * @description  统计数量
     * @param  tablename
     * @return  返回结果
     * @date  2021-4-13 9:58
     * @author  wanghb
     * @edit
     */
    public Long getCount(String tablename,String regex){
        Connection connection = getCreateConnection();
        AggregationClient aggregationClient = null;
        try {
            //提前创建connection和conf
            Admin admin = connection.getAdmin();
            TableName name=TableName.valueOf(tablename);
            //先disable表，添加协处理器后再enable表
            admin.disableTable(name);
            HTableDescriptor descriptor = admin.getTableDescriptor(name);
            String coprocessorClass = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
            if (! descriptor.hasCoprocessor(coprocessorClass)) {
                descriptor.addCoprocessor(coprocessorClass);
            }
            admin.modifyTable(name, descriptor);
            admin.enableTable(name);
            Scan scan = new Scan();
            if (PowerUtil.isNotNull( regex )) {
                Filter filter = new RowFilter(CompareOp.EQUAL,new RegexStringComparator(regex));
                scan.setFilter(filter);
            }
            aggregationClient =  getAggregationClient();
            return aggregationClient.rowCount(name, new LongColumnInterpreter(), scan);
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            try {
                if (aggregationClient != null) {
                    aggregationClient.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @description  统计数量
     * @param  tablename
     * @return  返回结果
     * @date  2021-4-13 9:58
     * @author  wanghb
     * @edit
     */
    public Long getCount(String tablename,String regex,String startRow,String stopRow){
        Connection connection = getCreateConnection();
        AggregationClient aggregationClient = null;
        try {
            //提前创建connection和conf
            Admin admin = connection.getAdmin();
            TableName name=TableName.valueOf(tablename);
            //先disable表，添加协处理器后再enable表
            admin.disableTable(name);
            HTableDescriptor descriptor = admin.getTableDescriptor(name);
            String coprocessorClass = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
            if (! descriptor.hasCoprocessor(coprocessorClass)) {
                descriptor.addCoprocessor(coprocessorClass);
            }
            admin.modifyTable(name, descriptor);
            admin.enableTable(name);
            Scan scan = new Scan();
            if (PowerUtil.isNotNull( regex )) {
                Filter filter = new RowFilter( CompareOp.EQUAL, new RegexStringComparator( regex ) );
                scan.setFilter( filter );
            }
            if (PowerUtil.isNotNull( startRow )) {
                scan.setStartRow(Bytes.toBytes(startRow));
            } else {
                scan.setStartRow(Bytes.toBytes(""));
            }
            if (PowerUtil.isNotNull( stopRow )) {
                scan.setStopRow(Bytes.toBytes(stopRow));
            } else {
                scan.setStopRow(Bytes.toBytes(""));
            }
            aggregationClient = getAggregationClient();
            return aggregationClient.rowCount(name, new LongColumnInterpreter(), scan);
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            try {
                if (aggregationClient != null) {
                    aggregationClient.close();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }


    /**
     * @description  删除表
     * @param  tableName  表名称
     * @return  返回结果
     * @date  2021-3-25 14:43
     * @author  wanghb
     * @edit
     */
    public void delTable(String tableName) {
        Admin admin = null;
        Connection connection = getCreateConnection();
        try {
            admin = connection.getAdmin();
            if (admin.isTableAvailable(TableName.valueOf(tableName))) {
                admin.disableTable(TableName.valueOf(tableName));
                admin.deleteTable(TableName.valueOf(tableName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (admin != null) {
                    admin.close();
                }

                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

}
