package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.AppUsageLogEntity;
import cn.yeezi.db.mapper.AppUsageLogMapper;
import cn.yeezi.db.repository.AppUsageLogRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * AppUsageLog Service implementation
 * </p>
 *
 * @author codex
 * @since 2026-03-23
 */
@Service
public class AppUsageLogRepositoryImpl extends ServiceImpl<AppUsageLogMapper, AppUsageLogEntity> implements AppUsageLogRepository {

}
