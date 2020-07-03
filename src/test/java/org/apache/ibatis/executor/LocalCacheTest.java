package org.apache.ibatis.executor;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.Reader;

public class LocalCacheTest {
  private static SqlSessionFactory sqlSessionFactory;

  @Before
  public void setUp() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/executor/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSessionFactory.getConfiguration().setLocalCacheScope(LocalCacheScope.STATEMENT);
    }

  }

  @Test
  public void testLocalCacheTest() {
    SqlSession sqlSession = sqlSessionFactory.openSession(true);

    LocalCacheTestMapper mapper = sqlSession.getMapper(LocalCacheTestMapper.class);
//    PageHelper.startPage(1,1);
//    PageInfo pageInfo = new PageInfo(mapper.list(14L));


    mapper.select(14L);
   // mapper.select(14L);

    System.out.println("");
  }
}
