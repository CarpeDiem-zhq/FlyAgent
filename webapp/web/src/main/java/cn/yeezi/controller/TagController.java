package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.TagGroupCreateParam;
import cn.yeezi.model.param.TagCoreSellingPointListParam;
import cn.yeezi.model.param.TagFeatureListParam;
import cn.yeezi.model.param.TagGroupDisableParam;
import cn.yeezi.model.param.TagGroupListParam;
import cn.yeezi.model.param.TagGroupUpdateParam;
import cn.yeezi.model.param.TagItemCreateParam;
import cn.yeezi.model.param.TagItemListParam;
import cn.yeezi.model.param.TagItemStatusParam;
import cn.yeezi.model.param.TagItemUpdateParam;
import cn.yeezi.model.vo.TagFeatureVO;
import cn.yeezi.model.vo.TagGroupVO;
import cn.yeezi.model.vo.TagItemVO;
import cn.yeezi.service.TagService;
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

@Tag(name = "标签")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "标签组及标签")
    @GetMapping("/groups")
    public ResultVO<List<TagGroupVO>> listGroups(@Valid TagGroupListParam param) {
        List<TagGroupVO> result = tagService.listTagGroups(param.getProductId());
        return ResultVO.success(result);
    }

    @Operation(summary = "标签项列表")
    @GetMapping("/items")
    public ResultVO<List<TagItemVO>> listItems(@Valid TagItemListParam param) {
        List<TagItemVO> result = tagService.listTagItems(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "根据产品功能查询核心卖点和优势")
    @GetMapping("/item/coreSellingPoints")
    public ResultVO<List<TagItemVO>> listCoreSellingPoints(@Valid TagCoreSellingPointListParam param) {
        List<TagItemVO> result = tagService.listCoreSellingPointsByFeature(param.getProductId(), param.getFeatureItemId());
        return ResultVO.success(result);
    }

    @Operation(summary = "产品功能列表")
    @GetMapping("/item/features")
    public ResultVO<List<TagFeatureVO>> listFeatures(@Valid TagFeatureListParam param) {
        List<TagFeatureVO> result = tagService.listProductFeatures(param.getProductId());
        return ResultVO.success(result);
    }

    /*@Operation(summary = "创建标签组")
    @PostMapping("/group/create")
    public ResultVO<Void> createGroup(@Valid @RequestBody TagGroupCreateParam param) {
        tagService.createTagGroup(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新标签组")
    @PostMapping("/group/update")
    public ResultVO<Void> updateGroup(@Valid @RequestBody TagGroupUpdateParam param) {
        tagService.updateTagGroup(param);
        return ResultVO.success();
    }

    @Operation(summary = "停用标签组")
    @PostMapping("/group/disable")
    public ResultVO<Void> disableGroup(@Valid @RequestBody TagGroupDisableParam param) {
        tagService.disableTagGroup(param);
        return ResultVO.success();
    }*/

    @Operation(summary = "创建标签项")
    @PostMapping("/item/create")
    public ResultVO<Void> createItem(@Valid @RequestBody TagItemCreateParam param) {
        tagService.createTagItem(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新标签项")
    @PostMapping("/item/update")
    public ResultVO<Void> updateItem(@Valid @RequestBody TagItemUpdateParam param) {
        tagService.updateTagItem(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新标签项状态")
    @PostMapping("/item/status")
    public ResultVO<Void> updateItemStatus(@Valid @RequestBody TagItemStatusParam param) {
        tagService.updateTagItemStatus(param);
        return ResultVO.success();
    }
}
