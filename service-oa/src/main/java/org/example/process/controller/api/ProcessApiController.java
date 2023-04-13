package org.example.process.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.example.auth.service.SysUserService;
import org.example.common.result.Result;
import org.example.model.process.Process;
import org.example.model.process.ProcessTemplate;
import org.example.model.process.ProcessType;
import org.example.process.service.OaProcessService;
import org.example.process.service.OaProcessTemplateService;
import org.example.process.service.OaProcessTypeService;
import org.example.vo.process.ApprovalVo;
import org.example.vo.process.ProcessFormVo;
import org.example.vo.process.ProcessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "前端审批流管理")
@RestController
@RequestMapping(value = "/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private OaProcessService oaProcessService;
    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;
    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @ApiOperation(value = "获取全部审批分类及模板")
    @GetMapping("findProcessType")
    public Result findProcessType() {
        List<ProcessType> list = oaProcessTypeService.findProcessType();
        return Result.ok(list);
    }

    @ApiOperation(value = "获取审批模板")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result getProcessTemplate(@PathVariable Long processTemplateId) {
        ProcessTemplate processTemplate = oaProcessTemplateService.getById(processTemplateId);
        return Result.ok(processTemplate);
    }

    @ApiOperation(value = "启动流程实例")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo) {
        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "待处理页面")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParm = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.findPending(pageParm);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "审批确认流程")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) {
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation(value = "获取审批详情")
    @GetMapping("show/{id}")
    public Result showProcessInfo(@PathVariable Long id) {
        return Result.ok(oaProcessService.showProcessInfo(id));
    }

    @ApiOperation(value = "已处理列表")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.findProcessed(pageParam);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "已发起列表")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findStarted(pageParam));
    }

    @ApiOperation(value = "获取当前用户基本信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser() {
        Map<String, Object> user = sysUserService.getCurrentUser();
        return Result.ok(user);
    }

}
