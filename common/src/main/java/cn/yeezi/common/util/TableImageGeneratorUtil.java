package cn.yeezi.common.util;

import cn.hutool.core.util.StrUtil;
import cn.yeezi.common.annotation.TableImageColumn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 通用表格图片生成工具类
 * 支持将任意对象列表转换为Excel样式的表格图片
 *
 * @author luoguoliang
 * @since 2025-09-04
 */
public class TableImageGeneratorUtil {


    /**
     * 字段信息类
     */
    private static class FieldInfo {
        private final Field field;
        private final String displayName;
        private final int width;
        private final int order;
        private final TableImageColumn.Align align;

        public FieldInfo(Field field, String displayName, int width, int order, TableImageColumn.Align align) {
            this.field = field;
            this.displayName = displayName;
            this.width = width;
            this.order = order;
            this.align = align;
        }

        public Field getField() {
            return field;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getWidth() {
            return width;
        }

        public int getOrder() {
            return order;
        }

        public TableImageColumn.Align getAlign() {
            return align;
        }
    }


    /**
     * 获取字段信息（基于注解配置）
     */
    private static FieldInfo[] getFieldInfos(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();

        return Arrays.stream(fields)
                .filter(field -> {
                    TableImageColumn annotation = field.getAnnotation(TableImageColumn.class);
                    return annotation != null && !annotation.ignore(); // 只处理有注解且不忽略的字段
                })
                .map(field -> {
                    TableImageColumn annotation = field.getAnnotation(TableImageColumn.class);
                    // 必须有注解才能处理
                    if (annotation.value().isEmpty()) {
                        throw new IllegalArgumentException(
                                StrUtil.format("字段 {} 的 @TableImageColumn 注解必须指定 value 属性作为表头名称", field.getName()));
                    }
                    return new FieldInfo(field, annotation.value(), annotation.width(),
                            annotation.order(), annotation.align());
                })
                .sorted(Comparator.comparingInt(FieldInfo::getOrder).thenComparing(f -> f.getField().getName()))
                .toArray(FieldInfo[]::new);
    }

    /**
     * 设置高质量渲染
     */
    private static void setupHighQualityRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * 绘制表头
     */
    private static void drawTableHeader(Graphics2D g2d, FieldInfo[] fieldInfos, TableStyle style) {
        // 设置表头背景（包括左右边距区域）
        g2d.setColor(style.getHeaderBackgroundColor());
        int headerY = style.getPadding();
        int totalWidth = Arrays.stream(fieldInfos).mapToInt(FieldInfo::getWidth).sum();
        // 绘制整个表头区域的背景，包括左右边距
        g2d.fillRect(style.getPadding(), headerY, totalWidth + style.getHorizontalPadding() * 2, style.getHeaderHeight());

        // 设置表头字体（加粗）
        Font headerFont = new Font(style.getFontName(), Font.BOLD, style.getHeaderFontSize());
        g2d.setFont(headerFont);
        g2d.setColor(style.getHeaderFontColor());

        FontMetrics fm = g2d.getFontMetrics(headerFont);

        // 绘制表头文字
        int currentX = style.getPadding() + style.getHorizontalPadding(); // 加上左边距
        for (FieldInfo fieldInfo : fieldInfos) {
            String headerText = fieldInfo.getDisplayName();
            int columnWidth = fieldInfo.getWidth();

            // 表头左对齐
            int x = currentX + 6; // 左边距6像素

            int y = headerY + (style.getHeaderHeight() + fm.getAscent()) / 2 - 2;
            g2d.drawString(headerText, x, y);

            currentX += columnWidth;
        }
    }

    /**
     * 绘制数据行（支持文本换行）
     */
    private static void drawDataRows(Graphics2D g2d, List<?> dataList, FieldInfo[] fieldInfos, TableStyle style, boolean showHeader) {
        // 设置数据字体（普通）
        Font dataFont = new Font(style.getFontName(), Font.PLAIN, style.getDataFontSize());
        g2d.setFont(dataFont);
        g2d.setColor(style.getDataFontColor());

        FontMetrics fm = g2d.getFontMetrics(dataFont);
        int lineHeight = fm.getHeight();
        int padding = 8; // 行内上下边距，增加文字与横线的距离

        // 根据是否显示表头来计算数据行起始位置
        int currentRowY = style.getPadding() + (showHeader ? style.getHeaderHeight() : 0);

        for (int row = 0; row < dataList.size(); row++) {
            Object dataObject = dataList.get(row);

            // 首先计算该行所有列的最大行数，确定行高
            int maxLinesInRow = 1;
            List<List<String>> allColumnLines = new ArrayList<>();

            for (FieldInfo fieldInfo : fieldInfos) {
                try {
                    fieldInfo.getField().setAccessible(true);
                    Object value = fieldInfo.getField().get(dataObject);
                    String text = value != null ? value.toString() : "";

                    int columnWidth = fieldInfo.getWidth();
                    // 使用更小的边距，给文本更多空间
                    List<String> lines = wrapText(text, columnWidth - 12, fm);
                    allColumnLines.add(lines);
                    maxLinesInRow = Math.max(maxLinesInRow, lines.size());

                } catch (IllegalAccessException e) {
                    allColumnLines.add(Arrays.asList(""));
                }
            }

            // 计算该行的实际高度
            int actualRowHeight = Math.max(style.getRowHeight(), maxLinesInRow * lineHeight + padding * 2);

            // 绘制该行的数据内容背景色（包括左右边距区域）
            g2d.setColor(style.getDataBackgroundColor());
            int totalWidth = Arrays.stream(fieldInfos).mapToInt(FieldInfo::getWidth).sum();
            // 绘制整个数据行区域的背景，包括左右边距
            g2d.fillRect(style.getPadding(), currentRowY, totalWidth + style.getHorizontalPadding() * 2, actualRowHeight);

            // 恢复字体颜色
            g2d.setColor(style.getDataFontColor());

            // 绘制该行的所有列
            int currentX = style.getPadding() + style.getHorizontalPadding(); // 加上左边距
            for (int col = 0; col < fieldInfos.length; col++) {
                FieldInfo fieldInfo = fieldInfos[col];
                List<String> lines = allColumnLines.get(col);
                int columnWidth = fieldInfo.getWidth();

                // 绘制多行文本
                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);

                    // 根据对齐方式计算x坐标
                    int x;
                    switch (fieldInfo.getAlign()) {
                        case CENTER:
                            x = currentX + (columnWidth - fm.stringWidth(line)) / 2;
                            break;
                        case RIGHT:
                            x = currentX + columnWidth - fm.stringWidth(line) - 6;
                            break;
                        default: // LEFT
                            x = currentX + 6;
                            break;
                    }

                    // 计算y坐标（垂直居中对齐）
                    int totalTextHeight = lines.size() * lineHeight;
                    int textStartY = currentRowY + (actualRowHeight - totalTextHeight) / 2 + fm.getAscent();
                    int y = textStartY + lineIndex * lineHeight;

                    g2d.drawString(line, x, y);
                }

                currentX += columnWidth;
            }

            currentRowY += actualRowHeight;
        }
    }

    /**
     * 计算数据区域的总高度（支持换行）
     */
    private static int calculateTotalDataHeight(List<?> dataList, FieldInfo[] fieldInfos, TableStyle style, FontMetrics fm) {
        int totalHeight = 0;
        int lineHeight = fm.getHeight();
        int padding = 8; // 与drawDataRows保持一致

        for (Object dataObject : dataList) {
            int maxLinesInRow = 1;

            for (FieldInfo fieldInfo : fieldInfos) {
                try {
                    fieldInfo.getField().setAccessible(true);
                    Object value = fieldInfo.getField().get(dataObject);
                    String text = value != null ? value.toString() : "";

                    int columnWidth = fieldInfo.getWidth();
                    List<String> lines = wrapText(text, columnWidth - 12, fm);
                    maxLinesInRow = Math.max(maxLinesInRow, lines.size());

                } catch (IllegalAccessException e) {
                    // 忽略错误
                }
            }

            int actualRowHeight = Math.max(style.getRowHeight(), maxLinesInRow * lineHeight + padding * 2);
            totalHeight += actualRowHeight;
        }

        return totalHeight;
    }

    /**
     * 获取每行的实际高度列表（支持换行）
     */
    private static List<Integer> calculateRowHeights(List<?> dataList, FieldInfo[] fieldInfos, TableStyle style, FontMetrics fm) {
        List<Integer> rowHeights = new ArrayList<>();
        int lineHeight = fm.getHeight();
        int padding = 8; // 与drawDataRows保持一致

        for (Object dataObject : dataList) {
            int maxLinesInRow = 1;

            for (FieldInfo fieldInfo : fieldInfos) {
                try {
                    fieldInfo.getField().setAccessible(true);
                    Object value = fieldInfo.getField().get(dataObject);
                    String text = value != null ? value.toString() : "";

                    int columnWidth = fieldInfo.getWidth();
                    List<String> lines = wrapText(text, columnWidth - 12, fm);
                    maxLinesInRow = Math.max(maxLinesInRow, lines.size());

                } catch (IllegalAccessException e) {
                    // 忽略错误
                }
            }

            int actualRowHeight = Math.max(style.getRowHeight(), maxLinesInRow * lineHeight + padding * 2);
            rowHeights.add(actualRowHeight);
        }

        return rowHeights;
    }

    /**
     * 绘制表格边框（支持动态行高）
     * 只绘制水平分割线，不绘制竖直分割线
     * 不绘制表头开始的横线和表格结尾的横线
     */
    private static void drawTableBorders(Graphics2D g2d, FieldInfo[] fieldInfos, List<?> dataList, TableStyle style, FontMetrics fm, boolean showHeader) {
        g2d.setColor(style.getBorderColor());
        g2d.setStroke(new BasicStroke(style.getBorderWidth()));

        int totalWidth = Arrays.stream(fieldInfos).mapToInt(FieldInfo::getWidth).sum();
        int startX = style.getPadding() + style.getHorizontalPadding(); // 加上左边距
        int startY = style.getPadding();

        // 不绘制外边框的上边框和下边框

        // 不绘制垂直线（竖直分割线）

        // 根据是否显示表头来决定绘制表头分隔线
        int dataStartY;
        if (showHeader) {
            // 绘制水平线（表头分隔线）
            int headerLineY = startY + style.getHeaderHeight();
            g2d.drawLine(startX, headerLineY, startX + totalWidth, headerLineY);
            dataStartY = headerLineY;
        } else {
            // 不显示表头时，数据行从顶部开始
            dataStartY = startY;
        }

        // 绘制数据行分隔线（根据实际行高）
        List<Integer> rowHeights = calculateRowHeights(dataList, fieldInfos, style, fm);
        int currentY = dataStartY;
        for (int row = 0; row < rowHeights.size() - 1; row++) {
            currentY += rowHeights.get(row);
            g2d.drawLine(startX, currentY, startX + totalWidth, currentY);
        }
    }

    /**
     * 文本换行处理 - 改进版本
     * 确保所有文本都能完整显示，支持中文和英文混合文本
     */
    private static List<String> wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        // 如果文本可以在一行内显示
        if (fm.stringWidth(text) <= maxWidth) {
            lines.add(text);
            return lines;
        }

        // 处理换行逻辑 - 逐字符检查
        StringBuilder currentLine = new StringBuilder();
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];
            String testLine = currentLine.toString() + currentChar;

            // 检查添加当前字符后是否超宽
            if (fm.stringWidth(testLine) <= maxWidth) {
                currentLine.append(currentChar);
            } else {
                // 超宽了，需要换行
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    currentLine.append(currentChar);
                } else {
                    // 连一个字符都放不下，强制添加
                    lines.add(String.valueOf(currentChar));
                }
            }
        }

        // 添加最后一行
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        // 确保至少有一行
        if (lines.isEmpty()) {
            lines.add("");
        }

        return lines;
    }

    /**
     * 将对象列表转换为Excel样式的表格图片并返回字节输出流
     *
     * @param dataList 数据列表
     * @param style    表格样式配置
     * @param showHeader 是否显示表头，为false时将不显示表头及其占用空间
     * @return 字节输出流
     * @throws IOException 文件操作异常
     */
    public static ByteArrayOutputStream generateTableImageStream(List<?> dataList, TableStyle style, Boolean showHeader) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("数据列表不能为空");
        }

        // 获取字段信息
        Object firstObject = dataList.get(0);
        FieldInfo[] fieldInfos = getFieldInfos(firstObject);

        // 预计算数据行高度（支持换行）
        Font dataFont = new Font(style.getFontName(), Font.PLAIN, style.getDataFontSize());
        FontMetrics fm = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
                .createGraphics().getFontMetrics(dataFont);
        int totalDataHeight = calculateTotalDataHeight(dataList, fieldInfos, style, fm);

        // 计算图片尺寸（使用注解配置的列宽度）
        int imageWidth = Arrays.stream(fieldInfos).mapToInt(FieldInfo::getWidth).sum() + style.getPadding() * 2 + style.getHorizontalPadding() * 2; // 加上左右边距
        // 根据是否显示表头来计算图片高度
        int imageHeight = (showHeader ? style.getHeaderHeight() : 0) + totalDataHeight + style.getPadding() * 2;

        // 创建BufferedImage
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        try {
            // 设置高质量渲染
            setupHighQualityRendering(g2d);

            // 设置背景色
            g2d.setColor(style.getBackgroundColor());
            g2d.fillRect(0, 0, imageWidth, imageHeight);

            if (showHeader) {
                // 绘制表头
                drawTableHeader(g2d, fieldInfos, style);
            }

            // 绘制数据行
            drawDataRows(g2d, dataList, fieldInfos, style, showHeader);

            // 绘制边框
            if (style.isShowBorder()) {
                drawTableBorders(g2d, fieldInfos, dataList, style, fm, showHeader);
            }

        } finally {
            g2d.dispose();
        }

        // 将图片写入字节输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        return outputStream;
    }

    /**
     * 使用默认样式生成表格图片并返回字节输出流
     * 默认显示表头
     */
    public static ByteArrayOutputStream generateTableImageStream(List<?> dataList) throws IOException {
        return generateTableImageStream(dataList, createDefaultStyle(), true);
    }

    /**
     * 使用默认样式生成表格图片并返回字节输出流
     * @param showHeader 是否显示表头
     */
    public static ByteArrayOutputStream generateTableImageStream(List<?> dataList, Boolean showHeader) throws IOException {
        return generateTableImageStream(dataList, createDefaultStyle(), showHeader);
    }

    /**
     * 使用自定义样式生成表格图片并返回字节输出流
     * 默认显示表头
     */
    public static ByteArrayOutputStream generateTableImageStream(List<?> dataList, TableStyle style) throws IOException {
        return generateTableImageStream(dataList, style, true);
    }


    /**
     * 创建默认样式
     */
    public static TableStyle createDefaultStyle() {
        TableStyle style = new TableStyle();
        // 设置表头背景色为 245, 246, 247
        style.setHeaderBackgroundColor(new Color(245, 246, 247));
        // 设置数据内容背景色为 255, 255, 255（白色）
        style.setDataBackgroundColor(new Color(255, 255, 255));
        // 设置更淡的边框颜色
        style.setBorderColor(new Color(230, 230, 230));
        // 设置加深的字体颜色
        style.setHeaderFontColor(new Color(51, 51, 51));
        style.setDataFontColor(new Color(68, 68, 68));
        // 设置0外边距，12像素左右内边距
        style.setPadding(0);
        style.setHorizontalPadding(12);
        return style;
    }

    /**
     * 表格样式配置类
     */
    public static class TableStyle {
        // 字体配置
        private String fontName = "Microsoft YaHei";
        private int headerFontSize = 16;
        private int dataFontSize = 14;

        // 颜色配置
        private Color backgroundColor = Color.WHITE;
        private Color headerBackgroundColor = new Color(245, 246, 247); // 表头背景色
        private Color dataBackgroundColor = new Color(255, 255, 255); // 数据内容背景色（白色）
        private Color headerFontColor = new Color(51, 51, 51); // 加深的表头字体颜色
        private Color dataFontColor = new Color(68, 68, 68); // 加深的数据字体颜色
        private Color borderColor = new Color(230, 230, 230); // 更淡的边框颜色

        // 布局配置
        private int padding = 0;  // 无外边距
        private int horizontalPadding = 12; // 左右内边距
        private int headerHeight = 50; // 增加表头高度
        private int rowHeight = 45;
        private int columnWidth = 180;
        private boolean showBorder = true;
        private int borderWidth = 1;

        // Getter和Setter方法
        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public int getHeaderFontSize() {
            return headerFontSize;
        }

        public void setHeaderFontSize(int headerFontSize) {
            this.headerFontSize = headerFontSize;
        }

        public int getDataFontSize() {
            return dataFontSize;
        }

        public void setDataFontSize(int dataFontSize) {
            this.dataFontSize = dataFontSize;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public Color getHeaderBackgroundColor() {
            return headerBackgroundColor;
        }

        public void setHeaderBackgroundColor(Color headerBackgroundColor) {
            this.headerBackgroundColor = headerBackgroundColor;
        }

        public Color getDataBackgroundColor() {
            return dataBackgroundColor;
        }

        public void setDataBackgroundColor(Color dataBackgroundColor) {
            this.dataBackgroundColor = dataBackgroundColor;
        }

        public Color getHeaderFontColor() {
            return headerFontColor;
        }

        public void setHeaderFontColor(Color headerFontColor) {
            this.headerFontColor = headerFontColor;
        }

        public Color getDataFontColor() {
            return dataFontColor;
        }

        public void setDataFontColor(Color dataFontColor) {
            this.dataFontColor = dataFontColor;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public void setBorderColor(Color borderColor) {
            this.borderColor = borderColor;
        }

        public int getPadding() {
            return padding;
        }

        public void setPadding(int padding) {
            this.padding = padding;
        }

        public int getHorizontalPadding() {
            return horizontalPadding;
        }

        public void setHorizontalPadding(int horizontalPadding) {
            this.horizontalPadding = horizontalPadding;
        }

        public int getHeaderHeight() {
            return headerHeight;
        }

        public void setHeaderHeight(int headerHeight) {
            this.headerHeight = headerHeight;
        }

        public int getRowHeight() {
            return rowHeight;
        }

        public void setRowHeight(int rowHeight) {
            this.rowHeight = rowHeight;
        }

        public int getColumnWidth() {
            return columnWidth;
        }

        public void setColumnWidth(int columnWidth) {
            this.columnWidth = columnWidth;
        }

        public boolean isShowBorder() {
            return showBorder;
        }

        public void setShowBorder(boolean showBorder) {
            this.showBorder = showBorder;
        }

        public int getBorderWidth() {
            return borderWidth;
        }

        public void setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
        }
    }


}