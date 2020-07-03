package org.apache.ibatis.autoconstructor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.LocalCacheTestMapper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.HashMap;
import java.util.Map;

@Intercepts(
  {
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
  }
)
public class RewriteSqlInterceptor implements Interceptor {
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];










    Object parameter = args[1];
    RowBounds rowBounds = (RowBounds) args[2];
    ResultHandler resultHandler = (ResultHandler) args[3];
    Executor executor = (Executor) invocation.getTarget();
    CacheKey cacheKey;
    BoundSql boundSql;
    //由于逻辑关系，只会进入一次
    if (args.length == 4) {
      //4 个参数时
      boundSql = ms.getBoundSql(parameter);
      cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
    } else {
      //6 个参数时
      cacheKey = (CacheKey) args[4];
      boundSql = (BoundSql) args[5];
    }
    BoundSql rewriteSql = new BoundSql(ms.getConfiguration(),boundSql.getSql()+" and 1=1 ",boundSql.getParameterMappings(),boundSql.getParameterObject());

    {
      MappedStatement mappedStatement = ms.getConfiguration().getMappedStatement("org.apache.ibatis.executor.LocalCacheTestMapper.select");


    Map map = new HashMap();
    map.put("id",15L);
//    map.put("param1",15L);

      RowBounds aDefault = RowBounds.DEFAULT;
      ResultHandler noResultHandler = Executor.NO_RESULT_HANDLER;
      BoundSql boundSql1 = mappedStatement.getBoundSql(map);
      executor.query(mappedStatement,map, aDefault, noResultHandler
        ,executor.createCacheKey(mappedStatement,map,aDefault,boundSql1),boundSql1);
    }

    return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, rewriteSql);
//    return invocation.proceed();
  }
}
