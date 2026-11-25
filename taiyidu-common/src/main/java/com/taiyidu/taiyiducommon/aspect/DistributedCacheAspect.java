package com.taiyidu.taiyiducommon.aspect;

import com.taiyidu.taiyiducommon.annotation.DistributedCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedCacheAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedissonClient redissonClient;

    @Around("@annotation(distributedCacheable)")
    public Object around(ProceedingJoinPoint pjp, DistributedCacheable distributedCacheable) throws Throwable {
        String cacheKey = distributedCacheable.cacheKey();
        String lockKey = distributedCacheable.lockKey();

        // 1. 第一次查缓存
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("缓存命中，cacheKey={}", cacheKey);
            return cached;
        }
        log.info("缓存不存在，走数据库逻辑，cacheKey={}", cacheKey);

        // 2. 抢分布式锁
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(distributedCacheable.waitTime(), TimeUnit.MILLISECONDS)) {
                log.warn("获取分布式锁失败，走降级逻辑，lockKey={}", lockKey);
                return fallbackResult(distributedCacheable, pjp);
            }

            try {
                // 3. 双重检查
                cached = redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    return cached;
                }

                // 整个业务执行 + 写缓存 用 try-catch 包起来
                try {
                    Object result = pjp.proceed();

                    // 5. 防穿透 + 随机 TTL 防雪崩（原来的逻辑搬到这里）
                    if (result == null || (result instanceof java.util.Collection && ((java.util.Collection<?>) result).isEmpty())) {
                        redisTemplate.opsForValue().set(cacheKey, Collections.emptyList(),
                                distributedCacheable.nullTtlMinutes(), TimeUnit.MINUTES);
                    } else {
                        long randomTtl = distributedCacheable.ttlMinutes()
                                + ThreadLocalRandom.current().nextInt(1, 6);
                        redisTemplate.opsForValue().set(cacheKey, result, randomTtl, TimeUnit.MINUTES);
                    }

                    return result;
                } catch (Throwable ex) {
                    //  异常兜底：写错误占位缓存
                    redisTemplate.opsForValue().set(cacheKey, Collections.emptyList(), 1, TimeUnit.MINUTES);
                    log.error("缓存方法执行异常，cacheKey={}, lockKey={}", cacheKey, lockKey, ex);
                    throw ex; // 继续向上抛，让全局异常处理器处理
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取分布式锁被中断，lockKey={}", lockKey, e);
            return fallbackResult(distributedCacheable, pjp);
        }
    }

    private Object fallbackResult(DistributedCacheable distributedCacheable, ProceedingJoinPoint pjp) {
        return Collections.emptyList();
    }
}