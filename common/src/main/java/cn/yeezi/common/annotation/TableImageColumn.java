package cn.yeezi.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格图片列配置注解，用于配置表格图片生成时列的显示信息
 *
 * @author luoguoliang
 * @since 2025-09-04
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableImageColumn {
    
    /**
     * 表头名称
     * @return 表头显示名称
     */
    String value() default "";
    
    /**
     * 列宽度（像素）
     * @return 列宽度
     */
    int width() default 180;
    
    /**
     * 列显示顺序，数字越小越靠前
     * @return 显示顺序
     */
    int order() default Integer.MAX_VALUE;
    
    /**
     * 是否忽略该字段，不在表格中显示
     * @return 是否忽略
     */
    boolean ignore() default false;
    
    /**
     * 列对齐方式
     * @return 对齐方式
     */
    Align align() default Align.LEFT;
    
    /**
     * 对齐方式枚举
     */
    enum Align {
        LEFT, CENTER, RIGHT
    }
}