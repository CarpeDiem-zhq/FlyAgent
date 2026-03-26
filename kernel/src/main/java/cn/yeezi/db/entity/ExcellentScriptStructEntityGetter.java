package cn.yeezi.db.entity;

import cn.yeezi.common.generator.EntityCloner;
import java.time.LocalDateTime;

/**
 * 优秀脚本结构记录
 *
 * @author codex
 * @since 2026-03-04
 */
public interface ExcellentScriptStructEntityGetter extends EntityCloner<ExcellentScriptStructEntity> {

    /**
     * 主键
     */
    Long getId();
    /**
     * 产品id
     */
    Long getProductId();
    /**
     * 产品名称
     */
    String getProductName();
    /**
     * 功能名称
     */
    String getFunctionName();
    /**
     * 优秀脚本
     */
    String getExcellentScript();
    /**
     * 解析后的脚本结构
     */
    String getStructuredScript();
    /**
     * 知识库id
     */
    String getKnowledgeDatasetId();
    /**
     * 知识库文档id
     */
    String getKnowledgeDocumentId();
    /**
     * 文档块id
     */
    String getSegmentId();
    /**
     * 同步状态：SYNCING/SUCCESS/FAILED
     */
    String getSyncStatus();
    /**
     * 同步失败原因
     */
    String getSyncErrorMsg();
    /**
     * 是否删除：0.否 1.是
     */
    Boolean getDel();
    /**
     * 创建时间
     */
    LocalDateTime getCreateTime();
    /**
     * 更新时间
     */
    LocalDateTime getUpdateTime();

    @Override
    ExcellentScriptStructEntity cloneEntity();
}
