package com.taiyidu.taiyidulibrary.controller;

import com.taiyidu.taiyiducommon.result.Result;
import com.taiyidu.taiyidulibrary.domain.dto.DocPageInfoDto;
import com.taiyidu.taiyidulibrary.service.DocInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/bookresource")
@RequiredArgsConstructor
@RestController
public class DocInfoController {
    private final DocInfoService docInfoService;
//    @GetMapping("/pageAllBookInfo")
//    public Result<DocPageInfoDto> pageBookInfo(){
//        log.info("pageBookInfo");
//        return Result.success();
//    }

    /**
     * 显示新书
     * @return List<DocPageInfoDto> 书籍展示的集合
     */
    @GetMapping("/shownewbooks")
    public Result<List<DocPageInfoDto>> showNewDoc(){
        log.info("showNewDoc");
        List<DocPageInfoDto> docList = docInfoService.showNewDoc();
        return Result.success(docList);
    }


    /**
     * 显示推荐书
     * @return List<DocPageInfoDto> 书籍展示的集合
     */
    @GetMapping("/showrecommendbooks")
    public Result<List<DocPageInfoDto>> showRecommendDoc(){
        log.info("showRecommendDoc");
        List<DocPageInfoDto> docList = docInfoService.showRecommendDoc();
        return Result.success(docList);
    }
}
