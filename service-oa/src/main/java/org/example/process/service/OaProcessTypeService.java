package org.example.process.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.process.ProcessType;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-08
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();
}
