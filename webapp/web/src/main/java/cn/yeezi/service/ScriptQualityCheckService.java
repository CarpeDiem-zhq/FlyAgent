package cn.yeezi.service;

import cn.yeezi.model.vo.ScriptQualityCheckVO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScriptQualityCheckService {

    public ScriptQualityCheckVO check(ScriptGenerationLlmResult result, String adWords) {
        List<String> issues = new ArrayList<>();
        if (result == null) {
            issues.add("生成结果为空");
        } else {
            if (result.getScript() == null || result.getScript().isBlank()) {
                issues.add("脚本正文为空");
            }
            if (result.getTitle() == null || result.getTitle().isBlank()) {
                issues.add("标题为空");
            }
            if (result.getStructure() == null ||
                    isBlank(result.getStructure().getHook()) ||
                    isBlank(result.getStructure().getProblem()) ||
                    isBlank(result.getStructure().getSolution()) ||
                    isBlank(result.getStructure().getCta())) {
                issues.add("脚本结构不完整");
            }
            if (result.getScript() != null && result.getScript().contains("```")) {
                issues.add("脚本正文包含代码块");
            }
            if (adWords != null && !adWords.isBlank() && result.getScript() != null
                    && result.getScript().trim().length() < 20) {
                issues.add("脚本正文过短");
            }
        }
        return ScriptQualityCheckVO.builder().passed(issues.isEmpty()).issues(issues).build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
