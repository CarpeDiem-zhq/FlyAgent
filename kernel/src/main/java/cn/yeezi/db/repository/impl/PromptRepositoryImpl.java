package cn.yeezi.db.repository.impl;

import cn.yeezi.db.entity.PromptEntity;
import cn.yeezi.db.mapper.PromptMapper;
import cn.yeezi.db.repository.PromptRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 提示词配置 Service implementation
 *
 * @author codex
 * @since 2026-01-13
 */
@Service
public class PromptRepositoryImpl extends ServiceImpl<PromptMapper, PromptEntity> implements PromptRepository {
}