package cn.yeezi.common.query;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 时间和分页参数
 *
 * @author wanghh
 * @since 2021-06-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TimeAndPageParam extends PageParam {

    protected LocalDateTime startTime;

    protected LocalDateTime endTime;
}

