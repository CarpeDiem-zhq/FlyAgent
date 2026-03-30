package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.util.ObjectUtils;
import cn.yeezi.db.entity.ScriptAssetEntity;
import cn.yeezi.db.repository.ScriptAssetRepository;
import cn.yeezi.model.param.ScriptAssetDetailParam;
import cn.yeezi.model.param.ScriptAssetMyPageParam;
import cn.yeezi.model.param.ScriptAssetPageParam;
import cn.yeezi.model.vo.ScriptAssetDetailVO;
import cn.yeezi.model.vo.ScriptAssetVO;
import cn.yeezi.web.WebSessionHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScriptAssetService {

    private static final TypeReference<List<Long>> LONG_LIST_TYPE = new TypeReference<>() {
    };

    private final ScriptAssetRepository scriptAssetRepository;
    private final ObjectMapper objectMapper;

    public IPage<ScriptAssetVO> listMyAssets(ScriptAssetMyPageParam param) {
        Long userId = WebSessionHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Page<ScriptAssetEntity> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<ScriptAssetEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptAssetEntity::getUserId, userId);
        query.eq(param.getProductId() != null, ScriptAssetEntity::getProductId, param.getProductId());
        query.eq(param.getFeatureId() != null, ScriptAssetEntity::getFeatureId, param.getFeatureId());
        query.eq(ScriptAssetEntity::getDel, false);
        query.orderByDesc(ScriptAssetEntity::getCreateTime);
        return ObjectUtils.entityToVO(scriptAssetRepository.page(page, query), this::toListVO);
    }

    public IPage<ScriptAssetVO> listAllAssets(ScriptAssetPageParam param) {
        Page<ScriptAssetEntity> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<ScriptAssetEntity> query = new LambdaQueryWrapper<>();
        query.eq(param.getProductId() != null, ScriptAssetEntity::getProductId, param.getProductId());
        query.eq(param.getFeatureId() != null, ScriptAssetEntity::getFeatureId, param.getFeatureId());
        query.eq(ScriptAssetEntity::getDel, false);
        query.orderByDesc(ScriptAssetEntity::getCreateTime);
        return ObjectUtils.entityToVO(scriptAssetRepository.page(page, query), this::toListVO);
    }

    public ScriptAssetDetailVO getDetail(ScriptAssetDetailParam param) {
        ScriptAssetEntity entity = scriptAssetRepository.getById(param.getAssetId());
        if (entity == null || Boolean.TRUE.equals(entity.getDel())) {
            throw new BusinessException("脚本资产不存在");
        }
        ScriptAssetDetailVO vo = new ScriptAssetDetailVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCoreSellingPointIds(readIds(entity.getCoreSellingPointIds(), entity.getCoreSellingPointId()));
        return vo;
    }

    private ScriptAssetVO toListVO(ScriptAssetEntity entity) {
        ScriptAssetVO vo = new ScriptAssetVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setCoreSellingPointIds(readIds(entity.getCoreSellingPointIds(), entity.getCoreSellingPointId()));
        return vo;
    }

    private List<Long> readIds(String json, Long fallbackId) {
        if (json != null && !json.isBlank()) {
            try {
                return objectMapper.readValue(json, LONG_LIST_TYPE);
            } catch (Exception ignored) {
            }
        }
        return fallbackId == null ? Collections.emptyList() : Collections.singletonList(fallbackId);
    }
}
