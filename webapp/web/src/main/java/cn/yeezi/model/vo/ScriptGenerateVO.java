package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 脚本生成结果
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "脚本生成结果")
public class ScriptGenerateVO {

    @Schema(description = "资产id")
    private Long assetId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id列表")
    private List<Long> coreSellingPointIds;

    @Schema(description = "核心卖点名称列表")
    private List<String> coreSellingPointNames;

    @Schema(description = "策略id")
    private Long strategyId;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "脚本标题")
    private String scriptTitle;

    @Schema(description = "脚本内容")
    private String scriptContent;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "质检结果")
    private ScriptQualityCheckVO qualityCheck;
}
