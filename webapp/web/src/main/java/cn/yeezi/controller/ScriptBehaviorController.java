package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ScriptCopyParam;
import cn.yeezi.model.param.ScriptFavoriteParam;
import cn.yeezi.model.param.ScriptFeedbackParam;
import cn.yeezi.model.param.ScriptLikeParam;
import cn.yeezi.model.vo.ScriptGenerateVO;
import cn.yeezi.service.ScriptAssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "脚本行为")
@RestController
@RequestMapping("/scriptBehavior")
@RequiredArgsConstructor
public class ScriptBehaviorController {

    private final ScriptAssetService scriptAssetService;

    @Operation(summary = "复制脚本")
    @PostMapping("/copy")
    public ResultVO<Void> copy(@Valid @RequestBody ScriptCopyParam param) {
        scriptAssetService.recordCopy(param.getAssetId());
        return ResultVO.success();
    }

    @Operation(summary = "点赞脚本")
    @PostMapping("/like")
    public ResultVO<Void> like(@Valid @RequestBody ScriptLikeParam param) {
        scriptAssetService.recordLike(param.getAssetId());
        return ResultVO.success();
    }

    @Operation(summary = "收藏脚本")
    @PostMapping("/favorite")
    public ResultVO<Void> favorite(@Valid @RequestBody ScriptFavoriteParam param) {
        scriptAssetService.recordFavorite(param.getAssetId());
        return ResultVO.success();
    }

    @Operation(summary = "提交反馈")
    @PostMapping("/feedback")
    public ResultVO<ScriptGenerateVO> feedback(@Valid @RequestBody ScriptFeedbackParam param) {
        ScriptGenerateVO result = scriptAssetService.handleFeedback(param);
        return ResultVO.success(result);
    }
}
