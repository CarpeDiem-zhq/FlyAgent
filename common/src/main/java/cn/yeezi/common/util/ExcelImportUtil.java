package cn.yeezi.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.yeezi.common.exception.BusinessException;
import com.alibaba.excel.EasyExcel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入工具类，提供通用的Excel文件导入功能
 *
 * @author luoguoliang
 * @since 2025-08-26
 */
public class ExcelImportUtil {

    /**
     * 使用自定义监听器导入Excel文件
     *
     * @param file     上传的Excel文件
     * @param clazz    Excel数据模型类
     * @param listener 自定义监听器
     * @param <T>      Excel数据模型类型
     * @throws BusinessException 导入异常
     */
    public static <T> void importExcel(MultipartFile file, Class<T> clazz, ExcelImportListener<T> listener) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传的文件为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new BusinessException("文件格式错误，请上传Excel文件");
        }

        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, clazz, listener)
                    .sheet() // 读取第一个sheet
                    .doRead();
        } catch (IOException e) {
            throw new BusinessException("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("Excel导入失败：" + e.getMessage());
        }
    }

    /**
     * 使用自定义监听器导入Excel文件
     *
     * @param inputStream Excel文件输入流
     * @param clazz       Excel数据模型类
     * @param listener    自定义监听器
     * @param <T>         Excel数据模型类型
     * @throws BusinessException 导入异常
     */
    public static <T> void importExcel(InputStream inputStream, Class<T> clazz, ExcelImportListener<T> listener) {
        if (inputStream == null) {
            throw new BusinessException("输入流为空");
        }

        try {
            EasyExcel.read(inputStream, clazz, listener)
                    .sheet() // 读取第一个sheet
                    .doRead();
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            throw new BusinessException("Excel导入失败：" + e.getMessage());
        }
    }

    /**
     * 简单导入Excel文件并返回数据列表，不进行校验
     *
     * @param file  上传的Excel文件
     * @param clazz Excel数据模型类
     * @param <T>   Excel数据模型类型
     * @return 解析后的数据列表
     * @throws BusinessException 导入异常
     */
    public static <T> List<T> importExcelToList(MultipartFile file, Class<T> clazz) {
        SimpleExcelImportListener<T> listener = new SimpleExcelImportListener<>();
        importExcel(file, clazz, listener);
        return listener.getDataList();
    }

    /**
     * 简单导入Excel文件并返回数据列表，不进行校验
     *
     * @param inputStream Excel文件输入流
     * @param clazz       Excel数据模型类
     * @param <T>         Excel数据模型类型
     * @return 解析后的数据列表
     * @throws BusinessException 导入异常
     */
    public static <T> List<T> importExcelToList(InputStream inputStream, Class<T> clazz) {
        SimpleExcelImportListener<T> listener = new SimpleExcelImportListener<>();
        importExcel(inputStream, clazz, listener);
        return listener.getDataList();
    }

    /**
     * 简单的Excel导入监听器，仅用于获取数据列表，不进行校验
     *
     * @param <T> Excel数据模型类型
     */
    public static class SimpleExcelImportListener<T> extends ExcelImportListener<T> {

        // 存储所有解析到的数据
        private final List<T> allData = new ArrayList<>();

        @Override
        protected String validate(T data, int rowNum) {
            // 不进行校验，始终返回null表示校验通过
            return null;
        }

        @Override
        protected void processData(List<T> data) {
            // 保存所有数据
            allData.addAll(data);
        }

        @Override
        protected void handleValidationErrors(List<String> errors) {
            // 简单监听器不会产生校验错误
        }

        /**
         * 获取所有解析到的数据
         *
         * @return 数据列表
         */
        public List<T> getDataList() {
            return allData;
        }
    }

    /**
     * 构建校验错误消息
     *
     * @param errors 错误信息列表
     * @return 格式化后的错误消息字符串
     */
    public static String buildErrorMessage(List<String> errors) {
        if (CollUtil.isEmpty(errors)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Excel导入失败，共").append(errors.size()).append("条错误：\n");
        for (int i = 0; i < Math.min(errors.size(), 10); i++) { // 最多显示10条错误
            sb.append(i + 1).append(". ").append(errors.get(i)).append("\n");
        }
        if (errors.size() > 10) {
            sb.append("... 还有").append(errors.size() - 10).append("条错误未显示");
        }
        return sb.toString();
    }
}