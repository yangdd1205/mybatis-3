/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.scripting.xmltags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ibatis.session.Configuration;

/**
 * trim 标签 SqlNode 实现类
 * @author Clinton Begin
 */
public class TrimSqlNode implements SqlNode {


  /**
   * 节点内容
   */
  private final SqlNode contents;
  /**
   * 前缀
   */
  private final String prefix;
  /**
   * 后缀
   */
  private final String suffix;
  /**
   * 需要被删除的前缀
   */
  private final List<String> prefixesToOverride;
  /**
   * 需要被删除的后缀
   */
  private final List<String> suffixesToOverride;
  private final Configuration configuration;

  public TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, String prefixesToOverride, String suffix, String suffixesToOverride) {
    this(configuration, contents, prefix, parseOverrides(prefixesToOverride), suffix, parseOverrides(suffixesToOverride));
  }

  protected TrimSqlNode(Configuration configuration, SqlNode contents, String prefix, List<String> prefixesToOverride, String suffix, List<String> suffixesToOverride) {
    this.contents = contents;
    this.prefix = prefix;
    this.prefixesToOverride = prefixesToOverride;
    this.suffix = suffix;
    this.suffixesToOverride = suffixesToOverride;
    this.configuration = configuration;
  }

  @Override
  public boolean apply(DynamicContext context) {
    // 创建 FilteredDynamicContext 对象
    FilteredDynamicContext filteredDynamicContext = new FilteredDynamicContext(context);
    // 执行 contents 的应用，调用其他节点的 apply 方法
    boolean result = contents.apply(filteredDynamicContext);
    // 执行 FilteredDynamicContext 的应用
    filteredDynamicContext.applyAll();
    return result;
  }

  /**
   * 解析需要覆盖的字符，用"|" 分隔后，转成大写
   * @param overrides
   * @return
   */
  private static List<String> parseOverrides(String overrides) {
    if (overrides != null) {
      final StringTokenizer parser = new StringTokenizer(overrides, "|", false);
      final List<String> list = new ArrayList<>(parser.countTokens());
      while (parser.hasMoreTokens()) {
        list.add(parser.nextToken().toUpperCase(Locale.ENGLISH));
      }
      return list;
    }
    return Collections.emptyList();
  }

  /**
   * trim 逻辑实现类
   */
  private class FilteredDynamicContext extends DynamicContext {
    /**
     * 委托的 DynamicContext 对象
     */
    private DynamicContext delegate;
    /**
     * 是否 prefix 已经被应用
     */
    private boolean prefixApplied;
    /**
     * 是否 suffix 已经被应用
     */
    private boolean suffixApplied;
    /**
     *  StringBuilder 对象
     */
    private StringBuilder sqlBuffer;

    public FilteredDynamicContext(DynamicContext delegate) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefixApplied = false;
      this.suffixApplied = false;
      this.sqlBuffer = new StringBuilder();
    }

    public void applyAll() {
      // trim 掉多余的空格，生成新的 sqlBuffer 对象。
      // sqlBuffer 会在 contents.apply(filteredDynamicContext); 被修改
      sqlBuffer = new StringBuilder(sqlBuffer.toString().trim());
      // 将 sqlBuffer 大写，生成新的 trimmedUppercaseSql 对象
      String trimmedUppercaseSql = sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
      // 应用 TrimSqlNode 的 trim 逻辑
      if (trimmedUppercaseSql.length() > 0) {
        // 应用前缀
        applyPrefix(sqlBuffer, trimmedUppercaseSql);
        // 应用后缀
        applySuffix(sqlBuffer, trimmedUppercaseSql);
      }
      delegate.appendSql(sqlBuffer.toString());
    }

    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }

    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

    @Override
    public void appendSql(String sql) {
      sqlBuffer.append(sql);
    }

    @Override
    public String getSql() {
      return delegate.getSql();
    }

    private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
      // 如果以应用，直接跳过
      if (!prefixApplied) {
        prefixApplied = true;
        if (prefixesToOverride != null) {
          // 遍历需要删除的字符集合
          for (String toRemove : prefixesToOverride) {
            // 如果是以需要被删除的字符开始
            if (trimmedUppercaseSql.startsWith(toRemove)) {
              // 删除
              sql.delete(0, toRemove.trim().length());
              // 一个满足条件就退出
              break;
            }
          }
        }
        // 插入前缀
        if (prefix != null) {
          sql.insert(0, " ");
          sql.insert(0, prefix);
        }
      }
    }

    private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
      if (!suffixApplied) {
        suffixApplied = true;
        if (suffixesToOverride != null) {
          for (String toRemove : suffixesToOverride) {
            // 如果是以被删除的字符结尾
            if (trimmedUppercaseSql.endsWith(toRemove) || trimmedUppercaseSql.endsWith(toRemove.trim())) {
              int start = sql.length() - toRemove.trim().length();
              int end = sql.length();
              //删除
              sql.delete(start, end);
              // 一个满足条件就退出
              break;
            }
          }
        }
        // 追加后缀
        if (suffix != null) {
          sql.append(" ");
          sql.append(suffix);
        }
      }
    }

  }

}
