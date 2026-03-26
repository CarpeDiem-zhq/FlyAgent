package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.StrategyCreateParam;
import cn.yeezi.model.param.StrategyListParam;
import cn.yeezi.model.param.StrategyUpdateParam;
import cn.yeezi.model.vo.StrategyVO;
import cn.yeezi.service.StrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "策略")
@RestController
@RequestMapping("/strategy")
@RequiredArgsConstructor
public class StrategyController {

    private final StrategyService strategyService;

    @Operation(summary = "策略列表")
    @GetMapping("/list")
    public ResultVO<List<StrategyVO>> list(@Valid StrategyListParam param) {
        return ResultVO.success(strategyService.list(
                param.getProductId(), param.getFeatureId(), param.getCoreSellingPointId()));
    }

    @Operation(summary = "创建策略")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody StrategyCreateParam param) {
        strategyService.create(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新策略")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody StrategyUpdateParam param) {
        strategyService.update(param);
        return ResultVO.success();
    }
}
