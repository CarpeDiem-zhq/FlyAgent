package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.repository.ProductFeatureRepository;
import cn.yeezi.model.param.ProductFeatureCreateParam;
import cn.yeezi.model.param.ProductFeatureUpdateParam;
import cn.yeezi.model.vo.ProductFeatureVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductFeatureService {

    private final ProductFeatureRepository productFeatureRepository;
    private final ProductService productService;
    private final StrategySnapshotSyncService strategySnapshotSyncService;

    public ProductFeatureEntity requireActiveFeature(Long featureId) {
        LambdaQueryWrapper<ProductFeatureEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductFeatureEntity::getId, featureId);
        query.eq(ProductFeatureEntity::getDel, false);
        ProductFeatureEntity entity = productFeatureRepository.getOne(query);
        if (entity == null) {
            throw new BusinessException("产品功能不存在");
        }
        if (Boolean.FALSE.equals(entity.getEnabled())) {
            throw new BusinessException("产品功能已停用");
        }
        return entity;
    }

    public List<ProductFeatureVO> listByProduct(Long productId) {
        productService.requireActiveProduct(productId);
        LambdaQueryWrapper<ProductFeatureEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductFeatureEntity::getProductId, productId);
        query.eq(ProductFeatureEntity::getDel, false);
        query.orderByAsc(ProductFeatureEntity::getId);
        return productFeatureRepository.list(query).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public void create(ProductFeatureCreateParam param) {
        productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity entity = new ProductFeatureEntity()
                .setProductId(param.getProductId())
                .setFeatureName(param.getFeatureName().trim())
                .setEnabled(true)
                .setDel(false);
        productFeatureRepository.save(entity);
    }

    @Transactional
    public void update(ProductFeatureUpdateParam param) {
        productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity existing = productFeatureRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("产品功能不存在");
        }
        ProductFeatureEntity update = new ProductFeatureEntity()
                .setId(param.getId())
                .setProductId(param.getProductId())
                .setFeatureName(param.getFeatureName().trim());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        productFeatureRepository.updateById(update);
        strategySnapshotSyncService.syncFeatureNameSnapshot(param.getProductId(), param.getId(), param.getFeatureName().trim());
    }

    private ProductFeatureVO toVO(ProductFeatureEntity entity) {
        ProductFeatureVO vo = new ProductFeatureVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
