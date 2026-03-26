package cn.yeezi.core.user;

import cn.hutool.json.JSONUtil;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import cn.yeezi.common.util.ApplicationUtil;
import cn.yeezi.db.entity.UserEntity;
import cn.yeezi.db.entity.UserEntityGetter;
import cn.yeezi.db.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;

/**
 * 用户
 *
 * @author whh
 * @since 2025-12-16
 */
@RequiredArgsConstructor
public class User {

    private final UserEntity entity;

    public long getId() {
        return entity.getId();
    }

    public UserEntityGetter getEntity() {
        return entity;
    }

    public void checkStatus() {
        //todo zhq 校验用户状态
    }

    // ------------------------------------------- 相关静态方法 ------------------------------------------

    public static final UserRepository repository = ApplicationUtil.getBean(UserRepository.class);

    public static User build(UserEntity entity) {
        return new User(entity);
    }

    public static User create(UserEntity entity) {
        boolean save = repository.save(entity);
        if (save) {
            return build(entity);
        }
        throw new BusinessException(String.format("User create failed! entity:%s", JSONUtil.toJsonStr(entity)));
    }


    public static User get(long id) {
        UserEntity entity = repository.getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND, String.format("User<%s> not found!", id));
        }
        return build(entity);
    }

    public static User getByPhone(String phone) {
        UserEntity entity = repository.getOne(
                new LambdaQueryWrapper<UserEntity>().
                        eq(UserEntity::getPhone, phone)
        );
        if (entity == null) {
            return null;
        }
        return build(entity);
    }

    public static boolean existsByPhone(String phone) {
        return repository.exists(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getPhone, phone)
        );
    }

}
