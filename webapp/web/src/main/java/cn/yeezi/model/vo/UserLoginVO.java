package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVO {

    @Schema(description = "登录令牌名字")
    public String tokenName;

    @Schema(description = "登录令牌")
    public String tokenValue;
}

