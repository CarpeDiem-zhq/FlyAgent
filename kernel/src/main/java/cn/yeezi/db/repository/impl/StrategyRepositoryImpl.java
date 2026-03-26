package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.StrategyEntity;
import cn.yeezi.db.mapper.StrategyMapper;
import cn.yeezi.db.repository.StrategyRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StrategyRepositoryImpl extends ServiceImpl<StrategyMapper, StrategyEntity> implements StrategyRepository {
}
