package cn.yeezi.common.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入监听器接口，用于自定义Excel导入的校验逻辑
 * @param <T> Excel数据模型类型
 * @author luoguoliang
 * @since 2025-08-26
 */
@Getter
public abstract class ExcelImportListener<T> extends AnalysisEventListener<T> {

    /**
     * -- GETTER --
     *  获取解析到的所有数据
     *
     */
    // 存储解析到的数据
    protected final List<T> dataList = new ArrayList<>();

    /**
     * -- GETTER --
     *  获取校验失败信息
     *
     */
    // 存储校验失败的信息
    protected final List<String> errorMessages = new ArrayList<>();
    
    /**
     * 构造函数
     */
    public ExcelImportListener() {
    }
    
    /**
     * 每解析一行数据会调用此方法
     * @param data 当前行的数据
     * @param context 解析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        // 获取当前行号（从1开始）
        int rowNum = context.readRowHolder().getRowIndex() + 1;
        
        // 执行数据校验
        String errorMsg = validate(data, rowNum);
        if (errorMsg != null && !errorMsg.isEmpty()) {
            errorMessages.add("第" + rowNum + "行：" + errorMsg);
        } else {
            // 校验通过，添加到数据列表
            dataList.add(data);
        }
    }
    
    /**
     * 解析完成后调用此方法
     * @param context 解析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 处理所有数据
        if (!dataList.isEmpty()) {
            processData(dataList);
        }
        
        // 处理校验失败的情况
        if (!errorMessages.isEmpty()) {
            handleValidationErrors(errorMessages);
        }
    }
    
    /**
     * 数据校验方法，需要子类实现
     * @param data 要校验的数据
     * @param rowNum 行号
     * @return 校验失败信息，返回null表示校验通过
     */
    protected abstract String validate(T data, int rowNum);
    
    /**
     * 数据处理方法，需要子类实现
     * @param data 所有解析到的数据
     */
    protected abstract void processData(List<T> data);
    
    /**
     * 校验错误处理方法，可被子类重写
     * @param errors 错误信息列表
     */
    protected abstract void handleValidationErrors(List<String> errors);

    /**
     * 是否有校验错误
     * @return 有错误返回true，否则返回false
     */
    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }
}