package cn.yeezi.service;

import cn.yeezi.db.entity.ScriptGenerationRecordEntity;
import cn.yeezi.db.repository.ScriptGenerationRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScriptGenerationRecordService {
    private final ScriptGenerationRecordRepository recordRepository;

    @Transactional
    public void saveRecord(Long productId, String ruleSnapshot, String inputSnapshot, String outputContent) {
        ScriptGenerationRecordEntity entity = new ScriptGenerationRecordEntity();
        entity.setProductId(productId);
        entity.setRuleSnapshot(ruleSnapshot);
        entity.setInputSnapshot(inputSnapshot);
        entity.setOutputContent(outputContent);
        recordRepository.save(entity);
    }
}
