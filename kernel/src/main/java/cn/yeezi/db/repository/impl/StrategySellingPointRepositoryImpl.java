package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.StrategySellingPointEntity;
import cn.yeezi.db.mapper.StrategySellingPointMapper;
import cn.yeezi.db.repository.StrategySellingPointRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Repository;

@Repository
public class StrategySellingPointRepositoryImpl
        extends ServiceImpl<StrategySellingPointMapper, StrategySellingPointEntity>
        implements StrategySellingPointRepository {
}
