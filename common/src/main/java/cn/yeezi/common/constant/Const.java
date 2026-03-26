package cn.yeezi.common.constant;

import cn.hutool.crypto.SecureUtil;

import java.util.Set;

public interface Const {

    String YES = "是";

    //中文逗号
    String CHINESE_COMMA = "，";

    //换行符
    String JSON_LINE_SEPARATOR = "\\n";
    String LINE_SEPARATOR = "\n";

    //默认时间格式
    String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    String DEFAULT_MANAGER_PASSWORD = "yz@2025";

    // 预计算 MD5（仅当系统确实用 MD5 存储密码时）
    String DEFAULT_MANAGER_PASSWORD_MD5 = SecureUtil.md5(DEFAULT_MANAGER_PASSWORD);

    // 1. 定义需要同步的 operatorId
    Set<Long> NEED_SYNC_OPERATOR_IDS = Set.of(1683675326L, 2347751751L, 4131252340L);

}
