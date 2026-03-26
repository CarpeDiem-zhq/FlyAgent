package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.repository.StrategyRepository;
import cn.yeezi.model.param.StrategyCreateParam;
import cn.yeezi.model.param.StrategyUpdateParam;
import cn.yeezi.model.vo.StrategyVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final StrategyRepository strategyRepository;
    private final ProductService productService;
    private final ProductFeatureService productFeatureService;
    private final FeatureSellingPointService featureSellingPointService;

    public StrategyEntity requireActiveStrategy(Long strategyId) {
        LambdaQueryWrapper<StrategyEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategyEntity::getId, strategyId);
        query.eq(StrategyEntity::getDel, false);
        StrategyEntity entity = strategyRepository.getOne(query);
        if (entity == null) {
            throw new BusinessException("策略不存在");
        }
        if (Boolean.FALSE.equals(entity.getEnabled())) {
            throw new BusinessException("策略已停用");
        }
        return entity;
    }

    public List<StrategyVO> list(Long productId, Long featureId, Long coreSellingPointId) {
        productService.requireActiveProduct(productId);
        LambdaQueryWrapper<StrategyEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategyEntity::getProductId, productId);
        query.eq(featureId != null, StrategyEntity::getFeatureId, featureId);
        query.eq(coreSellingPointId != null, StrategyEntity::getCoreSellingPointId, coreSellingPointId);
        query.eq(StrategyEntity::getDel, false);
        query.orderByAsc(StrategyEntity::getId);
        return strategyRepository.list(query).stream().map(this::toVO).collect(Collectors.toList());
    }

    @Transactional
    public void create(StrategyCreateParam param) {
        validateRelation(param.getProductId(), param.getFeatureId(), param.getCoreSellingPointId());
        StrategyEntity entity = new StrategyEntity()
                .setStrategyName(param.getStrategyName().trim())
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setCoreSellingPointId(param.getCoreSellingPointId())
                .setTargetAudience(param.getTargetAudience().trim())
                .setTargetScene(param.getTargetScene().trim())
                .setToneStyle(param.getToneStyle().trim())
                .setCallToAction(param.getCallToAction().trim())
                .setAdWords(param.getAdWords().trim())
                .setPromptId(param.getPromptId())
                .setEnabled(true)
                .setDel(false);
        strategyRepository.save(entity);
    }

    @Transactional
    public void update(StrategyUpdateParam param) {
        validateRelation(param.getProductId(), param.getFeatureId(), param.getCoreSellingPointId());
        StrategyEntity existing = strategyRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("策略不存在");
        }
        StrategyEntity update = new StrategyEntity()
                .setId(param.getId())
                .setStrategyName(param.getStrategyName().trim())
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setCoreSellingPointId(param.getCoreSellingPointId())
                .setTargetAudience(param.getTargetAudience().trim())
                .setTargetScene(param.getTargetScene().trim())
                .setToneStyle(param.getToneStyle().trim())
                .setCallToAction(param.getCallToAction().trim())
                .setAdWords(param.getAdWords().trim())
                .setPromptId(param.getPromptId());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        strategyRepository.updateById(update);
    }

    private void validateRelation(Long productId, Long featureId, Long coreSellingPointId) {
        productService.requireActiveProduct(productId);
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(featureId);
        if (!productId.equals(feature.getProductId())) {
            throw new BusinessException("功能不属于该产品");
        }
        FeatureSellingPointEntity sellingPoint = featureSellingPointService.requireActiveSellingPoint(coreSellingPointId);
        if (!productId.equals(sellingPoint.getProductId()) || !featureId.equals(sellingPoint.getFeatureId())) {
            throw new BusinessException("核心卖点不属于该功能");
        }
    }

    private StrategyVO toVO(StrategyEntity entity) {
        StrategyVO vo = new StrategyVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
