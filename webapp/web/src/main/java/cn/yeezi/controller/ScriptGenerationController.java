package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ExcellentScriptStructAddParam;
import cn.yeezi.model.param.ExcellentScriptStructPageParam;
import cn.yeezi.model.param.ScriptGenerateParam;
import cn.yeezi.model.param.ScriptRerunParam;
import cn.yeezi.model.vo.ExcellentScriptStructAddVO;
import cn.yeezi.model.vo.ExcellentScriptStructRecordVO;
import cn.yeezi.model.vo.ScriptGenerateVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import cn.yeezi.service.ExcellentScriptStructService;
import cn.yeezi.service.ScriptGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ExcellentScriptStructService excellentScriptStructService;

    @Operation(summary = "生成脚本")
    @PostMapping("/generate")
    public ResultVO<List<ScriptGenerateVO>> generate(@Valid @RequestBody ScriptGenerateParam param) {
        List<ScriptGenerateVO> result = scriptGenerationService.generate(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "回炉生成")
    @PostMapping("/rerun")
    public ResultVO<ScriptGenerateVO> rerun(@Valid @RequestBody ScriptRerunParam param) {
        ScriptGenerateVO result = scriptGenerationService.rerun(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "增加优秀脚本并异步同步知识库")
    @PostMapping("/excellent/add")
    public ResultVO<ExcellentScriptStructAddVO> addExcellentScript(@Valid @RequestBody ExcellentScriptStructAddParam param) {
        ExcellentScriptStructAddVO result = excellentScriptStructService.addExcellentScript(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "优秀脚本结构分页查询")
    @GetMapping("/excellent/list")
    public ResultVO<IPage<ExcellentScriptStructRecordVO>> listExcellentScript(@Valid ExcellentScriptStructPageParam param) {
        IPage<ExcellentScriptStructRecordVO> result = excellentScriptStructService.pageList(param);
        return ResultVO.success(result);
    }
}
