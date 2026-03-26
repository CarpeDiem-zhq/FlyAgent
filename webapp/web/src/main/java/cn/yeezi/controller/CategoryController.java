package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.CategoryCreateParam;
import cn.yeezi.model.param.CategoryListParam;
import cn.yeezi.model.param.CategoryProductListParam;
import cn.yeezi.model.param.ProductDetailParam;
import cn.yeezi.model.vo.CategoryVO;
import cn.yeezi.model.vo.ProductVO;
import cn.yeezi.service.CategoryService;
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

@Tag(name = "类目")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @Operation(summary = "类目列表")
    @GetMapping("/list")
    public ResultVO<List<CategoryVO>> list(@Valid CategoryListParam param) {
        List<CategoryVO> result = categoryService.listCategories(param);
        return ResultVO.success(result);
    }

    @Operation(summary = "类目下产品列表")
    @GetMapping("/products")
    public ResultVO<List<ProductVO>> listProducts(@Valid CategoryProductListParam param) {
        List<ProductVO> result = categoryService.listProductsByCategory(param.getCategoryId());
        return ResultVO.success(result);
    }

    @Operation(summary = "产品详情")
    @GetMapping("/productDetail")
    public ResultVO<ProductVO> productDetail(@Valid ProductDetailParam param) {
        ProductVO result = productService.getDetail(param.getProductId());
        return ResultVO.success(result);
    }

    @Operation(summary = "创建类目")
    @PostMapping("/create")
    public ResultVO<Void> create(@Valid @RequestBody CategoryCreateParam param) {
        categoryService.createCategory(param);
        return ResultVO.success();
    }
}
