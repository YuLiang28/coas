package org.example.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.common.result.Result;
import org.example.process.service.OaProcessService;
import org.example.process.service.OaProcessTypeService;
import org.example.vo.process.ProcessQueryVo;
import org.example.vo.process.ProcessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2023-04-08
 */
@Api(tags = "后端审批流管理")
@RestController
@RequestMapping("/admin/process")
@SuppressWarnings({"unchecked", "rawtypes"})
public class OaProcessController {
    @Autowired
    private OaProcessService oaProcessService;

    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @PreAuthorize("hasAuthority('bnt.process.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result pageQuery(
            @PathVariable Long page,
            @PathVariable Long limit,
            ProcessQueryVo processQueryVo) {
        Page<ProcessVo> pageParam = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.selectPage(pageParam, processQueryVo);
        return Result.ok(pageModel);
    }
}

