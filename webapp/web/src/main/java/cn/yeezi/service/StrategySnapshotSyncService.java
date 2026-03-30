package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.entity.StrategySellingPointEntity;
import cn.yeezi.db.repository.StrategyRepository;
import cn.yeezi.db.repository.StrategySellingPointRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StrategySnapshotSyncService {

    private final StrategyRepository strategyRepository;
    private final StrategySellingPointRepository strategySellingPointRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void syncFeatureNameSnapshot(Long productId, Long featureId, String featureName) {
        LambdaQueryWrapper<StrategyEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategyEntity::getProductId, productId);
        query.eq(StrategyEntity::getFeatureId, featureId);
        query.eq(StrategyEntity::getDel, false);
        List<StrategyEntity> strategies = strategyRepository.list(query);
        for (StrategyEntity strategy : strategies) {
            StrategyEntity update = new StrategyEntity()
                    .setId(strategy.getId())
                    .setFeatureName(featureName);
            strategyRepository.updateById(update);
        }
    }

    @Transactional
    public void syncSellingPointNameSnapshot(Long sellingPointId, String sellingPointName) {
        LambdaQueryWrapper<StrategySellingPointEntity> relationQuery = new LambdaQueryWrapper<>();
        relationQuery.eq(StrategySellingPointEntity::getSellingPointId, sellingPointId);
        relationQuery.eq(StrategySellingPointEntity::getDel, false);
        List<StrategySellingPointEntity> relations = strategySellingPointRepository.list(relationQuery);
        if (relations.isEmpty()) {
            return;
        }
        Set<Long> strategyIds = new LinkedHashSet<>();
        for (StrategySellingPointEntity relation : relations) {
            strategyIds.add(relation.getStrategyId());
            StrategySellingPointEntity update = new StrategySellingPointEntity()
                    .setId(relation.getId())
                    .setSellingPointName(sellingPointName);
            strategySellingPointRepository.updateById(update);
        }
        for (Long strategyId : strategyIds) {
            StrategyEntity update = new StrategyEntity()
                    .setId(strategyId)
                    .setCoreSellingPointNames(buildSellingPointNamesJson(strategyId));
            strategyRepository.updateById(update);
        }
    }

    private String buildSellingPointNamesJson(Long strategyId) {
        LambdaQueryWrapper<StrategySellingPointEntity> query = new LambdaQueryWrapper<>();
        query.eq(StrategySellingPointEntity::getStrategyId, strategyId);
        query.eq(StrategySellingPointEntity::getDel, false);
        query.orderByAsc(StrategySellingPointEntity::getSellingPointId);
        List<String> names = strategySellingPointRepository.list(query).stream()
                .map(StrategySellingPointEntity::getSellingPointName)
                .collect(Collectors.toList());
        try {
            return objectMapper.writeValueAsString(names);
        } catch (Exception e) {
            throw new BusinessException("策略快照序列化失败");
        }
    }
}
