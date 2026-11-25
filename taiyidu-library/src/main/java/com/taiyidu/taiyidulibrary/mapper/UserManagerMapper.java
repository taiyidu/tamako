package com.taiyidu.taiyidulibrary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taiyidu.taiyidulibrary.domain.po.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserManagerMapper extends BaseMapper<SysUser> {

}
