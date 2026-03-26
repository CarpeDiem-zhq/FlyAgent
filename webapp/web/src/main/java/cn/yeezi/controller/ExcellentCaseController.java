package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ExcellentCaseListParam;
import cn.yeezi.model.vo.ScriptAssetVO;
import cn.yeezi.service.ScriptAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "优秀案例")
@RestController
@RequestMapping("/excellentCase")
@RequiredArgsConstructor
public class ExcellentCaseController {

    private final ScriptAssetService scriptAssetService;

    @Operation(summary = "优秀案例列表")
    @GetMapping("/list")
    public ResultVO<List<ScriptAssetVO>> list(@Valid ExcellentCaseListParam param) {
        List<ScriptAssetVO> result = scriptAssetService.listExcellentCases(param.getProductId());
        return ResultVO.success(result);
    }
}
