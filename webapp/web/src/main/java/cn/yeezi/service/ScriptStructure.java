package cn.yeezi.service;

import lombok.Builder;
import lombok.Getter;

/**
 * 脚本结构化信息
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Builder
public class ScriptStructure {

    private final String hook;

    private final String problem;

    private final String solution;

    private final String cta;
}
