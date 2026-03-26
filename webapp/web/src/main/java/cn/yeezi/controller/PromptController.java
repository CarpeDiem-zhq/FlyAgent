package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.PromptCreateParam;
import cn.yeezi.model.param.PromptDetailParam;
import cn.yeezi.model.param.PromptListParam;
import cn.yeezi.model.param.PromptUpdateParam;
import cn.yeezi.model.vo.PromptVO;
import cn.yeezi.service.PromptService;
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

@Tag(name = "提示词")
@RestController
@RequestMapping("/prompt")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    @Operation(summary = "提示词列表")
    @GetMapping("/list")
    public ResultVO<List<PromptVO>> list(@Valid PromptListParam param) {
        List<PromptVO> result = promptService.listPrompts(param.getProductId(), param.getEnabled());
        return ResultVO.success(result);
    }

    @Operation(summary = "提示词详情")
    @GetMapping("/detail")
    public ResultVO<PromptVO> detail(@Valid PromptDetailParam param) {
        PromptVO result = promptService.getPromptDetail(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "创建提示词")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody PromptCreateParam param) {
        promptService.createPrompt(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新提示词")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody PromptUpdateParam param) {
        promptService.updatePrompt(param);
        return ResultVO.success();
    }
}