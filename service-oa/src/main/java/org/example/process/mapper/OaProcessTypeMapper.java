package org.example.process.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.process.ProcessType;
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
public interface OaProcessTypeMapper extends BaseMapper<ProcessType> {

}
