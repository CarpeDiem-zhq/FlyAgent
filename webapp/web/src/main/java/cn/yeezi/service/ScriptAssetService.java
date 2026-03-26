package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.util.ObjectUtils;
import cn.yeezi.db.entity.CorrectionInputEntity;
import cn.yeezi.db.entity.ScriptAssetEntity;
import cn.yeezi.db.entity.ScriptCopyLogEntity;
import cn.yeezi.db.entity.ScriptFavoriteLogEntity;
import cn.yeezi.db.entity.ScriptFeedbackEntity;
import cn.yeezi.db.entity.ScriptGenerateHistoryEntity;
import cn.yeezi.db.entity.ScriptLikeLogEntity;
import cn.yeezi.db.repository.CorrectionInputRepository;
import cn.yeezi.db.repository.ScriptAssetRepository;
import cn.yeezi.db.repository.ScriptCopyLogRepository;
import cn.yeezi.db.repository.ScriptFavoriteLogRepository;
import cn.yeezi.db.repository.ScriptFeedbackRepository;
import cn.yeezi.db.repository.ScriptGenerateHistoryRepository;
import cn.yeezi.db.repository.ScriptLikeLogRepository;
import cn.yeezi.model.param.ScriptAssetDetailParam;
import cn.yeezi.model.param.ScriptAssetMyPageParam;
import cn.yeezi.model.param.ScriptAssetPageParam;
import cn.yeezi.model.param.ScriptAssetSaveParam;
import cn.yeezi.model.param.ScriptAssetUpdateParam;
import cn.yeezi.model.param.ScriptFeedbackParam;
import cn.yeezi.model.vo.ScriptAssetDetailVO;
import cn.yeezi.model.vo.ScriptAssetVO;
import cn.yeezi.model.vo.ScriptGenerateVO;
import cn.yeezi.web.WebSessionHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScriptAssetService {

    private final ScriptAssetRepository scriptAssetRepository;
    private final ScriptCopyLogRepository scriptCopyLogRepository;
    private final ScriptLikeLogRepository scriptLikeLogRepository;
    private final ScriptFavoriteLogRepository scriptFavoriteLogRepository;
    private final ScriptFeedbackRepository scriptFeedbackRepository;
    private final ScriptGenerateHistoryRepository scriptGenerateHistoryRepository;
    private final CorrectionInputRepository correctionInputRepository;
    private final ScriptGenerationService scriptGenerationService;

    public IPage<ScriptAssetVO> listMyAssets(ScriptAssetMyPageParam param) {
        Long userId = WebSessionHolder.getUserId();
        Page<ScriptAssetEntity> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<ScriptAssetEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptAssetEntity::getUserId, userId);
        query.eq(ScriptAssetEntity::getProductId, param.getProductId());
        query.eq(param.getPromptId() != null, ScriptAssetEntity::getPromptId, param.getPromptId());
        query.eq(ScriptAssetEntity::getDel, false);
        query.orderByDesc(ScriptAssetEntity::getCreateTime);
        IPage<ScriptAssetEntity> entityPage = scriptAssetRepository.page(page, query);
        return ObjectUtils.entityToVO(entityPage, entity -> {
            ScriptAssetVO vo = new ScriptAssetVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    public IPage<ScriptAssetVO> listAllAssets(ScriptAssetPageParam param) {
        Long userId = WebSessionHolder.getUserId();
        Page<ScriptAssetEntity> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<ScriptAssetEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptAssetEntity::getProductId, param.getProductId());
        query.eq(param.getPromptId() != null, ScriptAssetEntity::getPromptId, param.getPromptId());
        query.eq(ScriptAssetEntity::getDel, false);
        query.ne(userId != null, ScriptAssetEntity::getUserId, userId);
        query.orderByDesc(ScriptAssetEntity::getCreateTime);
        IPage<ScriptAssetEntity> entityPage = scriptAssetRepository.page(page, query);
        return ObjectUtils.entityToVO(entityPage, entity -> {
            ScriptAssetVO vo = new ScriptAssetVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    public List<ScriptAssetVO> listExcellentCases(Long productId) {
        LambdaQueryWrapper<ScriptAssetEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptAssetEntity::getProductId, productId);
        query.eq(ScriptAssetEntity::getDel, false);
        query.gt(ScriptAssetEntity::getCopyCount, 0);
        query.orderByDesc(ScriptAssetEntity::getCopyCount, ScriptAssetEntity::getId);
        query.last("limit 10");
        return scriptAssetRepository.list(query).stream()
                .map(entity -> {
                    ScriptAssetVO vo = new ScriptAssetVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    public ScriptAssetDetailVO getDetail(ScriptAssetDetailParam param) {
        ScriptAssetEntity entity = scriptAssetRepository.getById(param.getAssetId());
        if (entity == null || Boolean.TRUE.equals(entity.getDel())) {
            throw new BusinessException("资产不存在");
        }
        ScriptAssetDetailVO vo = new ScriptAssetDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Transactional
    public Long saveScriptAsset(ScriptAssetSaveParam param) {
        Long userId = WebSessionHolder.getUserId();
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        }
        ScriptGenerateHistoryEntity history = null;
        if (param.getHistoryId() != null) {
            history = scriptGenerateHistoryRepository.getById(param.getHistoryId());
            if (history == null || Boolean.TRUE.equals(history.getDel())) {
                throw new BusinessException("生成历史不存在");
            }
            if (history.getSavedAssetId() != null || Boolean.TRUE.equals(history.getSaveStatus())) {
                throw new BusinessException("该生成结果已保存");
            }
            if (!param.getProductId().equals(history.getProductId())) {
                throw new BusinessException("生成历史与产品不匹配");
            }
            if (history.getUserId() != null && !history.getUserId().equals(userId)) {
                throw new BusinessException("无权保存该生成历史");
            }
        }
        ScriptAssetEntity entity = new ScriptAssetEntity();
        entity.setProductId(param.getProductId());
        entity.setUserId(userId);
        entity.setPromptId(param.getPromptId());
        entity.setPromptSnapshot(param.getPromptSnapshot());
        entity.setRuleSnapshot(param.getRuleSnapshot());
        entity.setInputSnapshot(param.getInputSnapshot());
        entity.setTagSnapshot(param.getTagSnapshot());
        entity.setUserInputSnapshot(param.getUserInputSnapshot());
        entity.setCaseSnapshot(param.getCaseSnapshot());
        entity.setModelName(param.getModelName() == null || param.getModelName().isBlank() ? "deepseek" : param.getModelName());
        entity.setRouteStrategy(param.getRouteStrategy() == null || param.getRouteStrategy().isBlank() ? "DEFAULT" : param.getRouteStrategy());
        entity.setOutputContent(param.getOutputContent());
        entity.setParentAssetId(param.getParentAssetId());
        entity.setRevisionSeq(param.getRevisionSeq());
        scriptAssetRepository.save(entity);

        if (history != null) {
            ScriptGenerateHistoryEntity update = new ScriptGenerateHistoryEntity();
            update.setId(history.getId());
            update.setSaveStatus(true);
            update.setSavedAssetId(entity.getId());
            scriptGenerateHistoryRepository.updateById(update);
        }
        return entity.getId();
    }

    @Transactional
    public void recordCopy(Long assetId) {
        Long userId = WebSessionHolder.getUserId();
        ScriptAssetEntity asset = requireAsset(assetId);
        ScriptCopyLogEntity log = new ScriptCopyLogEntity();
        log.setAssetId(assetId);
        log.setUserId(userId);
        scriptCopyLogRepository.save(log);
        ScriptAssetEntity update = new ScriptAssetEntity();
        update.setId(assetId);
        update.setCopyCount(asset.getCopyCount() == null ? 1 : asset.getCopyCount() + 1);
        scriptAssetRepository.updateById(update);
    }

    @Transactional
    public void recordLike(Long assetId) {
        Long userId = WebSessionHolder.getUserId();
        ScriptAssetEntity asset = requireAsset(assetId);
        if (userId != null && existsLike(assetId, userId)) {
            return;
        }
        ScriptLikeLogEntity log = new ScriptLikeLogEntity();
        log.setAssetId(assetId);
        log.setUserId(userId);
        scriptLikeLogRepository.save(log);
        ScriptAssetEntity update = new ScriptAssetEntity();
        update.setId(assetId);
        update.setLikeCount(asset.getLikeCount() == null ? 1 : asset.getLikeCount() + 1);
        scriptAssetRepository.updateById(update);
    }

    @Transactional
    public void recordFavorite(Long assetId) {
        Long userId = WebSessionHolder.getUserId();
        ScriptAssetEntity asset = requireAsset(assetId);
        if (userId != null && existsFavorite(assetId, userId)) {
            return;
        }
        ScriptFavoriteLogEntity log = new ScriptFavoriteLogEntity();
        log.setAssetId(assetId);
        log.setUserId(userId);
        scriptFavoriteLogRepository.save(log);
        ScriptAssetEntity update = new ScriptAssetEntity();
        update.setId(assetId);
        update.setFavoriteCount(asset.getFavoriteCount() == null ? 1 : asset.getFavoriteCount() + 1);
        scriptAssetRepository.updateById(update);
    }

    @Transactional
    public ScriptGenerateVO handleFeedback(ScriptFeedbackParam param) {
        Long userId = WebSessionHolder.getUserId();
        ScriptAssetEntity asset = requireAsset(param.getAssetId());

        ScriptFeedbackEntity feedback = new ScriptFeedbackEntity();
        feedback.setAssetId(param.getAssetId());
        feedback.setUserId(userId);
        feedback.setSatisfied(param.getSatisfied());
        feedback.setReasonCodes(scriptGenerationService.writeJson(param.getReasonCodes()));
        feedback.setSuggestion(param.getSuggestion());
        feedback.setRerun(Boolean.TRUE.equals(param.getRerun()));
        scriptFeedbackRepository.save(feedback);

        ScriptAssetEntity update = new ScriptAssetEntity();
        update.setId(asset.getId());
        if (Boolean.TRUE.equals(param.getSatisfied())) {
            update.setPositiveFeedbackCount(asset.getPositiveFeedbackCount() == null ? 1 : asset.getPositiveFeedbackCount() + 1);
        } else {
            update.setNegativeFeedbackCount(asset.getNegativeFeedbackCount() == null ? 1 : asset.getNegativeFeedbackCount() + 1);
            saveCorrectionInput(asset, feedback);
        }
        scriptAssetRepository.updateById(update);

        if (Boolean.TRUE.equals(param.getRerun())) {
            ScriptGenerateVO rerunResult = scriptGenerationService.rerunFromAssetWithFeedback(
                    asset,
                    param.getReasonCodes(),
                    param.getSuggestion()
            );
            return rerunResult;
        }
        return null;
    }

    private void saveCorrectionInput(ScriptAssetEntity asset, ScriptFeedbackEntity feedback) {
        CorrectionInputEntity input = new CorrectionInputEntity();
        input.setProductId(asset.getProductId());
        input.setAssetId(asset.getId());
        input.setInputType("NEGATIVE_FEEDBACK");
        input.setReasonCodes(feedback.getReasonCodes());
        input.setSuggestion(feedback.getSuggestion());
        correctionInputRepository.save(input);
    }

    private ScriptAssetEntity requireAsset(Long assetId) {
        ScriptAssetEntity asset = scriptAssetRepository.getById(assetId);
        if (asset == null || Boolean.TRUE.equals(asset.getDel())) {
            throw new BusinessException("资产不存在");
        }
        return asset;
    }

    private Long saveScriptAsset(ScriptGenerateVO result, Long userId) {
        if (userId == null) {
            throw new BusinessException("用户id不能为空");
        }
        ScriptAssetEntity entity = new ScriptAssetEntity();
        entity.setProductId(result.getProductId());
        entity.setUserId(userId);
        entity.setPromptId(result.getPromptId());
        entity.setPromptSnapshot(result.getPromptSnapshot());
        entity.setRuleSnapshot(result.getRuleSnapshot());
        entity.setInputSnapshot(result.getInputSnapshot());
        entity.setTagSnapshot(result.getTagSnapshot());
        entity.setUserInputSnapshot(result.getUserInputSnapshot());
        entity.setCaseSnapshot(result.getCaseSnapshot());
        entity.setModelName(result.getModelName() == null || result.getModelName().isBlank() ? "deepseek" : result.getModelName());
        entity.setRouteStrategy(result.getRouteStrategy() == null || result.getRouteStrategy().isBlank() ? "DEFAULT" : result.getRouteStrategy());
        entity.setOutputContent(result.getOutputContent());
        entity.setParentAssetId(result.getParentAssetId());
        entity.setRevisionSeq(result.getRevisionSeq());
        scriptAssetRepository.save(entity);
        return entity.getId();
    }

    private boolean existsLike(Long assetId, Long userId) {
        LambdaQueryWrapper<ScriptLikeLogEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptLikeLogEntity::getAssetId, assetId);
        query.eq(ScriptLikeLogEntity::getUserId, userId);
        return scriptLikeLogRepository.count(query) > 0;
    }

    private boolean existsFavorite(Long assetId, Long userId) {
        LambdaQueryWrapper<ScriptFavoriteLogEntity> query = new LambdaQueryWrapper<>();
        query.eq(ScriptFavoriteLogEntity::getAssetId, assetId);
        query.eq(ScriptFavoriteLogEntity::getUserId, userId);
        return scriptFavoriteLogRepository.count(query) > 0;
    }

    @Transactional
    public void updateScriptAsset(ScriptAssetUpdateParam param) {
        ScriptAssetEntity asset = requireAsset(param.getAssetId());
        ScriptAssetEntity update = new ScriptAssetEntity();
        update.setId(asset.getId());
        update.setPromptId(param.getPromptId());
        update.setPromptSnapshot(param.getPromptSnapshot());
        update.setRuleSnapshot(param.getRuleSnapshot());
        update.setInputSnapshot(param.getInputSnapshot());
        update.setTagSnapshot(param.getTagSnapshot());
        update.setUserInputSnapshot(param.getUserInputSnapshot());
        update.setCaseSnapshot(param.getCaseSnapshot());
        update.setModelName(param.getModelName());
        update.setRouteStrategy(param.getRouteStrategy());
        update.setOutputContent(param.getOutputContent());
        scriptAssetRepository.updateById(update);
    }
}
