package cn.yeezi.common.util;


import cn.yeezi.common.annotation.TableImageColumn;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: 盖亮Galen
 * @CreateTime: 2025-09-04
 */
@Builder
@Data
public class TableImageModel {

    @TableImageColumn(value = "操作类型", width = 200, order = 1)
    private String name;

    @TableImageColumn(value = "充值日期", width = 200, order = 2)
    private String dateTime;

    @TableImageColumn(value = "备注", width = 200, order = 3)
    private String title;

    @TableImageColumn(value = "总金额", width = 200, order = 4)
    private String desc;

}
