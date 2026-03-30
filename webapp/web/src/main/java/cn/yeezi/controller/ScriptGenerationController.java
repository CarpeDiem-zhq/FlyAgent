package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ScriptGenerateParam;
import cn.yeezi.model.vo.ScriptGenerateVO;
import cn.yeezi.service.ScriptGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "脚本生成")
@RestController
@RequestMapping("/script")
@RequiredArgsConstructor
public class ScriptGenerationController {

    private final ScriptGenerationService scriptGenerationService;

    @Operation(summary = "生成脚本")
    @PostMapping("/generate")
    public ResultVO<ScriptGenerateVO> generate(@Valid @RequestBody ScriptGenerateParam param) {
        return ResultVO.success(scriptGenerationService.generate(param));
    }
}
