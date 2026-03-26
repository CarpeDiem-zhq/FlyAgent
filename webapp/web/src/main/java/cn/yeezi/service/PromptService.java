package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.PromptEntity;
import cn.yeezi.db.repository.PromptRepository;
import cn.yeezi.model.param.PromptCreateParam;
import cn.yeezi.model.param.PromptDetailParam;
import cn.yeezi.model.param.PromptUpdateParam;
import cn.yeezi.model.vo.PromptVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromptService {

    private static final String DEFAULT_PROMPT_NAME = "默认提示词";
    private static final String DEFAULT_PROMPT_CONTENT = "请根据输入信息生成专业、吸引人的广告脚本。";

    private final PromptRepository promptRepository;

    public List<PromptVO> listPrompts(Long productId, Boolean enabled) {
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
        query.eq(PromptEntity::getDel, false);
        query.eq(enabled != null, PromptEntity::getEnabled, enabled);
        query.orderByDesc(PromptEntity::getId);
        return promptRepository.list(query).stream()
                .map(this::toPromptVO)
                .collect(Collectors.toList());
    }

    public PromptVO getPromptDetail(PromptDetailParam param) {
        PromptEntity prompt = promptRepository.getById(param.getPromptId());
        if (prompt == null || Boolean.TRUE.equals(prompt.getDel())) {
            throw new BusinessException("提示词不存在");
        }
        return toPromptVO(prompt);
    }

    @Transactional
    public void createPrompt(PromptCreateParam param) {
        PromptEntity entity = new PromptEntity();
        entity.setProductId(param.getProductId());
        entity.setPromptName(param.getPromptName());
        entity.setSystemPrompt(param.getSystemPrompt());
        entity.setEnabled(true);
        entity.setDel(false);
        promptRepository.save(entity);
    }

    @Transactional
    public void updatePrompt(PromptUpdateParam param) {
        PromptEntity existing = promptRepository.getById(param.getId());
        if (existing == null || Boolean.TRUE.equals(existing.getDel())) {
            throw new BusinessException("提示词不存在");
        }
        PromptEntity update = new PromptEntity();
        update.setId(param.getId());
        update.setPromptName(param.getPromptName());
        update.setSystemPrompt(param.getSystemPrompt());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        promptRepository.updateById(update);
    }

    @Transactional
    public void initDefaultPrompts(Long productId) {
        if (productId == null) {
            throw new BusinessException("产品id不能为空");
        }
        PromptEntity prompt = findPromptByName(productId, DEFAULT_PROMPT_NAME);
        if (prompt == null) {
            PromptEntity entity = new PromptEntity();
            entity.setProductId(productId);
            entity.setPromptName(DEFAULT_PROMPT_NAME);
            entity.setSystemPrompt(DEFAULT_PROMPT_CONTENT);
            entity.setEnabled(true);
            entity.setDel(false);
            promptRepository.save(entity);
        }
    }

    public PromptEntity getActivePrompt(Long productId, Long promptId) {
        PromptEntity prompt;
        if (promptId != null) {
            prompt = promptRepository.getById(promptId);
            if (prompt == null || Boolean.TRUE.equals(prompt.getDel())) {
                throw new BusinessException("提示词不存在");
            }
            if (!prompt.getProductId().equals(productId)) {
                throw new BusinessException("提示词不属于该产品");
            }
            if (Boolean.FALSE.equals(prompt.getEnabled())) {
                throw new BusinessException("提示词已停用");
            }
            return prompt;
        }
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
        query.eq(PromptEntity::getDel, false);
        query.eq(PromptEntity::getEnabled, true);
        query.orderByDesc(PromptEntity::getId);
        query.last("limit 1");
        prompt = promptRepository.getOne(query);
        if (prompt == null) {
            throw new BusinessException("暂无可用提示词");
        }
        return prompt;
    }

    public String buildPromptSnapshot(String ruleSnapshot, String systemPrompt, List<String> caseSnippets, String userInput) {
        StringBuilder builder = new StringBuilder();
        appendBlock(builder, systemPrompt);

        if (ruleSnapshot != null && !ruleSnapshot.isBlank()) {
            builder.append("【规则约束】\n").append(ruleSnapshot).append("\n\n");
        }
        if (caseSnippets != null && !caseSnippets.isEmpty()) {
            builder.append("【优秀案例片段】\n");
            for (String snippet : caseSnippets) {
                builder.append(snippet).append("\n");
            }
            builder.append("\n");
        }
        if (userInput != null && !userInput.isBlank()) {
            builder.append("【用户输入】\n").append(userInput).append("\n");
        }
        return builder.toString().trim();
    }

    private void appendBlock(StringBuilder builder, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        builder.append(content).append("\n\n");
    }

    private PromptEntity findPromptByName(Long productId, String promptName) {
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
        query.eq(PromptEntity::getPromptName, promptName);
        query.eq(PromptEntity::getDel, false);
        query.last("limit 1");
        return promptRepository.getOne(query);
    }

    private PromptVO toPromptVO(PromptEntity entity) {
        PromptVO vo = new PromptVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}