package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.TagItemEntity;
import cn.yeezi.db.mapper.TagItemMapper;
import cn.yeezi.db.repository.TagItemRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * TagItem Service implementation
 * </p>
 *
 * @author codex
 * @since 2026-01-12
 */
@Service
public class TagItemRepositoryImpl extends ServiceImpl<TagItemMapper, TagItemEntity> implements TagItemRepository {

}
