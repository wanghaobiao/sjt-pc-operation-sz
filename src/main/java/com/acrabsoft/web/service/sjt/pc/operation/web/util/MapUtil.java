package com.acrabsoft.web.service.sjt.pc.operation.web.util;

import com.acrabsoft.web.service.sjt.pc.operation.web.manager.controller.BaseController;
import com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils.CodeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Gte;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Title: Map的工具类
 * @author wanghb
 * @date 2019-07-18
 */
public class MapUtil extends BaseController {
    /**
     * 通过键获取String类型
     * @param key  键
     * @return value  值
     * @author wanghb
     * @date 2019-06-25
     */
    public static String getString(Map map,Object key) {
        return PowerUtil.getString(map.get(key));
    }

    /**
     * 通过键获取String类型  如果获取不到返回默认值
     * @explain  跟getOrDefault有所不同的是  getOrDefault如果存在这个键  它也还会返回空 这个则不会 省去了多余的判断
     * @param key  键
     * @return value  值
     * @author wanghb
     * @date 2019-06-25
     */
    public static String getStrOrDefaultStr(Map map,Object key, String defaultValue) {
        Object value = map.get(key);
        if(value == null){
            return defaultValue;
        }
        return PowerUtil.getString(value);
    }

    /**
     * map模糊取值 方法
     * @param keyLike String
     * @param map Map<String,Integer>
     * @return String类型
     * @author wanghb
     * @date 2017-11-13
     */
    public static List<Integer> likeString(String keyLike, Map<String,Integer> map) {
        List<Integer> list_integer_01 = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {

            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)it.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (key.indexOf(keyLike) != -1) {
                list_integer_01.add(value);
            }
        }
        return list_integer_01;
    }


    /**
     * 合并map 方法
     * @param maps Map<K, V>
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static <K, V> Map mergeMaps(Map<K, V>... maps) {
        Class clazz = maps[0].getClass(); // 获取传入map的类型
        Map<K, V> map = null;
        try {
            map = (Map) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0, len = maps.length; i < len; i++) {
            map.putAll(maps[i]);
        }
        return map;

    }

    /**
     * 实体类转Map 方法
     *
     * @param bean Object
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static Map<String, Object> toMap(Object bean){
        if(bean == null){
            return new HashMap<>();
        }
        Class type = bean.getClass();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                }
            }
        }catch (IntrospectionException e){
        }catch (InvocationTargetException e){
        }catch (IllegalAccessException e){}

        return  returnMap;
    }


    /**
     * 实体类转Map 方法
     *
     * @param bean Object
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static Map<String ,Object> toMap(String bean){
        if (PowerUtil.isNotNull( bean )) {
            bean = bean.replaceAll( "\\\\\":" ,"=").replaceAll( "\\\\\"" ,"");
            Map<String,Object> parse = (Map) JSON.parse(bean);
            return parse;
        }else {
            return new HashMap<>();
        }
    }

    /**
     * List实体类转ListMap 方法
     *
     * @param ListBean List<T>
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static <T> List<Map<String, Object>> toListMap(List<T> ListBean)  {
        if(ListBean == null){
            return null;
        }
        List<Map<String, Object>> list_map_01 = new ArrayList<>();
        try {

            for (T bean : ListBean) {
                Class type = bean.getClass();
                Map<String,Object> returnMap = new HashMap<>();
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                for (int i = 0; i < propertyDescriptors.length; i++) {
                    PropertyDescriptor descriptor = propertyDescriptors[i];
                    String propertyName = descriptor.getName();
                    if (!propertyName.equals("class")) {
                        Method readMethod = descriptor.getReadMethod();
                        Object result = readMethod.invoke(bean, new Object[0]);
                        if (result != null) {
                            returnMap.put(propertyName, result);
                        } else {
                            returnMap.put(propertyName, "");
                        }
                    }
                }
                list_map_01.add(returnMap);
            }
        }catch (Exception e){
            return null;
        }
        return list_map_01;
    }

    /**
     * 将List<Map<String,Object>>  转成 List<been>的工具类
     * @param listMap List<Map<String,Object>> 类型
     * @param T Class 类型
     * @return List<T>类型
     * @author wanghb
     * @date 2018-05-28
     */
    public static <T> List<T> toListBean(List<Map<String,Object>> listMap, Class T){
        List<T> beanList = new ArrayList<T>();
        if (listMap == null) {
            return beanList;
        }
        for(int i=0, n= listMap.size(); i<n; i++){
            Map<String,Object> map = listMap.get(i);
            String json = JSON.toJSONString(map);
            T bean = (T)JSON.parseObject(json, T);
            beanList.add(bean);
        }
        return beanList;
    }

    /**
     * 将Map<String,Object>  转成 been>的工具类
     * @param map <Map<String,Object> 类型
     * @param T Class 类型
     * @return T类型
     * @author wanghb
     * @date 2018-05-28
     */
    public static <T>T toBean(Map<String,Object> map, Class T) {
        T bean = (T)JSON.parseObject(JSON.toJSONString(map), T);
        return bean;
    }

    /**
     * 将命名改为驼峰的方法
     * @param listMap List<T>
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static  void toHump(List<Map<String, Object>> listMap) {
        int listMapSize = listMap.size();
        for (int i = 0; i < listMapSize; i++) {
            Map<String,Object> map = listMap.get(i);
            Map<String,Object> humpMap = new HashMap<>();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            Iterator<Map.Entry<String, Object>> iter = entrySet.iterator();
            while (iter.hasNext())
            {
                Map.Entry<String, Object> entry = iter.next();
                String keyStr = entry.getKey();
                Object valueStr = entry.getValue();
                humpMap.put(PowerUtil.toHump(keyStr),valueStr);
            }
            listMap.set(i,humpMap);
        }
    }

    /**
     * 将命名改为驼峰的方法
     * @param map List<T>
     * @return String类型
     * @author wanghb
     * @date 2017-11-13

     */
    public static void toHump(Map<String, Object> map) {
        Map<String,Object> humpMap = new HashMap<>();
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry<String, Object> entry = iter.next();
            String keyStr = entry.getKey();
            Object valueStr = entry.getValue();
            humpMap.put(PowerUtil.toHump(keyStr),valueStr);
        }
        map.clear();
        Iterator<Map.Entry<String, Object>> iterHump = humpMap.entrySet().iterator();
        while (iterHump.hasNext())
        {
            Map.Entry<String, Object> entry = iterHump.next();
            String keyStr = entry.getKey();
            Object valueStr = entry.getValue();
            map.put(keyStr,valueStr);
        }
    }

    /**
     * @description  获取id
     * @param  rows
     * @return  返回结果
     * @date  20/09/07 20:04
     * @author  wanghb
     * @edit
     */
    public static String getIds(List<Map<String, Object>>  rows,String  key) {
        StringBuffer ids = new StringBuffer();
        for (int i = 0; i < rows.size(); i++) {
            String id = PowerUtil.getString( rows.get(i).get( key ) );
            ids.append( "'" ).append( id ).append( "'" );
            if(i != rows.size() - 1){
                ids.append( "," );
            }
        }
        return PowerUtil.isNull( ids.toString() ) ? "'XXX'" : ids.toString();
    }

    /**
     * @description  null转""
     * @param  map
     * @return  返回结果
     * @date  20/09/12 11:16
     * @author  wanghb
     * @edit
     */
    public static void toEmpty(Map map) {
        Set mapset = map.entrySet();
        String EMPTYSTR = "";
        if (map != null) {
            for (Iterator it = mapset.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                if (entry.getValue() == null) {
                    map.put(entry.getKey(), EMPTYSTR);
                }
                else {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * @description  null转""
     * @param  list
     * @return  返回结果
     * @date  20/09/12 11:16
     * @author  wanghb
     * @edit
     */
    public static void toEmpty(List list) {
        if (list != null && list.size() > 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Map map = (Map) it.next();
                toEmpty(map);
            }
        }
    }

    /**
     * @description  创建对象塞入 并给对象塞入 id 创建时间  更新时间
     * @param  beenClass  实体的class
     * @param  date  日期
     * return  been  实体
     * @date  2019/10/29 11:34
     * @author  wanghb
     * @edit  .
     */
    public static <T> T createBean(Class beenClass,Date date){
        T bean = null;
        try {
            bean = (T) beenClass.newInstance();
            Method[] methods = beenClass.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                //获取方法名
                String metnodName = method.getName();
                //参数值
                Class[] paramTypes = method.getParameterTypes();
                if(paramTypes.length == 1){
                    if(metnodName.equals("setId")){
                        method.invoke(bean,CodeUtils.getUUID32());
                    }
                    if(metnodName.equals("setId")){
                        method.invoke(bean,ParamEnum.deleted.noDel.getCode());
                    }
                    if(metnodName.equals("setCreateTime") || metnodName.equals("setUpdateTime")){
                        method.invoke(bean,date);
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return bean;
    }

    /**
     * @description  给已创建好的对象 id 创建时间  更新时间
     * @param  entity
     * @param  date  日期
     * return  been  实体
     * @date  2019/10/29 11:34
     * @author  wanghb
     * @edit  .
     */
    public static void setCreateBean(BaseEntity entity,String id,Date date){
        entity.setId( id );
        entity.setDeleted( ParamEnum.deleted.noDel.getCode() );
        entity.setCreateTime( date );
        entity.setUpdateTime( date );
    }

    /**
     * @description  没有id的话
     * @param  entity
     * @param  date  日期
     * return  been  实体
     * @date  2019/10/29 11:34
     * @author  wanghb
     * @edit  .
     */
    public static void setUpdateBean(BaseEntity entity,Date date){
        entity.setUpdateTime( date );
    }

    /**
     * @description  给已创建好的对象 id 创建时间  更新时间
     * @param  entitys
     * @param  date  日期
     * return  been  实体
     * @date  2019/10/29 11:34
     * @author  wanghb
     * @edit  .
     */
    public static void setBean(List entitys,String parentId,Date date){
        if (entitys == null) {
            return;
        }
        for (Object entityObj : entitys) {
            MiddleEntity entity = (MiddleEntity) entityObj;
            if (PowerUtil.isNull( entity.getId() )) {
                entity.setParentId( parentId );
                setCreateBean(entity, CodeUtils.getUUID32(),date);
            }else {
                setUpdateBean( entity,date );
            }
        }
    }

    /**
     * @description  逻辑删除专用
     * @param  entitys
     * @param  date  日期
     * return  been  实体
     * @date  2019/10/29 11:34
     * @author  wanghb
     * @edit  .
     */
    public static void setDeletedBean(List entitys,Date date,String deleted){
        for (Object entityObj : entitys) {
            MiddleEntity entity = (MiddleEntity) entityObj;
            entity.setDeleted( deleted );
            setUpdateBean( entity,date );
        }
    }

}

