package com.taiyidu.taiyidulibrary.controller;

import com.taiyidu.taiyiducommon.result.Result;
import com.taiyidu.taiyidulibrary.domain.dto.DocPageInfoDto;
import com.taiyidu.taiyidulibrary.service.DocInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    /**
     * 显示新书
     * @return List<DocPageInfoDto> 书籍展示的集合
     */

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/shownewbooks")
    public Result<List<DocPageInfoDto>> showNewDoc(){
        log.info("showNewDoc");
        List<DocPageInfoDto> docList = docInfoService.showNewDoc();

//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null) {
//            String userId = (String) auth.getPrincipal(); // 拿网关传的 ID
//            String payload = (String) auth.getCredentials(); // 拿网关传的完整载荷
//            log.info("User ID: {}, Payload: {}", userId, payload);
//        }

        return Result.success(docList);
    }


    /**
     * 显示推荐书
     * @return List<DocPageInfoDto> 书籍展示的集合
     */
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/showrecommendbooks")
    public Result<List<DocPageInfoDto>> showRecommendDoc(){
        log.info("showRecommendDoc");
        List<DocPageInfoDto> docList = docInfoService.showRecommendDoc();
        return Result.success(docList);
    }

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/showfavoritebooks")
    public Result<List<DocPageInfoDto>> showFavoriteDoc(){
        log.info("showFavoriteDoc");
        List<DocPageInfoDto> docList = docInfoService.showFavoriteDoc();
        return Result.success(docList);
    }
}
