package com.xiaoyu.mapper;

import com.xiaoyu.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

/**
* @author xiaoyu
* @date 2023/03/06 19:03
* @version 1.0
*
*/

@Mapper
public interface UserMapper extends BaseMapper<User> {

}




