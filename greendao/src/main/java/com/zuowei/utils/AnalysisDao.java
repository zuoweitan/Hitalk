package com.zuowei.utils;

import java.io.File;
import java.lang.reflect.Field;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by zuowei on 16-7-14.
 */
public class AnalysisDao {

    private static final String PREFIX = "com.zuowei.config.";

    public static Class[] getAllDao(){
        Class [] allDao= null;
        String [] daosName = null;
        String className = null;
        File file = new File("./greendao/src/main/java/com/zuowei/config");
        if (file.exists() && (daosName = file.list()) != null){
            allDao = new Class[daosName.length];
            for(int i = 0;i < daosName.length;i++){
                try {
                    className = daosName[i].substring(0,daosName[i].indexOf("."));
                    allDao[i] = Class.forName(PREFIX + className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return allDao;
    }

    public static Entity getTable(Schema schema, Class c){
        Entity entity = null;
        String table = null;
        try {
            Field field = c.getDeclaredField("TABLE_NAME");
            field.setAccessible(true);
            table = (String) field.get(null);
            table = table.substring(0,1).toUpperCase() + table.substring(1);
            entity = schema.addEntity(table);
        } catch (Exception e) {
        }

        return entity;
    }
}
