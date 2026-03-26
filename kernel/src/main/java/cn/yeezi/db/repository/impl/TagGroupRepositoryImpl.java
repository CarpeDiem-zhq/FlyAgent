package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.TagGroupEntity;
import cn.yeezi.db.mapper.TagGroupMapper;
import cn.yeezi.db.repository.TagGroupRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * TagGroup Service implementation
 * </p>
 *
 * @author codex
 * @since 2026-01-12
 */
@Service
public class TagGroupRepositoryImpl extends ServiceImpl<TagGroupMapper, TagGroupEntity> implements TagGroupRepository {

}
