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

    private final PromptRepository promptRepository;
    private final ProductService productService;

    public List<PromptVO> listPrompts(Long productId, Boolean enabled) {
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
        query.eq(PromptEntity::getDel, false);
        query.eq(enabled != null, PromptEntity::getEnabled, enabled);
        query.orderByAsc(PromptEntity::getId);
        return promptRepository.list(query).stream().map(this::toPromptVO).collect(Collectors.toList());
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
        productService.requireActiveProduct(param.getProductId());
        if (findByProductId(param.getProductId()) != null) {
            throw new BusinessException("该产品已存在系统提示词");
        }
        PromptEntity entity = new PromptEntity();
        entity.setProductId(param.getProductId());
        entity.setSystemPrompt(param.getSystemPrompt().trim());
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
        update.setSystemPrompt(param.getSystemPrompt().trim());
        if (param.getEnabled() != null) {
            update.setEnabled(param.getEnabled());
        }
        promptRepository.updateById(update);
    }

    public PromptEntity requireActivePromptByProduct(Long productId) {
        productService.requireActiveProduct(productId);
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
        query.eq(PromptEntity::getDel, false);
        query.eq(PromptEntity::getEnabled, true);
        query.orderByAsc(PromptEntity::getId);
        query.last("limit 1");
        PromptEntity prompt = promptRepository.getOne(query);
        if (prompt == null) {
            throw new BusinessException("暂无可用提示词");
        }
        return prompt;
    }

    private PromptEntity findByProductId(Long productId) {
        LambdaQueryWrapper<PromptEntity> query = new LambdaQueryWrapper<>();
        query.eq(PromptEntity::getProductId, productId);
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
