package com.taiyidu.taiyiducommon.config;

import com.taiyidu.taiyiducommon.aspect.DistributedCacheAspect;
import com.taiyidu.taiyiducommon.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Redis 自动装配入口类
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate") // 1. 有 Redis 依赖才往下走
@ConditionalOnProperty(prefix = "spring.redis", name = "host") // 2. 有配置 host 才往下走
@Import({RedisConfig.class, DistributedCacheAspect.class}) // 3. 满足条件，才会去加载这两个类
public class RedisAutoConfiguration {
}