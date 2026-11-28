package com.taiyidu.taiyidulibrary.service;

import com.taiyidu.taiyidulibrary.domain.dto.DocPageInfoDto;

import java.util.List;

public interface DocInfoService {
    List<DocPageInfoDto> showNewDoc();

    List<DocPageInfoDto> showRecommendDoc();

    List<DocPageInfoDto> showFavoriteDoc();
}
