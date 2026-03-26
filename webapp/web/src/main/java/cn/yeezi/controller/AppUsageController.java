package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.AppUsageRecordParam;
import cn.yeezi.model.param.AppUsageStatsParam;
import cn.yeezi.model.vo.AppUsageStatsVO;
import cn.yeezi.service.AppUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用使用埋点
 *
 * @author codex
 * @since 2026-03-23
 */
@Tag(name = "应用使用埋点")
@RestController
@RequestMapping("/appUsage")
@RequiredArgsConstructor
public class AppUsageController {

    private final AppUsageService appUsageService;

    @Operation(summary = "记录应用使用次数")
    @PostMapping("/record")
    public ResultVO<Void> record(@Valid @RequestBody AppUsageRecordParam param) {
        appUsageService.recordUsage(param);
        return ResultVO.success();
    }

    @Operation(summary = "查询应用使用统计")
    @GetMapping("/stats")
    public ResultVO<AppUsageStatsVO> stats(@Valid AppUsageStatsParam param) {
        return ResultVO.success(appUsageService.getStats(param));
    }
}
