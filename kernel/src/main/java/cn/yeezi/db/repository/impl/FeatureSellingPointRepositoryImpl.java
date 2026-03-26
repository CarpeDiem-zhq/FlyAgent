package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.FeatureSellingPointEntity;
import cn.yeezi.db.mapper.FeatureSellingPointMapper;
import cn.yeezi.db.repository.FeatureSellingPointRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FeatureSellingPointRepositoryImpl
        extends ServiceImpl<FeatureSellingPointMapper, FeatureSellingPointEntity>
        implements FeatureSellingPointRepository {
}
