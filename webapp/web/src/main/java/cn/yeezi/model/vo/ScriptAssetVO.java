package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 脚本资产信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "脚本资产信息")
public class ScriptAssetVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "生成内容")
    private String outputContent;

    @Schema(description = "复制次数")
    private Integer copyCount;

    @Schema(description = "点赞次数")
    private Integer likeCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}