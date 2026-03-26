package cn.yeezi.ai;

/**
 * AI 调用场景
 *
 * @author codex
 * @since 2026-03-18
 */
public enum AiSceneType {

    SCRIPT_GENERATION("script-generation");

    private final String code;

    AiSceneType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
