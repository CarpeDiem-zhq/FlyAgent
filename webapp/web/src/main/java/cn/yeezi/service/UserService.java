package cn.yeezi.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.redis.RedisService;
import cn.yeezi.common.util.IdGenerateUtil;
import cn.yeezi.core.user.User;
import cn.yeezi.db.entity.UserEntity;
import cn.yeezi.model.enums.RedisKeyEnum;
import cn.yeezi.model.enums.UserStatusEnum;
import cn.yeezi.model.param.UserLoginParam;
import cn.yeezi.model.param.UserRegisterParam;
import cn.yeezi.model.vo.ImgCodeVO;
import cn.yeezi.model.vo.UserLoginVO;
import cn.yeezi.model.vo.UserVO;
import cn.yeezi.web.WebSessionHolder;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.springboot.captcha.ArithmeticCaptcha;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.yeezi.model.constant.ConstantPool.USER_NAME;
import static cn.yeezi.model.constant.ConstantPool.USER_PHONE;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final RedisService redisService;
    private final IdGenerateUtil idGenerateUtil;

    public ImgCodeVO getImgCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 48);
        captcha.setLen(2);
        captcha.getArithmeticString();
        long snowflakeId = idGenerateUtil.generateSnowflakeId();
        String redisKey = String.format(RedisKeyEnum.IMG_CODE.key, snowflakeId);
        redisService.set(redisKey, captcha.text(), RedisKeyEnum.IMG_CODE.timeout, RedisKeyEnum.IMG_CODE.timeUnit);
        ImgCodeVO vo = new ImgCodeVO();
        vo.setImgCode(captcha.toBase64());
        vo.setRandomId(String.valueOf(snowflakeId));
        return vo;
    }


    @Transactional
    public UserLoginVO register(UserRegisterParam param) {

        // 校验手机号
        if (StringUtils.isBlank(param.getPhone()) || param.getPhone().length() != 11) {
            throw new BusinessException("手机号必须为11位");
        }
        if (!param.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }

        if (!Objects.equals(param.getPassword(), param.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        // 校验验证码
        String redisKey = String.format(RedisKeyEnum.IMG_CODE.key, param.getRandomId());
        String imgCode = (String) redisService.get(redisKey);
        // 使用后即删除
        redisService.del(redisKey);

        if (StringUtils.isBlank(imgCode)) {
            throw new BusinessException("验证码已失效，请刷新验证码");
        }
        if (!Objects.equals(param.getCode(), imgCode)) {
            throw new BusinessException("验证码错误");
        }

        // 检查是否已注册
        if (User.existsByPhone(param.getPhone())) {
            throw new BusinessException("该手机号已被注册");
        }

        // 注册用户
        UserEntity entity = new UserEntity();
        entity.setPhone(param.getPhone());
        /*
        entity.setPassword(SecureUtil.md5(param.getPassword()));
        */
        entity.setPassword(new BCryptPasswordEncoder().encode(param.getPassword()));

        entity.setName("用户" + param.getPhone().substring(param.getPhone().length() - 4));
        entity.setStatus(UserStatusEnum.ENABLE.value);

        User user = User.create(entity);

        UserLoginVO vo = new UserLoginVO();
        saveLoginSession(user);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        vo.setTokenName(tokenInfo.getTokenName());
        vo.setTokenValue(tokenInfo.getTokenValue());

        return vo;
    }


    @Transactional
    public UserLoginVO login(UserLoginParam param) {
        String password = param.getPassword();
        String redisKey = String.format(RedisKeyEnum.IMG_CODE.key, param.getRandomId());
        String imgCode = (String) redisService.get(redisKey);
        // 使用后即删除
        redisService.del(redisKey);
        if (StringUtils.isBlank(imgCode)) {
            throw new BusinessException("验证码已失效，请刷新验证码");
        }
        if (!Objects.equals(param.getCode(), imgCode)) {
            throw new BusinessException("验证码错误");
        }
        // 查询用户
        User manager = User.getByPhone(param.getPhone());
        if (manager == null) {
            throw new BusinessException("账号不存在，请先注册");
        }
        if (!new BCryptPasswordEncoder().matches(password, manager.getEntity().getPassword())) {
            throw new BusinessException("密码错误");
        }
        if (manager.getEntity().getStatus() == UserStatusEnum.DISABLE.value) {
            throw new BusinessException("账号已禁用");
        }

        UserLoginVO vo = new UserLoginVO();
        saveLoginSession(manager);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        vo.setTokenName(tokenInfo.getTokenName());
        vo.setTokenValue(tokenInfo.getTokenValue());
        return vo;
    }

    private void saveLoginSession(User user) {
        Map<String, Object> extMap = new HashMap<>();
        extMap.put(USER_NAME, user.getEntity().getName());
        extMap.put(USER_PHONE, user.getEntity().getPhone());
        StpUtil.login(user.getId(), SaLoginParameter.create().setExtraData(extMap));
    }

    public UserVO getLoginInfo() {
        long userId = WebSessionHolder.getUserId();
        User user = User.get(userId);
        return BeanUtil.copyProperties(user.getEntity(), UserVO.class);
    }

}