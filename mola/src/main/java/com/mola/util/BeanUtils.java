package com.mola.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by bilgi on 4/4/15.
 */
@Component
public class BeanUtils
{
    static ApplicationContext context;
    static
    {
        context =
                new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
    }

    public static <T> T createObjectFromId(Class clazz, Object... params){
        return (T)context.getBean(clazz, params);
    }
}
