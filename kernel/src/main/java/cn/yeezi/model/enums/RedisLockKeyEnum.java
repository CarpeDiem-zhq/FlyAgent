package cn.yeezi.model.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RedisLockKeyEnum {
    CUSTOMER_RECHARGE_BALANCE_LOCK("customer:rechargeBalance:%s", "客户充值余额"),
    ACCOUNT_COMPANY_RECHARGE_BALANCE_LOCK("accountCompany:rechargeBalance:%s:%s", "账户主体充值余额"),
    SYNC_ACCOUNT_TRANSACTION_LOG_LOCK("sync:accountTransactionLog:%s", "同步充值账户流水"),
    SYNC_PORT_WALLET_TRANSACTION_LOG_LOCK("sync:portWalletTransactionLog:%s", "同步子端口和共享钱包流水"),
    SYNC_ACCOUNT_LOCK("sync:account:%s", "同步账户数据"),
    //
    ;

    public final String key;
    public final String text;

    public static String getCustomerRechargeBalanceLockKey(long projectId) {
        return String.format(CUSTOMER_RECHARGE_BALANCE_LOCK.key, projectId);
    }

    public static String getAccountCompanyRechargeBalanceLockKey(long projectId, long accountCompanyId) {
        return String.format(ACCOUNT_COMPANY_RECHARGE_BALANCE_LOCK.key, projectId, accountCompanyId);
    }

    public static String getSyncAccountTransactionLogLockKey(String platformAgentId) {
        return String.format(SYNC_ACCOUNT_TRANSACTION_LOG_LOCK.key, platformAgentId);
    }

    public static String getSyncPortWalletTransactionLogLockKey(String platformAgentId) {
        return String.format(SYNC_PORT_WALLET_TRANSACTION_LOG_LOCK.key, platformAgentId);
    }

    public static String getSyncAccountLockKey(String platformAgentId) {
        return String.format(SYNC_ACCOUNT_LOCK.key, platformAgentId);
    }
}
