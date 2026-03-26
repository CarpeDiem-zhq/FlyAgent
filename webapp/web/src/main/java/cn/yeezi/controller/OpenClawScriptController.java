package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.OpenClawScriptGenerateParam;
import cn.yeezi.model.param.OpenClawScriptResolveParam;
import cn.yeezi.model.vo.OpenClawScriptGenerateVO;
import cn.yeezi.model.vo.OpenClawScriptResolveVO;
import cn.yeezi.service.OpenClawScriptFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "OpenClaw 脚本")
@RestController
@RequestMapping("/internal/openclaw/script")
@RequiredArgsConstructor
public class OpenClawScriptController {

    private final OpenClawScriptFacadeService openClawScriptFacadeService;

    @Operation(summary = "补参解析")
    @PostMapping("/resolve")
    public ResultVO<OpenClawScriptResolveVO> resolve(@Valid @RequestBody OpenClawScriptResolveParam param) {
        return ResultVO.success(openClawScriptFacadeService.resolve(param));
    }

    @Operation(summary = "脚本生成")
    @PostMapping("/generate")
    public ResultVO<OpenClawScriptGenerateVO> generate(@Valid @RequestBody OpenClawScriptGenerateParam param) {
        return ResultVO.success(openClawScriptFacadeService.generate(param));
    }
}
