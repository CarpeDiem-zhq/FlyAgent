package ${voPackage};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(description = "${table.comment!}-响应数据")
public class ${classPrefix}VO {

    @Schema(description = "DEMO字段，根据实际场景定义字段")
    private String name;
}
