package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Statement;
import java.util.ArrayList;

@Intercepts({
  @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class IgnoreInterceptor implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) {
    try {
      System.out.println("插件执行了");
      Object proceed = invocation.proceed();

      if (proceed instanceof ArrayList) {
        ArrayList arrayList = (ArrayList) proceed;
        arrayList.forEach(e->{
          Class<?> clazz = e.getClass();
          Field[] fields = clazz.getDeclaredFields();
          for (Field field : fields) {
            Ignore ignore = field.getAnnotation(Ignore.class);
            if (ignore != null) {
              field.setAccessible(true);
              try {
                field.set(e,null);
              } catch (IllegalAccessException ex) {
                ex.printStackTrace();
              }
            }
          }
        });

      }
      return proceed;
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

}
