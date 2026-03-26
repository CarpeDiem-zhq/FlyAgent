package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.UserEntity;
import cn.yeezi.db.mapper.UserMapper;
import cn.yeezi.db.repository.UserRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author whh
 * @since 2025-12-16
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, UserEntity> implements UserRepository {

}
