package org.apache.ibatis.executor;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface LocalCacheTestMapper {

  @Select("select * from goods_sku_preferential_retry_log where sku_id = #{id}")
  Map select(@Param("id")Long id);


  @Select("select * from goods_sku_preferential_retry_log where sku_id = #{id}")
  List<Map> list(@Param("id")Long id);

  @Insert("insert into goods_sku_preferential_retry_log (sku_id) values (#{id})")
  int insert(@Param("id")Long id);



  @Select("select * from wx_user_account where id=7 ")
  List<Map>  getList();
}
