package cn.yeezi.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.yeezi.common.result.NoData;
import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.UserLoginParam;
import cn.yeezi.model.param.UserRegisterParam;
import cn.yeezi.model.vo.ImgCodeVO;
import cn.yeezi.model.vo.UserLoginVO;
import cn.yeezi.model.vo.UserVO;
import cn.yeezi.service.UserService;
import cn.yeezi.web.WebSessionHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取图形验证码")
    @GetMapping("/getImgCode")
    public ResultVO<ImgCodeVO> getImgCode() {
        ImgCodeVO vo = userService.getImgCode();
        return ResultVO.success(vo);
    }

    @Operation(summary = "用户-登录")
    @PostMapping("/login")
    public ResultVO<UserLoginVO> login(@RequestBody UserLoginParam param) {
        UserLoginVO vo = userService.login(param);
        return ResultVO.success(vo);
    }

    @Operation(summary = "用户-注册")
    @PostMapping("/register")
    public ResultVO<UserLoginVO> register(@RequestBody UserRegisterParam param) {
        UserLoginVO vo = userService.register(param);
        return ResultVO.success(vo);
    }

    @Operation(summary = "用户-登出")
    @PostMapping("/logout")
    public ResultVO<NoData> logout() {
        if (!StpUtil.isLogin()) {
            return ResultVO.success();
        }
        long userId = WebSessionHolder.getUserId();
        StpUtil.logout(userId);
        return ResultVO.success();
    }

    @Operation(summary = "用户-获取当前登录员工信息")
    @GetMapping("/getLoginInfo")
    public ResultVO<UserVO> getLoginInfo() {
        UserVO vo = userService.getLoginInfo();
        return ResultVO.success(vo);
    }
}
