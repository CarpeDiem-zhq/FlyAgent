package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.mapper.ProductMapper;
import cn.yeezi.db.repository.ProductRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 浜у搧 鏈嶅姟瀹炵幇绫? * </p>
 *
 * @author codex
 * @since 2025-12-19
 */
@Service
public class ProductRepositoryImpl extends ServiceImpl<ProductMapper, ProductEntity> implements ProductRepository {

}
