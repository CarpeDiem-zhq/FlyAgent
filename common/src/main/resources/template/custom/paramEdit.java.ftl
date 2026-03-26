package ${paramPackage};

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(description = "${table.comment!}-编辑参数")
public class ${classPrefix}EditParam {

    @NotNull
    @Schema(description = "数据id")
    private Long id;

    @Schema(description = "DEMO字段，根据实际场景定义字段")
    private String name;
}
