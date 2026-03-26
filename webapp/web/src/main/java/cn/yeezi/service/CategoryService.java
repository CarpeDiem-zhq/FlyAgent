package cn.yeezi.service;

import cn.hutool.core.util.StrUtil;
import cn.yeezi.db.entity.CategoryEntity;
import cn.yeezi.db.repository.CategoryRepository;
import cn.yeezi.model.param.CategoryCreateParam;
import cn.yeezi.model.param.CategoryListParam;
import cn.yeezi.model.vo.CategoryVO;
import cn.yeezi.model.vo.ProductVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    public List<CategoryVO> listCategories(CategoryListParam param) {
        LambdaQueryWrapper<CategoryEntity> query = new LambdaQueryWrapper<>();
        if (param != null && StrUtil.isNotBlank(param.getKeyword())) {
            query.like(CategoryEntity::getCategoryName, param.getKeyword().trim());
        }
        query.eq(CategoryEntity::getDel, false);
        query.orderByAsc(CategoryEntity::getSortOrder, CategoryEntity::getId);
        return categoryRepository.list(query).stream()
                .map(entity -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<ProductVO> listProductsByCategory(Long categoryId) {
        return productService.listByCategory(categoryId);
    }

    @Transactional
    public void createCategory(CategoryCreateParam param) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCategoryName(param.getCategoryName());
        entity.setSortOrder(param.getSortOrder());
        entity.setDel(false);
        categoryRepository.save(entity);
    }
}
