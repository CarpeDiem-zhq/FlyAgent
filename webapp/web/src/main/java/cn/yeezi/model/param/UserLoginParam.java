package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author wanghh
 * @since 2022-08-28
 */
@Data
@Schema(description = "手机号密码登录-参数")
public class UserLoginParam {

    @NotBlank
    @Schema(description = "手机号")
    private String phone;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @NotBlank
    @Schema(description = "图形验证码")
    private String code;

    @NotBlank
    @Schema(description = "随机码")
    private String randomId;
}
