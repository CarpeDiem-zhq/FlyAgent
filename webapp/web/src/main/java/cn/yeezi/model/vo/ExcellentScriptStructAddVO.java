package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 增加优秀脚本结果
 *
 * @author codex
 * @since 2026-03-02
 */
@Data
@Schema(description = "增加优秀脚本结果")
public class ExcellentScriptStructAddVO {

    @Schema(description = "记录id")
    private Long recordId;

    @Schema(description = "同步状态：SYNCING/SUCCESS/FAILED")
    private String syncStatus;
}
