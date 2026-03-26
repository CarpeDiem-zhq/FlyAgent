package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.AppUsageLogEntity;
import cn.yeezi.db.repository.AppUsageLogRepository;
import cn.yeezi.model.enums.AppUsageBizTypeEnum;
import cn.yeezi.model.param.AppUsageRecordParam;
import cn.yeezi.model.param.AppUsageStatsParam;
import cn.yeezi.model.vo.AppUsageStatsVO;
import cn.yeezi.web.WebSessionHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 应用使用埋点服务
 *
 * @author codex
 * @since 2026-03-23
 */
@Service
@RequiredArgsConstructor
public class AppUsageService {

    private final AppUsageLogRepository appUsageLogRepository;

    @Transactional
    public void recordUsage(AppUsageRecordParam param) {
        String appCode = requireValidAppCode(param.getAppCode());
        Long userId = WebSessionHolder.getUserId();

        AppUsageLogEntity entity = new AppUsageLogEntity();
        entity.setUserId(userId);
        entity.setAppCode(appCode);
        appUsageLogRepository.save(entity);
    }

    public AppUsageStatsVO getStats(AppUsageStatsParam param) {
        String appCode = requireValidAppCode(param.getAppCode());
        Long userId = WebSessionHolder.getUserId();

        LambdaQueryWrapper<AppUsageLogEntity> currentUserQuery = new LambdaQueryWrapper<>();
        currentUserQuery.eq(AppUsageLogEntity::getUserId, userId);
        currentUserQuery.eq(AppUsageLogEntity::getAppCode, appCode);

        LambdaQueryWrapper<AppUsageLogEntity> totalQuery = new LambdaQueryWrapper<>();
        totalQuery.eq(AppUsageLogEntity::getAppCode, appCode);

        AppUsageStatsVO vo = new AppUsageStatsVO();
        vo.setAppCode(appCode);
        vo.setCurrentUserCount(appUsageLogRepository.count(currentUserQuery));
        vo.setTotalCount(appUsageLogRepository.count(totalQuery));
        return vo;
    }

    private String requireValidAppCode(String appCode) {
        AppUsageBizTypeEnum bizType = AppUsageBizTypeEnum.fromCode(appCode);
        if (bizType == null) {
            throw new BusinessException("应用类型不存在");
        }
        return bizType.code;
    }
}
