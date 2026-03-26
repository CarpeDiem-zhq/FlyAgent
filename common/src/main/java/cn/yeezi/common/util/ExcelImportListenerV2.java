package cn.yeezi.common.util;

import cn.hutool.core.util.StrUtil;
import cn.yeezi.common.result.ResultVO;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.CommentData;
import com.alibaba.excel.metadata.data.RichTextStringData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入监听器接口，用于自定义Excel导入的校验逻辑
 */
@Slf4j
public abstract class ExcelImportListenerV2<T, T2> extends AnalysisEventListener<T> {

    /**
     * 单次缓存的数据量
     */
    protected static final int BATCH_COUNT = 1000;

    /**
     * 临时存储导入列表
     */
    protected final List<T> cachedImportList = new ArrayList<>(BATCH_COUNT);

    /**
     * 临时存储导出列表
     */
    protected final List<T2> cachedExportList = new ArrayList<>(BATCH_COUNT);

    /**
     * 数据校验成功
     */
    private boolean validate = true;

    protected final HttpServletResponse response;

    protected ExcelImportListenerV2(HttpServletResponse response) {
        this.response = response;
    }


    /**
     * 每解析一行数据会调用此方法
     *
     * @param data    当前行的数据
     * @param context 解析上下文
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        validate = validate(data);
    }

    /**
     * 解析完成后调用此方法
     *
     * @param context 解析上下文
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        try {
            if (validate) {
                if (cachedImportList.isEmpty()) {
                    try {
                        response.reset();
                        response.setContentType("application/json");
                        response.setCharacterEncoding("utf-8");
                        ResultVO<Object> fail = ResultVO.fail("Excel文件有效数据为空");
                        response.getWriter().println(JsonUtil.toJson(fail));
                    } catch (IOException ex) {
                        log.error(ex.getMessage(), ex);
                    }
                    return;
                }
                log.info("开始导入数据，共{}条...", cachedImportList.size());
                saveData();
                log.info("导入数据完成，共{}条。", cachedImportList.size());
                return;
            }

            exportData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                response.reset();
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                ResultVO<Object> fail = ResultVO.fail("导入数据失败：原因：" + e.getMessage());
                response.getWriter().println(JsonUtil.toJson(fail));
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * 保存数据
     */
    protected abstract boolean validate(T importData);

    /**
     * 保存数据
     */
    protected abstract void saveData();

    /**
     * 导出数据
     */
    protected abstract void exportData() throws IOException;

    /**
     * 构建错误单元格
     *
     * @param text    单元格文本
     * @param comment 错误提示
     * @return 错误单元格
     */
    protected WriteCellData<String> buildErrorCell(String text, String comment) {
        WriteCellData<String> cellData = new WriteCellData<>(text);
        cellData.setType(CellDataTypeEnum.STRING);

        // 设置单元格背景色为红色
        WriteCellStyle writeCellStyle = new WriteCellStyle();
        writeCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        writeCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        cellData.setWriteCellStyle(writeCellStyle);

        // 批注
        if (StrUtil.isNotBlank(comment)) {
            CommentData commentData = new CommentData();
            commentData.setRichTextStringData(new RichTextStringData(comment));
            commentData.setRelativeLastColumnIndex(3);
            commentData.setRelativeLastRowIndex(1);
            cellData.setCommentData(commentData);
        }

        return cellData;
    }

    /**
     * 构建普通单元格
     *
     * @param text 单元格文本
     * @return 单元格
     */
    protected WriteCellData<String> buildNormalCell(String text) {
        WriteCellData<String> cellData = new WriteCellData<>(StrUtil.nullToEmpty(text));
        cellData.setType(CellDataTypeEnum.STRING);
        return cellData;
    }
}