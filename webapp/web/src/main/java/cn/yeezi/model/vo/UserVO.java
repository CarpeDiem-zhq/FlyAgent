package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author whh
 * @since 2025-08-21
 */
@Data
@Schema(description = "员工-响应数据")
public class UserVO {

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "状态：1.正常 2.禁用")
    private Integer status;
}
