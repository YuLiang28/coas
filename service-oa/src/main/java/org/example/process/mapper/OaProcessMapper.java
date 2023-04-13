package org.example.process.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2023-04-08
 */
@Repository
@Mapper
public interface OaProcessMapper extends BaseMapper<org.example.model.process.Process> {
    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo);


}
