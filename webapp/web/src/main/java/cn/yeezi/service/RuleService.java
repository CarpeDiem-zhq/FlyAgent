package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.GlobalRuleEntity;
import cn.yeezi.db.entity.ProductRuleEntity;
import cn.yeezi.db.repository.GlobalRuleRepository;
import cn.yeezi.db.repository.ProductRuleRepository;
import cn.yeezi.model.vo.RuleItemVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RuleService {
    private final GlobalRuleRepository globalRuleRepository;
    private final ProductRuleRepository productRuleRepository;

    public String buildRuleSnapshot(Long productId, List<Long> tagIds) {
        List<String> globalRules = listGlobalRuleContents();
        List<String> productRules = listProductRuleContents(productId);
        StringBuilder prompt = new StringBuilder();
        if (!globalRules.isEmpty()) {
            prompt.append("【全局规则】\n");
            appendRules(prompt, globalRules);
        }
        if (!productRules.isEmpty()) {
            if (prompt.length() > 0) {
                prompt.append("\n");
            }
            prompt.append("【产品规则】\n");
            appendRules(prompt, productRules);
        }
        return prompt.toString().trim();
    }

    public List<RuleItemVO> listGlobalRuleItems() {
        Integer version = getLatestGlobalRuleVersion();
        if (version == null) {
            return List.of();
        }
        LambdaQueryWrapper<GlobalRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(GlobalRuleEntity::getDel, false);
        query.eq(GlobalRuleEntity::getEnabled, true);
        query.eq(GlobalRuleEntity::getVersion, version);
        query.orderByAsc(GlobalRuleEntity::getId);
        return globalRuleRepository.list(query).stream()
                .map(entity -> {
                    RuleItemVO vo = new RuleItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<RuleItemVO> listProductRuleItems(Long productId) {
        Integer version = getLatestProductRuleVersion(productId);
        if (version == null) {
            return List.of();
        }
        LambdaQueryWrapper<ProductRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductRuleEntity::getProductId, productId);
        query.eq(ProductRuleEntity::getDel, false);
        query.eq(ProductRuleEntity::getEnabled, true);
        query.eq(ProductRuleEntity::getVersion, version);
        query.orderByAsc(ProductRuleEntity::getId);
        return productRuleRepository.list(query).stream()
                .map(entity -> {
                    RuleItemVO vo = new RuleItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveGlobalRuleVersion(List<String> rules) {
        int nextVersion = nextGlobalRuleVersion();
        for (String rule : rules) {
            GlobalRuleEntity entity = new GlobalRuleEntity();
            entity.setRuleContent(rule);
            entity.setVersion(nextVersion);
            entity.setEnabled(true);
            globalRuleRepository.save(entity);
        }
    }

    @Transactional
    public void saveProductRuleVersion(Long productId, List<String> rules) {
        int nextVersion = nextProductRuleVersion(productId);
        for (String rule : rules) {
            ProductRuleEntity entity = new ProductRuleEntity();
            entity.setProductId(productId);
            entity.setRuleContent(rule);
            entity.setVersion(nextVersion);
            entity.setEnabled(true);
            productRuleRepository.save(entity);
        }
    }

    private List<String> listGlobalRuleContents() {
        Integer version = getLatestGlobalRuleVersion();
        if (version == null) {
            return List.of();
        }
        LambdaQueryWrapper<GlobalRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(GlobalRuleEntity::getDel, false);
        query.eq(GlobalRuleEntity::getEnabled, true);
        query.eq(GlobalRuleEntity::getVersion, version);
        query.orderByAsc(GlobalRuleEntity::getId);
        return globalRuleRepository.list(query).stream()
                .map(GlobalRuleEntity::getRuleContent)
                .filter(rule -> rule != null && !rule.isBlank())
                .collect(Collectors.toList());
    }

    private List<String> listProductRuleContents(Long productId) {
        Integer version = getLatestProductRuleVersion(productId);
        if (version == null) {
            return List.of();
        }
        LambdaQueryWrapper<ProductRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductRuleEntity::getProductId, productId);
        query.eq(ProductRuleEntity::getDel, false);
        query.eq(ProductRuleEntity::getEnabled, true);
        query.eq(ProductRuleEntity::getVersion, version);
        query.orderByAsc(ProductRuleEntity::getId);
        return productRuleRepository.list(query).stream()
                .map(ProductRuleEntity::getRuleContent)
                .filter(rule -> rule != null && !rule.isBlank())
                .collect(Collectors.toList());
    }

    private void appendRules(StringBuilder prompt, List<String> rules) {
        for (String rule : rules) {
            prompt.append("- ").append(rule).append("\n");
        }
    }

    private Integer getLatestGlobalRuleVersion() {
        LambdaQueryWrapper<GlobalRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(GlobalRuleEntity::getDel, false);
        query.eq(GlobalRuleEntity::getEnabled, true);
        query.orderByDesc(GlobalRuleEntity::getVersion);
        query.last("limit 1");
        GlobalRuleEntity latest = globalRuleRepository.getOne(query);
        return latest == null ? null : latest.getVersion();
    }

    private Integer getLatestProductRuleVersion(Long productId) {
        LambdaQueryWrapper<ProductRuleEntity> query = new LambdaQueryWrapper<>();
        query.eq(ProductRuleEntity::getProductId, productId);
        query.eq(ProductRuleEntity::getDel, false);
        query.eq(ProductRuleEntity::getEnabled, true);
        query.orderByDesc(ProductRuleEntity::getVersion);
        query.last("limit 1");
        ProductRuleEntity latest = productRuleRepository.getOne(query);
        return latest == null ? null : latest.getVersion();
    }

    private int nextGlobalRuleVersion() {
        Integer version = getLatestGlobalRuleVersion();
        return version == null ? 1 : version + 1;
    }

    private int nextProductRuleVersion(Long productId) {
        Integer version = getLatestProductRuleVersion(productId);
        return version == null ? 1 : version + 1;
    }

}
