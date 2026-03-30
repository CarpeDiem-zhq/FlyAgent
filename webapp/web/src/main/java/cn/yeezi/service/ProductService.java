package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.repository.ProductRepository;
import cn.yeezi.model.param.ProductCreateParam;
import cn.yeezi.model.param.ProductListParam;
import cn.yeezi.model.param.ProductUpdateParam;
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
public class ProductService {

    private final ProductRepository productRepository;

    public ProductEntity requireActiveProduct(Long productId) {
        if (productId == null) {
            throw new BusinessException("产品id不能为空");
        }
        LambdaQueryWrapper<ProductEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductEntity::getId, productId);
        query.eq(ProductEntity::getDel, false);
        ProductEntity product = productRepository.getOne(query);
        if (product == null) {
            throw new BusinessException("产品不存在或已删除");
        }
        if (Boolean.FALSE.equals(product.getEnabled())) {
            throw new BusinessException("产品已停用");
        }
        return product;
    }

    public List<ProductVO> listProducts(ProductListParam param) {
        String productName = param == null ? null : param.getProductName();
        String keyword = productName == null ? null : productName.trim();
        LambdaQueryWrapper<ProductEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductEntity::getDel, false);
        query.like(keyword != null && !keyword.isBlank(), ProductEntity::getProductName, keyword);
        query.orderByAsc(ProductEntity::getId);
        return productRepository.list(query).stream().map(this::toVO).collect(Collectors.toList());
    }

    public ProductVO getDetail(Long productId) {
        return toVO(requireActiveProduct(productId));
    }

    @Transactional
    public void createProduct(ProductCreateParam param) {
        ProductEntity entity = new ProductEntity();
        entity.setProductName(param.getProductName().trim());
        entity.setProductDesc(param.getProductDesc());
        entity.setEnabled(true);
        entity.setDel(false);
        productRepository.save(entity);
    }

    @Transactional
    public void updateProduct(ProductUpdateParam param) {
        ProductEntity existing = productRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("产品不存在或已删除");
        }
        ProductEntity update = new ProductEntity();
        update.setId(param.getId());
        update.setProductName(param.getProductName().trim());
        update.setProductDesc(param.getProductDesc());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        productRepository.updateById(update);
    }

    private ProductVO toVO(ProductEntity entity) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
