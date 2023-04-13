package org.example.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.model.process.ProcessTemplate;
import org.example.model.process.ProcessType;
import org.example.process.mapper.OaProcessTemplateMapper;
import org.example.process.mapper.OaProcessTypeMapper;
import org.example.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-08
 */
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    private OaProcessTemplateMapper oaProcessTemplateMapper;

    @Override
    public List<ProcessType> findProcessType() {
        List<ProcessType> typeList = baseMapper.selectList(null);
        for (ProcessType type : typeList) {
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId, type.getId());
            List<ProcessTemplate> processTemplates = oaProcessTemplateMapper.selectList(wrapper);
            type.setProcessTemplateList(processTemplates);
        }
        return typeList;
    }
}
