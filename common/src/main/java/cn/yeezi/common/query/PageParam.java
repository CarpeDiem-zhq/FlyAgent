package cn.yeezi.common.query;

import lombok.Data;

/**
 * 分页参数
 *
 * @author wanghh
 * @since 2021-06-02
 */
@Data
public class PageParam {

    protected int page = 1;

    protected int size = 10;
}
