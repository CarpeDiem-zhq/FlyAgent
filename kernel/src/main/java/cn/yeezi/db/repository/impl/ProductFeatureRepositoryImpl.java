package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.ProductFeatureEntity;
import cn.yeezi.db.mapper.ProductFeatureMapper;
import cn.yeezi.db.repository.ProductFeatureRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ProductFeatureRepositoryImpl extends ServiceImpl<ProductFeatureMapper, ProductFeatureEntity>
        implements ProductFeatureRepository {
}
