package com.zuowei.generate;

import com.zuowei.func.Id;
import com.zuowei.func.Property;
import com.zuowei.utils.AnalysisDao;

import java.lang.reflect.Field;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GenerateGreenDao {

    public static void  main(String [] args) throws Exception{
        Schema schema = new Schema(1,"com.zuowei.dao.greendao");
        addTables(schema);
        new DaoGenerator().generateAll(schema,"./app/src/main/java-gen");
    }

    private static void addTables(Schema schema){
        Class[] allDaos = AnalysisDao.getAllDao();
        if (allDaos != null){
            for (Class c : allDaos){
                Entity entity = AnalysisDao.getTable(schema,c);
                try {
                    fillEntity(entity,c);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void fillEntity(Entity entity, Class c) throws IllegalAccessException {
        for (Field field : c.getDeclaredFields()){
            Property pt = field.getAnnotation(Property.class);
            Id id = field.getAnnotation(Id.class);
            String value = null;
            String type = null;
            de.greenrobot.daogenerator.Property.PropertyBuilder propertyBuilder = null;
            if (id != null){
                propertyBuilder = entity.addIdProperty();
                if (id.auto()){
                    propertyBuilder.autoincrement();
                }
            }else if (pt != null){
                field.setAccessible(true);
                value = (String) field.get(null);
                type = pt.type();
                switch (checkPropertyType(type)){
                    case 0:
                        propertyBuilder = entity.addIntProperty(value);
                        break;
                    case 1:
                        propertyBuilder = entity.addStringProperty(value);
                        break;
                    case 2:
                        propertyBuilder = entity.addDateProperty(value);
                        break;
                }
                if (propertyBuilder != null){
                    if (pt.primaryKey()){
                        propertyBuilder.primaryKey();
                    }else {
                        if (pt.notNull()){
                            propertyBuilder.notNull();
                        }
                    }
                }
            }
        }
    }

    private static int checkPropertyType(String type) {

        if ("integer".equals(type)){
            return 0;
        }

        if ("text".equals(type)){
            return 1;
        }

        if ("date".equals(type)){
            return 2;
        }

        return -1;
    }
}
