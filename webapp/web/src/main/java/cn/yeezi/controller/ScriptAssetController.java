package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ScriptAssetDetailParam;
import cn.yeezi.model.param.ScriptAssetMyPageParam;
import cn.yeezi.model.param.ScriptAssetPageParam;
import cn.yeezi.model.param.ScriptAssetSaveParam;
import cn.yeezi.model.param.ScriptAssetUpdateParam;
import cn.yeezi.model.vo.ScriptAssetDetailVO;
import cn.yeezi.model.vo.ScriptAssetVO;
import cn.yeezi.service.ScriptAssetService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "脚本资产")
@RestController
@RequestMapping("/scriptAsset")
@RequiredArgsConstructor
public class ScriptAssetController {

    private final ScriptAssetService scriptAssetService;

    @Operation(summary = "我的脚本")
    @GetMapping("/my")
    public ResultVO<IPage<ScriptAssetVO>> myList(@Valid ScriptAssetMyPageParam param) {
        return ResultVO.success(scriptAssetService.listMyAssets(param));
    }

    @Operation(summary = "全部脚本")
    @GetMapping("/all")
    public ResultVO<IPage<ScriptAssetVO>> allList(@Valid ScriptAssetPageParam param) {
        return ResultVO.success(scriptAssetService.listAllAssets(param));
    }

    @Operation(summary = "脚本详情")
    @GetMapping("/detail")
    public ResultVO<ScriptAssetDetailVO> detail(@Valid ScriptAssetDetailParam param) {
        return ResultVO.success(scriptAssetService.getDetail(param));
    }

    @Operation(summary = "保存脚本资产")
    @PostMapping("/save")
    public ResultVO<Long> save(@Valid @RequestBody ScriptAssetSaveParam param) {
        Long assetId = scriptAssetService.saveScriptAsset(param);
        return ResultVO.success(assetId);
    }

    @Operation(summary = "更新脚本资产")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody ScriptAssetUpdateParam param) {
        scriptAssetService.updateScriptAsset(param);
        return ResultVO.success();
    }
}
