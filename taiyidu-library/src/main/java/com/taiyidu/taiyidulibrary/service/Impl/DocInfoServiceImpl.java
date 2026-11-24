package com.taiyidu.taiyidulibrary.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taiyidu.taiyiducommon.annotation.DistributedCacheable;
import com.taiyidu.taiyidulibrary.domain.dto.DocPageInfoDto;
import com.taiyidu.taiyidulibrary.domain.po.DocInfo;
import com.taiyidu.taiyidulibrary.mapper.DocInfoMapper;
import com.taiyidu.taiyidulibrary.service.DocInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocInfoServiceImpl implements DocInfoService{
    private final DocInfoMapper docInfoMapper;
    private final RedissonClient redissonClient;

    /**
     * 新书榜单展示
     * @return List<DocPageInfoDto> 展示dto的list
     * key的命名格式是  业务模块:数据类型:唯一标识
     * sync 在这里的作用是防止缓存击穿，即在在大量的请求进来的时候这个时候的缓存刚好过期，这个时候每一个请求都会查询数据库，但是数据库的查询需要时间
     * 字这个时间里，所有的请求竟来都需要进行处理，直接压死后端，这个时候我使用 sync 让一个线程去查询数据，但是实际上在多个服务共同去处理的时候这个还
     * 是会让多个线程去空转，所以我后面得改造一下使用redisson入解决这个问题
     *
     * 现在上面的问题已经解决，现在没有什么问题了
     */
    @Override
//    @Cacheable(value = "taiyidulibrary:DocPageInfoDto",
//            key = CacheConstants.NEW_DOC_KEY,
//            sync = true)
    @DistributedCacheable(
            cacheKey = "taiyidulibrary:DocPageInfoDto:showNewDoc",
            lockKey = "lock:taiyidulibrary:DocPageInfoDto:showNewDoc",
            ttlMinutes = 30,
            nullTtlMinutes = 2,
            waitTime = 100
    )
    public List<DocPageInfoDto> showNewDoc() {
        LambdaQueryWrapper<DocInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(DocInfo::getCreateTime);
        queryWrapper.last("LIMIT 40");
        List<DocInfo> docInfos = docInfoMapper.selectList(queryWrapper);
        //转换 DTO 并返回
        return docInfos.stream().map(doc -> {
            DocPageInfoDto dto = new DocPageInfoDto();
            BeanUtils.copyProperties(doc, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 推荐榜单展示
     * @return List<DocPageInfoDto> 展示dto的list
     */
    @Override
    @DistributedCacheable(
            cacheKey = "taiyidulibrary:DocPageInfoDto:showRecommendDoc",
            lockKey = "lock:taiyidulibrary:DocPageInfoDto:showRecommendDoc",
            ttlMinutes = 30,
            nullTtlMinutes = 2,
            waitTime = 100
    )
    public List<DocPageInfoDto> showRecommendDoc() {
        LambdaQueryWrapper<DocInfo> docInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        docInfoLambdaQueryWrapper.orderByDesc(DocInfo::getViewCount); // 假设你的表里有 view_count 字段
        docInfoLambdaQueryWrapper.last("LIMIT 40");
        List<DocInfo> docInfos = docInfoMapper.selectList(docInfoLambdaQueryWrapper);
        return docInfos.stream().map(doc -> {
            DocPageInfoDto dto = new DocPageInfoDto();
            BeanUtils.copyProperties(doc, dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
