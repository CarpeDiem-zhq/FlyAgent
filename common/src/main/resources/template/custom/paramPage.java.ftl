package ${paramPackage};

import cn.yeezi.common.query.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author ${author}
 * @since ${date}
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "${table.comment!}-列表参数")
public class ${classPrefix}PageParam extends PageParam {

    @Schema(description = "DEMO字段，根据实际场景定义")
    private String searchWord;
}
