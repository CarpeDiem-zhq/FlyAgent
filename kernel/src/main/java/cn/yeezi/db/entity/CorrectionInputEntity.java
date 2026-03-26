package cn.yeezi.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 纠偏输入
 *
 * @author codex
 * @since 2026-01-12
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@TableName("correction_input")
@Schema(description = "纠偏输入")
public class CorrectionInputEntity implements CorrectionInputEntityGetter {

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "产品id")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "资产id")
    @TableField("asset_id")
    private Long assetId;

    @Schema(description = "输入类型")
    @TableField("input_type")
    private String inputType;

    @Schema(description = "原因编码列表")
    @TableField("reason_codes")
    private String reasonCodes;

    @Schema(description = "建议")
    @TableField("suggestion")
    private String suggestion;

    @Schema(description = "指标快照")
    @TableField("metric_snapshot")
    private String metricSnapshot;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    public CorrectionInputEntity(CorrectionInputEntityGetter source) {
        this.id = source.getId();
        this.productId = source.getProductId();
        this.assetId = source.getAssetId();
        this.inputType = source.getInputType();
        this.reasonCodes = source.getReasonCodes();
        this.suggestion = source.getSuggestion();
        this.metricSnapshot = source.getMetricSnapshot();
        this.createTime = source.getCreateTime();
    }

    @Override
    public CorrectionInputEntity cloneEntity() {
        return new CorrectionInputEntity(this);
    }
}
