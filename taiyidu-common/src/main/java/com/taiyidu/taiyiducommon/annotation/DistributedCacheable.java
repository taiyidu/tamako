package com.taiyidu.taiyiducommon.annotation;

import java.lang.annotation.*;

/**
 * 用于代替默认的缓存注解用于分布式的将缓存放入redis解决缓存的 穿透 击穿 雪崩问题
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
    public @interface DistributedCacheable {
    /** 缓存 Key（建议写全，例如 doc:new::showNewDoc） */
    String cacheKey();

    /** 锁 Key（必须与缓存 Key 区分，例如 lock:doc:new:showNewDoc） */
    String lockKey();

    /** 基础过期时间（分钟），实际写入时会在此基础上加 1~5 分钟随机值防雪崩 */
    long ttlMinutes() default 30;

    /** 空值（防穿透）过期时间（分钟） */
    long nullTtlMinutes() default 2;

    /** 抢锁最大等待时间（毫秒），超过则走降级逻辑 */
    long waitTime() default 100;

    /** 降级时返回的值，默认为空集合（可根据业务改为返回旧数据或抛异常）
     * 这里的响应应该是在redis没有响应的时候返回的默认的降级返回值，但是好象直接忽略了熔断
     * */
    String fallback() default "empty";
}
