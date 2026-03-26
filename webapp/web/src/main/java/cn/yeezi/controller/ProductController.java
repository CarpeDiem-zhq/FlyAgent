package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.ProductCreateParam;
import cn.yeezi.model.param.ProductDetailParam;
import cn.yeezi.model.param.ProductListParam;
import cn.yeezi.model.param.ProductUpdateParam;
import cn.yeezi.model.vo.ProductVO;
import cn.yeezi.service.ProductService;
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

@Tag(name = "产品")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "产品列表")
    @GetMapping("/list")
    public ResultVO<List<ProductVO>> list(@Valid ProductListParam param) {
        return ResultVO.success(productService.listProducts(param));
    }

    @Operation(summary = "产品详情")
    @GetMapping("/detail")
    public ResultVO<ProductVO> detail(@Valid ProductDetailParam param) {
        return ResultVO.success(productService.getDetail(param.getProductId()));
    }

    @Operation(summary = "创建产品")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody ProductCreateParam param) {
        productService.createProduct(param);
        return ResultVO.success();
    }

    @Operation(summary = "更新产品")
    @PostMapping("/update")
    public ResultVO<Void> update(@Valid @RequestBody ProductUpdateParam param) {
        productService.updateProduct(param);
        return ResultVO.success();
    }
}
