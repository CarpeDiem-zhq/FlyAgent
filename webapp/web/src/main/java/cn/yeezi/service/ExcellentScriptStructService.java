package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.util.ObjectUtils;
import cn.yeezi.db.entity.ExcellentScriptStructEntity;
import cn.yeezi.db.entity.ProductEntity;
import cn.yeezi.db.entity.TagItemEntity;
import cn.yeezi.db.repository.ExcellentScriptStructRepository;
import cn.yeezi.model.param.ExcellentScriptStructPageParam;
import cn.yeezi.model.param.ExcellentScriptStructAddParam;
import cn.yeezi.model.vo.ExcellentScriptStructRecordVO;
import cn.yeezi.model.vo.ExcellentScriptStructAddVO;
import cn.yeezi.service.dify.DifyScriptStructClient;
import cn.yeezi.web.WebSessionHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 优秀脚本结构服务
 *
 * @author codex
 * @since 2026-03-02
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ExcellentScriptStructService {

    private static final String SYNC_STATUS_SYNCING = "SYNCING";
    private static final String SYNC_STATUS_SUCCESS = "SUCCESS";
    private static final String SYNC_STATUS_FAILED = "FAILED";

    private final DifyScriptStructClient difyScriptStructClient;
    private final ExcellentScriptStructRepository excellentScriptStructRepository;
    private final ProductService productService;
    private final TagService tagService;
    @Qualifier("scriptGenerateExecutor")
    private final Executor scriptGenerateExecutor;

    public ExcellentScriptStructAddVO addExcellentScript(ExcellentScriptStructAddParam param) {
        String userPhone = WebSessionHolder.getUserPhone();
        if (userPhone == null || userPhone.isBlank()) {
            throw new BusinessException("用户手机号不能为空");
        }

        ProductEntity product = productService.requireActiveProduct(param.getProductId());
        if (!product.getProductName().equals(param.getProductName())) {
            throw new BusinessException("产品名称与产品id不匹配");
        }
        TagItemEntity feature = tagService.requireActiveFeatureByName(param.getProductId(), param.getFunctionName());
        if (!feature.getTagName().equals(param.getFunctionName())) {
            throw new BusinessException("产品功能不存在或已停用");
        }

        ExcellentScriptStructEntity entity = new ExcellentScriptStructEntity();
        entity.setProductId(param.getProductId());
        entity.setProductName(param.getProductName());
        entity.setFunctionName(param.getFunctionName());
        entity.setExcellentScript(param.getExcellentScript());
        entity.setStructuredScript(null);
        entity.setKnowledgeDatasetId(difyScriptStructClient.getDatasetId());
        entity.setKnowledgeDocumentId(difyScriptStructClient.getDocumentId());
        entity.setSegmentId(null);
        entity.setSyncStatus(SYNC_STATUS_SYNCING);
        entity.setSyncErrorMsg(null);
        entity.setDel(false);
        excellentScriptStructRepository.save(entity);

        ExcellentScriptStructAddVO vo = new ExcellentScriptStructAddVO();
        vo.setRecordId(entity.getId());
        vo.setSyncStatus(SYNC_STATUS_SYNCING);

        CompletableFuture.runAsync(
                () -> asyncSync(entity.getId(), param, userPhone),
                scriptGenerateExecutor
        );
        return vo;
    }

    public IPage<ExcellentScriptStructRecordVO> pageList(ExcellentScriptStructPageParam param) {
        Page<ExcellentScriptStructEntity> page = new Page<>(param.getPage(), param.getSize());
        LambdaQueryWrapper<ExcellentScriptStructEntity> query = new LambdaQueryWrapper<>();
        query.eq(ExcellentScriptStructEntity::getProductId, param.getProductId());
        query.eq(ExcellentScriptStructEntity::getDel, false);
        query.orderByDesc(ExcellentScriptStructEntity::getId);
        IPage<ExcellentScriptStructEntity> entityPage = excellentScriptStructRepository.page(page, query);
        return ObjectUtils.entityToVO(entityPage, entity -> {
            ExcellentScriptStructRecordVO vo = new ExcellentScriptStructRecordVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        });
    }

    private String parseStructuredText(JsonNode workflowResult) {
        if (workflowResult == null || workflowResult.isMissingNode()) {
            return null;
        }
        return workflowResult.path("data").path("outputs").path("text").asText(null);
    }

    private String parseSegmentId(JsonNode knowledgeResult) {
        if (knowledgeResult == null || knowledgeResult.isMissingNode()) {
            return null;
        }
        JsonNode dataNode = knowledgeResult.path("data");
        if (!dataNode.isArray() || dataNode.isEmpty()) {
            return null;
        }
        JsonNode first = dataNode.get(0);
        if (first == null || first.isMissingNode()) {
            return null;
        }
        String id = first.path("id").asText(null);
        if (id == null || id.isBlank()) {
            return null;
        }
        JsonNode errorNode = first.get("error");
        if (errorNode != null && !errorNode.isNull()) {
            String error = errorNode.asText("");
            if (!error.isBlank()) {
                return null;
            }
        }
        return id;
    }

    private void asyncSync(Long recordId, ExcellentScriptStructAddParam param, String userPhone) {
        try {
            JsonNode workflowResult = difyScriptStructClient.runScriptStructWorkflow(param, userPhone);
            String structuredText = parseStructuredText(workflowResult);
            if (structuredText == null || structuredText.isBlank()) {
                throw new BusinessException("结构拆解结果为空");
            }

            JsonNode knowledgeResult = difyScriptStructClient.addKnowledgeSegment(
                    structuredText,
                    param.getProductName(),
                    param.getFunctionName()
            );
            String segmentId = parseSegmentId(knowledgeResult);
            if (segmentId == null || segmentId.isBlank()) {
                throw new BusinessException("写入知识库失败，请稍后重试");
            }

            ExcellentScriptStructEntity successUpdate = new ExcellentScriptStructEntity();
            successUpdate.setId(recordId);
            successUpdate.setStructuredScript(structuredText);
            successUpdate.setSegmentId(segmentId);
            successUpdate.setSyncStatus(SYNC_STATUS_SUCCESS);
            successUpdate.setSyncErrorMsg(null);
            excellentScriptStructRepository.updateById(successUpdate);
        } catch (Exception e) {
            String errorMsg = resolveAsyncErrorMessage(e);
            log.error("优秀脚本异步同步失败，recordId={}, error={}", recordId, errorMsg, e);
            ExcellentScriptStructEntity failUpdate = new ExcellentScriptStructEntity();
            failUpdate.setId(recordId);
            failUpdate.setSyncStatus(SYNC_STATUS_FAILED);
            failUpdate.setSyncErrorMsg(errorMsg);
            excellentScriptStructRepository.updateById(failUpdate);
        }
    }

    private String resolveAsyncErrorMessage(Exception e) {
        if (e instanceof BusinessException) {
            return e.getMessage();
        }
        return "网络请求异常，请稍后重试";
    }
}
