package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取图形验证码-返回数据")
public class ImgCodeVO {

    @Schema(description = "图形验证码")
    private String imgCode;

    @Schema(description = "随机码")
    private String randomId;
}
