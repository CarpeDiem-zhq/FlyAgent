package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.GlobalRuleListParam;
import cn.yeezi.model.param.GlobalRuleVersionSaveParam;
import cn.yeezi.model.param.ProductRuleListParam;
import cn.yeezi.model.param.ProductRuleVersionSaveParam;
import cn.yeezi.model.vo.RuleItemVO;
import cn.yeezi.service.RuleService;
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

@Tag(name = "规则")
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @Operation(summary = "全局规则列表")
    @GetMapping("/global/list")
    public ResultVO<List<RuleItemVO>> listGlobalRules(@Valid GlobalRuleListParam param) {
        List<RuleItemVO> result = ruleService.listGlobalRuleItems();
        return ResultVO.success(result);
    }

    @Operation(summary = "保存全局规则版本")
    @PostMapping("/global/saveVersion")
    public ResultVO<Void> saveGlobalRules(@Valid @RequestBody GlobalRuleVersionSaveParam param) {
        ruleService.saveGlobalRuleVersion(param.getRules());
        return ResultVO.success();
    }

    @Operation(summary = "产品规则列表")
    @GetMapping("/product/list")
    public ResultVO<List<RuleItemVO>> listProductRules(@Valid ProductRuleListParam param) {
        List<RuleItemVO> result = ruleService.listProductRuleItems(param.getProductId());
        return ResultVO.success(result);
    }

    @Operation(summary = "保存产品规则版本")
    @PostMapping("/product/saveVersion")
    public ResultVO<Void> saveProductRules(@Valid @RequestBody ProductRuleVersionSaveParam param) {
        ruleService.saveProductRuleVersion(param.getProductId(), param.getRules());
        return ResultVO.success();
    }
}