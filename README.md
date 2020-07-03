## 两种调用方式

### DefaultSqlSession#getMapper(Class)

1. 通过 Class 找到 MapperProxy （Mapper 接口的代理类）
2. 调用方法，会被拦截，根据方法名找到对应的 MappedStatement



### DefaultSqlSession#selectOne(String)

通过 String 找到 MappedStatement



## @Param 参数的传递

会被 `ParamNameResolver` 参数都会被转成一个 Map。 




## 一级缓存

MyBatis 的一级缓存默认是 `SESSION` 级别。如果多个 `session` 操作同一张表，比如删除或者新增了数据，是会导致脏读的。



### 与 Spring 集成

MyBatis 与 Spring 集成后，一级缓存只有在使用了事务的方法中生效。

原因是 Mybatis 是把 `session` 暴露出来给用户的，而 Spring 是封装好了。每次只需一个 Mapper 中的方法就会 `session.close()`

而开始事务的 `session` 会保存到 `ThreadLocal` 中。


获取 sqlSession 

`org.mybatis.spring.SqlSessionUtils#getSqlSession(ExecutorType,PersistenceExceptionTranslator)`

注册 SqlSession

`org.mybatis.spring.SqlSessionUtils#registerSessionHolder`



## Executor

![](http://static2.iocoder.cn/images/MyBatis/2020_01_04/05.png)





