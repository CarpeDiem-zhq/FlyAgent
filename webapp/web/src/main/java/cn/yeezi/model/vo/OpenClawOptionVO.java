package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "OpenClaw 候选项")
public class OpenClawOptionVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "标签")
    private String label;
}
