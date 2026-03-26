package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "脚本质检")
public class ScriptQualityCheckVO {

    @Schema(description = "是否通过")
    private Boolean passed;

    @Schema(description = "问题列表")
    private List<String> issues;
}
