package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.config.ScriptGenerationStrategyProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 开头策略分配服务
 *
 * @author codex
 * @since 2026-03-12
 */
@Service
@RequiredArgsConstructor
public class OpeningStrategyAssignmentService {

    private final ScriptGenerationStrategyProperties scriptGenerationStrategyProperties;

    public List<OpeningStrategy> assignStrategies(int adNumber) {
        if (adNumber <= 0) {
            throw new BusinessException("广告脚本生成条数需大于0");
        }
        List<OpeningStrategy> strategyPool = loadStrategyPool();
        if (strategyPool.isEmpty()) {
            throw new BusinessException("脚本开头策略未配置");
        }
        if (strategyPool.size() >= adNumber) {
            List<OpeningStrategy> shuffled = new ArrayList<>(strategyPool);
            Collections.shuffle(shuffled);
            return new ArrayList<>(shuffled.subList(0, adNumber));
        }
        List<OpeningStrategy> result = new ArrayList<>(adNumber);
        while (result.size() < adNumber) {
            List<OpeningStrategy> shuffled = new ArrayList<>(strategyPool);
            Collections.shuffle(shuffled);
            for (OpeningStrategy openingStrategy : shuffled) {
                result.add(openingStrategy);
                if (result.size() >= adNumber) {
                    break;
                }
            }
        }
        return result;
    }

    private List<OpeningStrategy> loadStrategyPool() {
        List<ScriptGenerationStrategyProperties.OpeningStrategyItem> configuredStrategies =
                scriptGenerationStrategyProperties.getOpeningStrategies();
        if (configuredStrategies == null || configuredStrategies.isEmpty()) {
            return List.of();
        }
        List<OpeningStrategy> strategyPool = new ArrayList<>(configuredStrategies.size());
        Set<String> strategyCodes = new HashSet<>();
        for (ScriptGenerationStrategyProperties.OpeningStrategyItem strategyItem : configuredStrategies) {
            validateStrategyItem(strategyItem);
            String normalizedCode = strategyItem.getCode().trim();
            if (!strategyCodes.add(normalizedCode)) {
                throw new BusinessException("脚本开头策略编码重复: " + normalizedCode);
            }
            strategyPool.add(new OpeningStrategy(
                    normalizedCode,
                    strategyItem.getName().trim(),
                    strategyItem.getInstruction().trim()
            ));
        }
        return strategyPool;
    }

    private void validateStrategyItem(ScriptGenerationStrategyProperties.OpeningStrategyItem strategyItem) {
        if (strategyItem == null) {
            throw new BusinessException("脚本开头策略配置不完整");
        }
        if (strategyItem.getCode() == null || strategyItem.getCode().isBlank()) {
            throw new BusinessException("脚本开头策略编码不能为空");
        }
        if (strategyItem.getName() == null || strategyItem.getName().isBlank()) {
            throw new BusinessException("脚本开头策略名称不能为空");
        }
        if (strategyItem.getInstruction() == null || strategyItem.getInstruction().isBlank()) {
            throw new BusinessException("脚本开头策略说明不能为空");
        }
    }
}
