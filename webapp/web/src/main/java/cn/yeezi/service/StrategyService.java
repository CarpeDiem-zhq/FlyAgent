package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.entity.StrategySellingPointEntity;
import cn.yeezi.db.repository.StrategyRepository;
import cn.yeezi.db.repository.StrategySellingPointRepository;
import cn.yeezi.model.param.StrategyCreateParam;
import cn.yeezi.model.param.StrategyUpdateParam;
import cn.yeezi.model.vo.StrategyVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final StrategyRepository strategyRepository;
    private final StrategySellingPointRepository strategySellingPointRepository;
    private final ProductService productService;
    private final ProductFeatureService productFeatureService;
    private final FeatureSellingPointService featureSellingPointService;
    private final ObjectMapper objectMapper;

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

    public List<Long> listActiveSellingPointIds(Long strategyId) {
        LambdaQueryWrapper<StrategySellingPointEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategySellingPointEntity::getStrategyId, strategyId);
        query.eq(StrategySellingPointEntity::getDel, false);
        query.orderByAsc(StrategySellingPointEntity::getSellingPointId);
        return strategySellingPointRepository.list(query).stream()
                .map(StrategySellingPointEntity::getSellingPointId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<StrategyVO> list(Long productId, Long featureId, List<Long> coreSellingPointIds) {
        productService.requireActiveProduct(productId);
        List<Long> normalizedIds = normalizeIds(coreSellingPointIds);
        LambdaQueryWrapper<StrategyEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategyEntity::getProductId, productId);
        query.eq(featureId != null, StrategyEntity::getFeatureId, featureId);
        query.eq(StrategyEntity::getDel, false);
        query.orderByAsc(StrategyEntity::getId);
        List<StrategyEntity> entities = strategyRepository.list(query);
        if (entities.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<StrategySellingPointEntity>> relationMap = loadRelationMap(entities.stream()
                .map(StrategyEntity::getId)
                .collect(Collectors.toList()));
        return entities.stream()
                .filter(entity -> normalizedIds.isEmpty() || isExactMatch(relationMap.get(entity.getId()), normalizedIds))
                .map(entity -> toVO(entity, relationMap.get(entity.getId())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void create(StrategyCreateParam param) {
        ProductFeatureEntity feature = validateFeature(param.getProductId(), param.getFeatureId());
        List<FeatureSellingPointEntity> sellingPoints = validateSellingPoints(
                param.getProductId(), param.getFeatureId(), param.getCoreSellingPointIds());

        StrategyEntity entity = new StrategyEntity()
                .setStrategyName(param.getStrategyName().trim())
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setFeatureName(feature.getFeatureName())
                .setCoreSellingPointId(firstSellingPointId(sellingPoints))
                .setCoreSellingPointNames(writeJson(sellingPoints.stream()
                        .map(FeatureSellingPointEntity::getSellingPointName)
                        .collect(Collectors.toList())))
                .setTargetAudience(param.getTargetAudience().trim())
                .setTargetScene(param.getTargetScene().trim())
                .setToneStyle(param.getToneStyle().trim())
                .setCallToAction(param.getCallToAction().trim())
                .setAdWords(param.getAdWords().trim())
                .setEnabled(true)
                .setDel(false);
        strategyRepository.save(entity);
        replaceRelations(entity.getId(), param.getProductId(), param.getFeatureId(), sellingPoints);
    }

    @Transactional
    public void update(StrategyUpdateParam param) {
        ProductFeatureEntity feature = validateFeature(param.getProductId(), param.getFeatureId());
        List<FeatureSellingPointEntity> sellingPoints = validateSellingPoints(
                param.getProductId(), param.getFeatureId(), param.getCoreSellingPointIds());
        StrategyEntity existing = strategyRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("策略不存在");
        }

        StrategyEntity update = new StrategyEntity()
                .setId(param.getId())
                .setStrategyName(param.getStrategyName().trim())
                .setProductId(param.getProductId())
                .setFeatureId(param.getFeatureId())
                .setFeatureName(feature.getFeatureName())
                .setCoreSellingPointId(firstSellingPointId(sellingPoints))
                .setCoreSellingPointNames(writeJson(sellingPoints.stream()
                        .map(FeatureSellingPointEntity::getSellingPointName)
                        .collect(Collectors.toList())))
                .setTargetAudience(param.getTargetAudience().trim())
                .setTargetScene(param.getTargetScene().trim())
                .setToneStyle(param.getToneStyle().trim())
                .setCallToAction(param.getCallToAction().trim())
                .setAdWords(param.getAdWords().trim());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        strategyRepository.updateById(update);
        replaceRelations(param.getId(), param.getProductId(), param.getFeatureId(), sellingPoints);
    }

    private ProductFeatureEntity validateFeature(Long productId, Long featureId) {
        productService.requireActiveProduct(productId);
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(featureId);
        if (!productId.equals(feature.getProductId())) {
            throw new BusinessException("功能不属于该产品");
        }
        return feature;
    }

    private List<FeatureSellingPointEntity> validateSellingPoints(Long productId, Long featureId, List<Long> coreSellingPointIds) {
        List<Long> normalizedIds = normalizeIds(coreSellingPointIds);
        if (normalizedIds.isEmpty()) {
            throw new BusinessException("至少选择一个核心卖点");
        }
        List<FeatureSellingPointEntity> result = new ArrayList<>();
        for (Long sellingPointId : normalizedIds) {
            FeatureSellingPointEntity sellingPoint = featureSellingPointService.requireActiveSellingPoint(sellingPointId);
            if (!productId.equals(sellingPoint.getProductId()) || !featureId.equals(sellingPoint.getFeatureId())) {
                throw new BusinessException("核心卖点不属于该功能");
            }
            result.add(sellingPoint);
        }
        return result;
    }

    private void replaceRelations(
            Long strategyId,
            Long productId,
            Long featureId,
            List<FeatureSellingPointEntity> sellingPoints
    ) {
        LambdaQueryWrapper<StrategySellingPointEntity> removeQuery = new LambdaQueryWrapper<>();
        removeQuery.eq(StrategySellingPointEntity::getStrategyId, strategyId);
        removeQuery.eq(StrategySellingPointEntity::getDel, false);
        strategySellingPointRepository.remove(removeQuery);

        List<StrategySellingPointEntity> relations = sellingPoints.stream()
                .map(item -> new StrategySellingPointEntity()
                        .setStrategyId(strategyId)
                        .setProductId(productId)
                        .setFeatureId(featureId)
                        .setSellingPointId(item.getId())
                        .setSellingPointName(item.getSellingPointName())
                        .setDel(false))
                .collect(Collectors.toList());
        strategySellingPointRepository.saveBatch(relations);
    }

    private Map<Long, List<StrategySellingPointEntity>> loadRelationMap(Collection<Long> strategyIds) {
        if (strategyIds == null || strategyIds.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<StrategySellingPointEntity> relationQuery = new LambdaQueryWrapper<>();
        relationQuery.in(StrategySellingPointEntity::getStrategyId, strategyIds);
        relationQuery.eq(StrategySellingPointEntity::getDel, false);
        relationQuery.orderByAsc(StrategySellingPointEntity::getSellingPointId);
        return strategySellingPointRepository.list(relationQuery).stream()
                .collect(Collectors.groupingBy(
                        StrategySellingPointEntity::getStrategyId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private boolean isExactMatch(List<StrategySellingPointEntity> relations, List<Long> normalizedIds) {
        List<Long> relationIds = relations == null
                ? Collections.emptyList()
                : relations.stream()
                        .map(StrategySellingPointEntity::getSellingPointId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());
        return relationIds.equals(normalizedIds);
    }

    private StrategyVO toVO(StrategyEntity entity, List<StrategySellingPointEntity> relations) {
        StrategyVO vo = new StrategyVO();
        vo.setId(entity.getId());
        vo.setStrategyName(entity.getStrategyName());
        vo.setProductId(entity.getProductId());
        vo.setFeatureId(entity.getFeatureId());
        vo.setFeatureName(entity.getFeatureName());
        vo.setCoreSellingPointIds(relations == null
                ? Collections.emptyList()
                : relations.stream().map(StrategySellingPointEntity::getSellingPointId).collect(Collectors.toList()));
        vo.setCoreSellingPointNames(readStringList(entity.getCoreSellingPointNames(), relations));
        vo.setTargetAudience(entity.getTargetAudience());
        vo.setTargetScene(entity.getTargetScene());
        vo.setToneStyle(entity.getToneStyle());
        vo.setCallToAction(entity.getCallToAction());
        vo.setAdWords(entity.getAdWords());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }

    private List<Long> normalizeIds(List<Long> ids) {
        if (ids == null) {
            return Collections.emptyList();
        }
        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    private Long firstSellingPointId(List<FeatureSellingPointEntity> sellingPoints) {
        return sellingPoints.isEmpty() ? null : sellingPoints.get(0).getId();
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new BusinessException("策略快照序列化失败");
        }
    }

    private List<String> readStringList(String json, List<StrategySellingPointEntity> relations) {
        if (json != null && !json.isBlank()) {
            try {
                return objectMapper.readValue(json, STRING_LIST_TYPE);
            } catch (Exception ignored) {
            }
        }
        if (relations == null) {
            return Collections.emptyList();
        }
        return relations.stream().map(StrategySellingPointEntity::getSellingPointName).collect(Collectors.toList());
    }
}
