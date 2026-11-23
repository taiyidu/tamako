package com.taiyidu.taiyidulibrary.service.Impl;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taiyidu.taiyidulibrary.domain.dto.DocPageInfoDto;
import com.taiyidu.taiyidulibrary.domain.po.DocInfo;
import com.taiyidu.taiyidulibrary.mapper.DocInfoMapper;
import com.taiyidu.taiyidulibrary.service.DocInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocInfoServiceImpl implements DocInfoService{
    private final DocInfoMapper docInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 新书榜单展示
     * @return List<DocPageInfoDto> 展示dto的list
     * key的命名格式是  业务模块:数据类型:唯一标识
     */
    @Override
    public List<DocPageInfoDto> showNewDoc() {
        //解决缓存的  穿透 击穿 雪崩 问题
        String cacheKey = "taiyidulibrary:DocPageInfoDto:showNewDoc:";
        String jsonStr = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(jsonStr)){
            //缓存命中直接返回
            return JSONUtil.toList(jsonStr,DocPageInfoDto.class);
        }
        //缓存未命中，在数据库中查一下，然后把结果写入缓存

        // 使用 QueryWrapper 按时间倒序，查前 10 条
        LambdaQueryWrapper<DocInfo> docInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        docInfoLambdaQueryWrapper.orderByDesc(DocInfo::getCreateTime); // 假设你的表里有 create_time 字段
        docInfoLambdaQueryWrapper.last("LIMIT 40");
        List<DocInfo> docInfos = docInfoMapper.selectList(docInfoLambdaQueryWrapper);

        //这里按理来说得防止穿透
        //这里其实有问题了，现在他不应该没有数据！！这个是查询新书榜，不可能没有数据，直接报错
        if(CollectionUtil.isEmpty(docInfos)){
            //存值的时候直接存""空串的时候会被上面的StringUtils.hasText(jsonStr)反复的查数据库，就没有写下面的代码的意义了。所以注意这个细节！
            stringRedisTemplate.opsForValue().set(cacheKey, "[]",1, TimeUnit.MINUTES);
            return null;
        }
        //数据查到了，转dto写回redis
        List<DocPageInfoDto> docPageInfoDtos = docInfos.stream().map(doc -> {
            DocPageInfoDto dto = new DocPageInfoDto();
            BeanUtils.copyProperties(doc, dto);
            return dto;
        }).collect(Collectors.toList());

        //这里的随机的 expireTime 是为了防止缓存击穿和雪崩
        long expireTime = 30 + RandomUtil.randomInt(1, 5);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(docPageInfoDtos), expireTime, TimeUnit.MINUTES);

        return docPageInfoDtos;
    }

    /**
     * 推荐榜单展示
     * @return List<DocPageInfoDto> 展示dto的list
     */
    @Override
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
