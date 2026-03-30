package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.repository.ProductRepository;
import cn.yeezi.model.dto.OpenClawScriptDraftDTO;
import cn.yeezi.model.param.OpenClawScriptGenerateParam;
import cn.yeezi.model.param.OpenClawScriptResolveParam;
import cn.yeezi.model.param.ScriptGenerateParam;
import cn.yeezi.model.vo.OpenClawOptionVO;
import cn.yeezi.model.vo.OpenClawScriptGenerateVO;
import cn.yeezi.model.vo.OpenClawScriptResolveVO;
import cn.yeezi.model.vo.ScriptGenerateVO;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenClawScriptFacadeService {

    private static final String STATUS_ASK = "ASK";
    private static final String STATUS_READY = "READY";
    private static final String STATUS_UNSUPPORTED = "UNSUPPORTED";

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductFeatureService productFeatureService;
    private final FeatureSellingPointService featureSellingPointService;
    private final StrategyService strategyService;
    private final ScriptGenerationService scriptGenerationService;

    public OpenClawScriptResolveVO resolve(OpenClawScriptResolveParam param) {
        OpenClawScriptDraftDTO draft = param.getCurrentDraft() == null ? new OpenClawScriptDraftDTO() : param.getCurrentDraft();
        String traceId = draft.getTraceId() == null || draft.getTraceId().isBlank()
                ? UUID.randomUUID().toString()
                : draft.getTraceId();
        String message = param.getMessageText() == null ? "" : param.getMessageText().trim();
        boolean hasActiveDraft = hasActiveDraft(draft);

        if (!looksLikeScriptIntent(message) && !hasActiveDraft) {
            return OpenClawScriptResolveVO.builder()
                    .status(STATUS_UNSUPPORTED)
                    .currentStep("UNSUPPORTED")
                    .traceId(traceId)
                    .askMessage("")
                    .build();
        }

        if (draft.getProductId() == null) {
            List<OpenClawOptionVO> options = listProducts();
            Long productId = matchOptionId(message, options);
            if (productId == null) {
                productId = matchProductId(message);
            }
            if (productId == null) {
                return buildAsk(traceId, "PRODUCT", "请先选择产品", draft, options, null, null, null);
            }
            draft.setProductId(productId);
        }

        if (draft.getFeatureId() == null) {
            List<OpenClawOptionVO> options = listFeatureOptions(draft.getProductId());
            Long featureId = matchOptionId(message, options);
            if (featureId == null) {
                return buildAsk(traceId, "FEATURE", "请选择产品功能", draft, null, options, null, null);
            }
            draft.setFeatureId(featureId);
        }

        if (draft.getCoreSellingPointId() == null) {
            List<OpenClawOptionVO> options = listSellingPointOptions(draft.getProductId(), draft.getFeatureId());
            Long sellingPointId = matchOptionId(message, options);
            if (sellingPointId == null) {
                return buildAsk(traceId, "SELLING_POINT", "请选择核心卖点", draft, null, null, options, null);
            }
            draft.setCoreSellingPointId(sellingPointId);
        }

        if (draft.getStrategyId() == null) {
            List<OpenClawOptionVO> options = listStrategyOptions(
                    draft.getProductId(), draft.getFeatureId(), draft.getCoreSellingPointId());
            Long strategyId = matchOptionId(message, options);
            if (strategyId == null) {
                return buildAsk(traceId, "STRATEGY", "请选择策略", draft, null, null, null, options);
            }
            draft.setStrategyId(strategyId);
        }

        return OpenClawScriptResolveVO.builder()
                .status(STATUS_READY)
                .currentStep("READY")
                .traceId(traceId)
                .productId(draft.getProductId())
                .featureId(draft.getFeatureId())
                .coreSellingPointId(draft.getCoreSellingPointId())
                .strategyId(draft.getStrategyId())
                .askMessage("参数已齐全，开始生成")
                .build();
    }

    public OpenClawScriptGenerateVO generate(OpenClawScriptGenerateParam param) {
        ScriptGenerateVO result = scriptGenerationService.generate(
                param.getProductId(),
                param.getFeatureId(),
                param.getCoreSellingPointId(),
                param.getStrategyId(),
                param.getUserId()
        );
        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        ProductFeatureEntity feature = productFeatureService.requireActiveFeature(param.getFeatureId());
        FeatureSellingPointEntity sellingPoint =
                featureSellingPointService.requireActiveSellingPoint(param.getCoreSellingPointId());
        StrategyEntity strategy = strategyService.requireActiveStrategy(param.getStrategyId());
        validateRelation(product, feature, sellingPoint, strategy);

        return OpenClawScriptGenerateVO.builder()
                .traceId(param.getTraceId())
                .assetId(result.getAssetId())
                .productId(result.getProductId())
                .featureId(result.getFeatureId())
                .coreSellingPointId(result.getCoreSellingPointIds().isEmpty() ? null : result.getCoreSellingPointIds().get(0))
                .strategyId(result.getStrategyId())
                .displayText(buildDisplayText(product, feature, sellingPoint, strategy, result))
                .outputContent(result.getScriptContent())
                .qualityCheck(result.getQualityCheck())
                .build();
    }

    private void validateRelation(
            ProductEntity product,
            ProductFeatureEntity feature,
            FeatureSellingPointEntity sellingPoint,
            StrategyEntity strategy
    ) {
        if (!product.getId().equals(feature.getProductId())
                || !product.getId().equals(sellingPoint.getProductId())
                || !feature.getId().equals(sellingPoint.getFeatureId())
                || !product.getId().equals(strategy.getProductId())
                || !feature.getId().equals(strategy.getFeatureId())
                || !sellingPoint.getId().equals(strategy.getCoreSellingPointId())) {
            throw new BusinessException("生成参数关系不一致");
        }
    }

    private OpenClawScriptResolveVO buildAsk(
            String traceId,
            String step,
            String askMessage,
            OpenClawScriptDraftDTO draft,
            List<OpenClawOptionVO> productOptions,
            List<OpenClawOptionVO> featureOptions,
            List<OpenClawOptionVO> sellingPointOptions,
            List<OpenClawOptionVO> strategyOptions
    ) {
        return OpenClawScriptResolveVO.builder()
                .status(STATUS_ASK)
                .currentStep(step)
                .traceId(traceId)
                .productId(draft.getProductId())
                .featureId(draft.getFeatureId())
                .coreSellingPointId(draft.getCoreSellingPointId())
                .strategyId(draft.getStrategyId())
                .askMessage(askMessage)
                .productOptions(productOptions)
                .featureOptions(featureOptions)
                .sellingPointOptions(sellingPointOptions)
                .strategyOptions(strategyOptions)
                .build();
    }

    private boolean looksLikeScriptIntent(String message) {
        return message.contains("脚本") || message.contains("广告") || message.contains("生成");
    }

    private boolean hasActiveDraft(OpenClawScriptDraftDTO draft) {
        return draft.getTraceId() != null && !draft.getTraceId().isBlank()
                || draft.getProductId() != null
                || draft.getFeatureId() != null
                || draft.getCoreSellingPointId() != null
                || draft.getStrategyId() != null;
    }

    private Long matchProductId(String message) {
        List<ProductEntity> matches = productRepository.list().stream()
                .filter(item -> !Boolean.TRUE.equals(item.getDel()))
                .filter(item -> item.getProductName() != null && message.contains(item.getProductName()))
                .collect(Collectors.toList());
        return matches.size() == 1 ? matches.get(0).getId() : null;
    }

    private Long matchOptionId(String message, List<OpenClawOptionVO> options) {
        if (options == null || options.isEmpty()) {
            return null;
        }
        String normalized = message.trim();
        try {
            long numeric = Long.parseLong(normalized);
            return options.stream()
                    .filter(option -> option.getId() != null && option.getId() == numeric)
                    .map(OpenClawOptionVO::getId)
                    .findFirst()
                    .orElse(null);
        } catch (NumberFormatException ignored) {
        }
        String numericToken = extractNumericToken(normalized);
        if (numericToken != null) {
            try {
                long numeric = Long.parseLong(numericToken);
                return options.stream()
                        .filter(option -> option.getId() != null && option.getId() == numeric)
                        .map(OpenClawOptionVO::getId)
                        .findFirst()
                        .orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }
        List<OpenClawOptionVO> matches = options.stream()
                .filter(option -> option.getLabel() != null && normalized.contains(option.getLabel()))
                .collect(Collectors.toList());
        return matches.size() == 1 ? matches.get(0).getId() : null;
    }

    private String extractNumericToken(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }
        String digits = message.replaceAll("\\D+", " ").trim();
        if (digits.isEmpty()) {
            return null;
        }
        String[] parts = digits.split("\\s+");
        return parts.length == 0 ? null : parts[0];
    }

    private List<OpenClawOptionVO> listProducts() {
        return productRepository.list().stream()
                .filter(item -> !Boolean.TRUE.equals(item.getDel()))
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getProductName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listFeatureOptions(Long productId) {
        return productFeatureService.listByProduct(productId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getFeatureName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listSellingPointOptions(Long productId, Long featureId) {
        return featureSellingPointService.listByFeature(productId, featureId).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getSellingPointName()))
                .collect(Collectors.toList());
    }

    private List<OpenClawOptionVO> listStrategyOptions(Long productId, Long featureId, Long coreSellingPointId) {
        return strategyService.list(productId, featureId, Collections.singletonList(coreSellingPointId)).stream()
                .filter(item -> !Boolean.FALSE.equals(item.getEnabled()))
                .map(item -> new OpenClawOptionVO(item.getId(), item.getStrategyName()))
                .collect(Collectors.toList());
    }

    private String buildDisplayText(
            ProductEntity product,
            ProductFeatureEntity feature,
            FeatureSellingPointEntity sellingPoint,
            StrategyEntity strategy,
            ScriptGenerateVO result
    ) {
        String qualitySummary = result.getQualityCheck() == null || Boolean.TRUE.equals(result.getQualityCheck().getPassed())
                ? "质检：通过"
                : "质检：未通过，问题：" + String.join("；", result.getQualityCheck().getIssues());
        return "产品：" + product.getProductName() + "\n"
                + "功能：" + feature.getFeatureName() + "\n"
                + "核心卖点：" + sellingPoint.getSellingPointName() + "\n"
                + "策略：" + strategy.getStrategyName() + "\n"
                + "标题：" + result.getScriptTitle() + "\n"
                + qualitySummary + "\n\n"
                + result.getScriptContent();
    }
}
