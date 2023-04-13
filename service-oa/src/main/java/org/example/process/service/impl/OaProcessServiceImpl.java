package org.example.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.example.auth.service.SysUserService;
import org.example.common.config.exception.OAException;
import org.example.model.process.Process;
import org.example.model.process.ProcessRecord;
import org.example.model.process.ProcessTemplate;
import org.example.model.system.SysUser;
import org.example.process.mapper.OaProcessMapper;
import org.example.process.service.OaProcessRecordService;
import org.example.process.service.OaProcessService;
import org.example.process.service.OaProcessTemplateService;
import org.example.security.custom.LoginUserInfoHelper;
import org.example.vo.process.ApprovalVo;
import org.example.vo.process.ProcessFormVo;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2023-04-08
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, org.example.model.process.Process> implements OaProcessService {


    @Autowired
    private SysUserService sysUserService;


    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;


    @Autowired
    private OaProcessRecordService oaProcessRecordService;

    @Autowired
    private HistoryService historyService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, @Param("vo") ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> page = baseMapper.selectPage(pageParam, processQueryVo);
        return page;
    }

    @Override
    public void deployByZip(String deployPath) {
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        // 根据当前用户ID获取用户信息
        SysUser user = sysUserService.getById(LoginUserInfoHelper.getUserId());
        // 根据审批模板ID获取模板信息
        Long templateId = processFormVo.getProcessTemplateId();
        ProcessTemplate template = oaProcessTemplateService.getById(templateId);
        // 保存审批信息到业务表
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process); // bean复制

        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(user.getName() + " 发起 " + template.getName() + " 申请");
        process.setStatus(1); // 审批中状态

        baseMapper.insert(process);


        // 流程定义 key
        String processDefinitionKey = template.getProcessDefinitionKey();
        // 业务 key oa_process 中的 id
        String businessKey = String.valueOf(process.getId());
        // 流程参数 form 表单 json 转换 map 集合传入
        String formJson = processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(formJson);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        Map<String, Object> variables = new HashMap<>();
        variables.put("data", map);
        // 启动流程实例
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(
                processDefinitionKey, businessKey, variables
        );


        // 查询下一个审批人，推送消息（可能有多个）
        List<Task> taskList = getCurrentTaskList(instance.getId());
        List<String> nextUserNames = new ArrayList<>();

        for (Task task : taskList) {
            String assigneeName = task.getAssignee();
            SysUser nextUser = sysUserService.getUserByUsername(assigneeName);
            nextUserNames.add(nextUser.getName());

            // TODO 消息推送

        }
        // 更新关联的业务和流程表
        process.setProcessInstanceId(instance.getId());

        String userNamesStr = StringUtils.join(nextUserNames.toArray(), ",");
        String description = "等待" + userNamesStr + "审批";
        process.setDescription(description);

        baseMapper.updateById(process);

        oaProcessRecordService.record(process.getId(), 1, "发起申请");

    }

    // 查询待处理任务的列表
    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        // 根据当前登录的用户名查询
        String userName = LoginUserInfoHelper.getUserName();
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(userName)
                .orderByTaskCreateTime()
                .desc();

        // 分页查询
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> tasks = query.listPage(begin, size);
        long totalCount = query.count();

        // 封装返回List<ProcessVo>
        List<ProcessVo> processVoList = new ArrayList<>();
        for (Task task : tasks) {

            // 从task中获取流程实例id
            String processInstanceId = task.getProcessInstanceId();
            // 根据流程实例id获取实例对象
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if (instance == null) {
                continue;
            }
            // 从流程实例对象获取业务key
            String businessKey = instance.getBusinessKey();
            if (StringUtils.isEmpty(businessKey)) {
                continue;
            }
            // 根据业务key获取Process
            Process process = baseMapper.selectById(Long.parseLong(businessKey));
            // Process对象复制到ProcessVo中
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }

        // 封装返回IPage对象
        IPage<ProcessVo> page = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processVoList);
        return page;
    }

    @Override
    public Map<String, Object> showProcessInfo(Long processId) {
        // 获取流程信息
        Process process = baseMapper.selectById(processId);
        // 获取流程记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<ProcessRecord>();
        wrapper.eq(ProcessRecord::getProcessId, processId);

        List<ProcessRecord> recordList = oaProcessRecordService.list(wrapper);
        // 获取模板信息
        ProcessTemplate template = oaProcessTemplateService.getById(process.getProcessTemplateId());

        // 判断当前用户是否有权审批，且不能重复审批
        boolean isApprove = false;
        List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());

        for (Task task : taskList) {
            // 判断当前用户是否有权审批
            if (task.getAssignee().equals(LoginUserInfoHelper.getUserName())) {
                isApprove = true;
            }
        }

        // 将查询和数据封装返回
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", recordList);
        map.put("processTemplate", template);
        map.put("isApprove", isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        // 通过任务ID获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);

        // 判断当前用户是否有权审批，且不能重复审批
        boolean isApprove = false;
        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = getCurrentTaskList(process.getProcessInstanceId());

        for (Task task : taskList) {
            // 判断当前用户是否有权审批
            if (task.getAssignee().equals(LoginUserInfoHelper.getUserName())) {
                isApprove = true;
            }
        }
        if (!isApprove) {
            throw new OAException(209, "无权审批");
        }
        String statusDescription = null;

        // 判断审批值
        Integer status = approvalVo.getStatus();
        if (status.intValue() == 1) {
            // 通过
            HashMap<String, Object> variable = new HashMap<>();
            taskService.complete(taskId, variable);
            statusDescription = "通过";
        } else if (status.intValue() == -1) {
            // 驳回
            endTask(taskId);
            statusDescription = "驳回";
        }

        // 记录审批相关过程信息 oa_process_record
        oaProcessRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), statusDescription);
        // 查询下一个审批人，更新流程记录表 oa_process
        List<Task> tasks = getCurrentTaskList(process.getProcessInstanceId()); // 查询任务列表，如果为空代表流程结束
        if (!CollectionUtils.isEmpty(tasks)) {
            // 流程未结束
            List<String> nextUserNames = new ArrayList<>();
            for (Task task : tasks) {
                String assignee = task.getAssignee();
                SysUser sysUser = sysUserService.getUserByUsername(assignee);
                nextUserNames.add(sysUser.getName());

                // TODO 下一个审批人消息推送
            }
            String userNamesStr = StringUtils.join(nextUserNames.toArray(), ",");
            String description = "等待" + userNamesStr + "审批";
            process.setDescription(description);
            process.setStatus(1);
        } else {
            // 流程结束
            if (status.intValue() == 1) {
                // 通过，且流程结束
                process.setDescription("审批完成（通过）");
                process.setStatus(2);
            } else if (status.intValue() == -1) {
                // 驳回
                process.setDescription("审批完成（驳回）");
                process.setStatus(-1);
            }
        }
        baseMapper.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        // 封装条件
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUserName())
                .finished()
                .orderByTaskCreateTime()
                .desc();
        int begin = (int) ((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        long totalCount = query.count();
        List<HistoricTaskInstance> list = query.listPage(begin, size);

        List<ProcessVo> processVos = new ArrayList<>();
        for (HistoricTaskInstance instance : list) {
            // 根据流程实例，获取process
            String instanceId = instance.getProcessInstanceId();
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId, instanceId);
            Process process = baseMapper.selectOne(wrapper);

            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);

            processVos.add(processVo);
        }
        IPage<ProcessVo> pageMode = new Page<>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        pageMode.setRecords(processVos);

        return pageMode;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {

        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());

        IPage<ProcessVo> pageMadel = baseMapper.selectPage(pageParam, processQueryVo);

        return pageMadel;
    }

    // 驳回，结束流程
    private void endTask(String taskId) {
        // 根据任务id获取任务对象
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        // 获取bpmn模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 获取当前节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        // 获取结束节点
        List<EndEvent> endEvents = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if (CollectionUtils.isEmpty(endEvents)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEvents.get(0);

        //  临时保存当前节点的原始流动方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());

        // 清理当前的流动方向
        currentFlowNode.getOutgoingFlows().clear();

        // 创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

        // 修改当前节点流向
        List newFlowList = new ArrayList<>();
        newFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newFlowList);

        // 完成任务
        taskService.complete(taskId);
    }

    private List<Task> getCurrentTaskList(String id) {
        return taskService.createTaskQuery().processInstanceId(id).list();
    }
}
