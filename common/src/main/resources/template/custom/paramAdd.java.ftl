package ${paramPackage};

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author ${author}
 * @since ${date}
 */
@Data
@Schema(description = "${table.comment!}-添加参数")
public class ${classPrefix}AddParam {

    @Schema(description = "DEMO字段，根据实际场景定义字段")
    private String name;
}
