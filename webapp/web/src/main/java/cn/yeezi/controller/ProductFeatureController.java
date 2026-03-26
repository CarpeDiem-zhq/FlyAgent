package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ProductFeatureCreateParam;
import cn.yeezi.model.param.ProductFeatureListParam;
import cn.yeezi.model.param.ProductFeatureUpdateParam;
import cn.yeezi.model.vo.ProductFeatureVO;
import cn.yeezi.service.ProductFeatureService;
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

@Tag(name = "产品功能")
@RestController
@RequestMapping("/productFeature")
@RequiredArgsConstructor
public class ProductFeatureController {

    private final ProductFeatureService productFeatureService;

    @Operation(summary = "功能列表")
    @GetMapping("/list")
    public ResultVO<List<ProductFeatureVO>> list(@Valid ProductFeatureListParam param) {
        return ResultVO.success(productFeatureService.listByProduct(param.getProductId()));
    }

    @Operation(summary = "创建功能")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody ProductFeatureCreateParam param) {
        productFeatureService.create(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新功能")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody ProductFeatureUpdateParam param) {
        productFeatureService.update(param);
        return ResultVO.success();
    }
}
