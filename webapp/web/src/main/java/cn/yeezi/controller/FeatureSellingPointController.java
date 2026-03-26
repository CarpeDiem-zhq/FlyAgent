package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.FeatureSellingPointCreateParam;
import cn.yeezi.model.param.FeatureSellingPointListParam;
import cn.yeezi.model.param.FeatureSellingPointUpdateParam;
import cn.yeezi.model.vo.FeatureSellingPointVO;
import cn.yeezi.service.FeatureSellingPointService;
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

@Tag(name = "核心卖点")
@RestController
@RequestMapping("/sellingPoint")
@RequiredArgsConstructor
public class FeatureSellingPointController {

    private final FeatureSellingPointService featureSellingPointService;

    @Operation(summary = "卖点列表")
    @GetMapping("/list")
    public ResultVO<List<FeatureSellingPointVO>> list(@Valid FeatureSellingPointListParam param) {
        return ResultVO.success(featureSellingPointService.listByFeature(param.getProductId(), param.getFeatureId()));
    }

    @Operation(summary = "创建卖点")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody FeatureSellingPointCreateParam param) {
        featureSellingPointService.create(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新卖点")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody FeatureSellingPointUpdateParam param) {
        featureSellingPointService.update(param);
        return ResultVO.success();
    }
}
