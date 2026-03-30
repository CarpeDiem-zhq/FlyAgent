package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.repository.FeatureSellingPointRepository;
import cn.yeezi.model.param.FeatureSellingPointCreateParam;
import cn.yeezi.model.param.FeatureSellingPointUpdateParam;
import cn.yeezi.model.vo.FeatureSellingPointVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeatureSellingPointService {

    private final FeatureSellingPointRepository featureSellingPointRepository;
    private final ProductService productService;
    private final ProductFeatureService productFeatureService;
    private final StrategySnapshotSyncService strategySnapshotSyncService;

    public FeatureSellingPointEntity requireActiveSellingPoint(Long sellingPointId) {
        LambdaQueryWrapper<FeatureSellingPointEntity> query = new LambdaQueryWrapper<>();
        query.eq(FeatureSellingPointEntity::getId, sellingPointId);
        query.eq(FeatureSellingPointEntity::getDel, false);
        FeatureSellingPointEntity entity = featureSellingPointRepository.getOne(query);
        if (entity == null) {
            throw new BusinessException("核心卖点不存在");
        }
        if (Boolean.FALSE.equals(entity.getEnabled())) {
            throw new BusinessException("核心卖点已停用");
        }
        return entity;
    }

    public List<FeatureSellingPointVO> listByFeature(Long productId, Long featureId) {
        productService.requireActiveProduct(productId);
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(featureId);
        if (!productId.equals(feature.getProductId())) {
            throw new BusinessException("功能不属于该产品");
        }
        LambdaQueryWrapper<FeatureSellingPointEntity> query = new LambdaQueryWrapper<>();
        query.eq(FeatureSellingPointEntity::getProductId, productId);
        query.eq(FeatureSellingPointEntity::getFeatureId, featureId);
        query.eq(FeatureSellingPointEntity::getDel, false);
        query.orderByAsc(FeatureSellingPointEntity::getId);
        return featureSellingPointRepository.list(query).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public void create(FeatureSellingPointCreateParam param) {
        productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(param.getFeatureId());
        if (!param.getProductId().equals(feature.getProductId())) {
            throw new BusinessException("功能不属于该产品");
        }
        FeatureSellingPointEntity entity = new FeatureSellingPointEntity()
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setSellingPointName(param.getSellingPointName().trim())
                .setEnabled(true)
                .setDel(false);
        featureSellingPointRepository.save(entity);
    }

    @Transactional
    public void update(FeatureSellingPointUpdateParam param) {
        productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(param.getFeatureId());
        if (!param.getProductId().equals(feature.getProductId())) {
            throw new BusinessException("功能不属于该产品");
        }
        FeatureSellingPointEntity existing = featureSellingPointRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("核心卖点不存在");
        }
        FeatureSellingPointEntity update = new FeatureSellingPointEntity()
                .setId(param.getId())
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setSellingPointName(param.getSellingPointName().trim());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        featureSellingPointRepository.updateById(update);
        strategySnapshotSyncService.syncSellingPointNameSnapshot(param.getId(), param.getSellingPointName().trim());
    }

    private FeatureSellingPointVO toVO(FeatureSellingPointEntity entity) {
        FeatureSellingPointVO vo = new FeatureSellingPointVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
