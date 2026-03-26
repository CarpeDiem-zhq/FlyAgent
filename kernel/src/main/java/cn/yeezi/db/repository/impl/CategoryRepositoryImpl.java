package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.CategoryEntity;
import cn.yeezi.db.mapper.CategoryMapper;
import cn.yeezi.db.repository.CategoryRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Category Service implementation
 * </p>
 *
 * @author codex
 * @since 2026-01-12
 */
@Service
public class CategoryRepositoryImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryRepository {

}
