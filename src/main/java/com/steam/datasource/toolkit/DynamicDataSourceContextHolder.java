/**
 * Copyright © 2018 organization baomidou
 * <pre>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <pre/>
 */
package com.steam.datasource.toolkit;

import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 核心基于ThreadLocal的切换数据源工具类
 *
 * @author TaoYu Kanyuxia
 * @since 1.0.0
 */
public final class DynamicDataSourceContextHolder {

  /**
   * 为什么要用链表存储(准确的是栈)
   * <pre>
   * 为了支持嵌套切换，如ABC三个service都是不同的数据源
   * 其中A的某个业务要调B的方法，B的方法需要调用C的方法。一级一级调用切换，形成了链。
   * 传统的只设置当前线程的方式不能满足此业务需求，必须模拟栈，后进先出。
   * </pre>
   *
   *为什么要用ThreadLocal
   * <pre>
   * ThreadLocal 用于提供线程局部变量，在多线程环境可以保证各个线程里的变量独立于其它线程里的变量。
   * 也就是说 ThreadLocal 可以为每个线程创建一个【单独的变量副本】，相当于线程的 private static 类型变量。
   * </pre>
   */
  @SuppressWarnings("unchecked")
  private static final ThreadLocal<Deque<String>> LOOKUP_KEY_HOLDER = new ThreadLocal() {
    @Override
    protected Object initialValue() {
      return new ArrayDeque();
    }
  };

  private DynamicDataSourceContextHolder() {
  }

  /**
   * 获得当前线程数据源
   *
   * @return 数据源名称
   */
  public static String peek() {
    return LOOKUP_KEY_HOLDER.get().peek();
  }

  /**
   * 设置当前线程数据源
   * <p>
   * 如非必要不要手动调用，调用后确保最终清除
   * </p>
   *
   * @param ds 数据源名称
   */
  public static void push(String ds) {
    LOOKUP_KEY_HOLDER.get().push(StringUtils.isEmpty(ds) ? "" : ds);
  }

  /**
   * 清空当前线程数据源
   * <p>
   * 如果当前线程是连续切换数据源 只会移除掉当前线程的数据源名称
   * </p>
   */
  public static void poll() {
    Deque<String> deque = LOOKUP_KEY_HOLDER.get();
    deque.poll();
    if (deque.isEmpty()) {
      LOOKUP_KEY_HOLDER.remove();
    }
  }

  /**
   * 强制清空本地线程
   * <p>
   * 防止内存泄漏，如手动调用了push可调用此方法确保清除
   * </p>
   */
  public static void clear() {
    LOOKUP_KEY_HOLDER.remove();
  }
}
