package org.example.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-09
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description);
}
