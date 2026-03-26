package cn.yeezi.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用表格图片生成工具类测试
 *
 * @author luoguoliang
 * @since 2025-09-04
 */
@DisplayName("通用表格图片生成工具类测试")
class TableImageGeneratorTest {


    private final String outputDir = "D:\\img\\";


    @Test
    @DisplayName("测试使用默认样式生成Excel表格")
    void testGenerateTableImageWithDefaultStyle() throws IOException {
        String outputPath = outputDir + "text_to_image_default5.png";
        List<TableImageModel> modelList = new ArrayList<>();
        TableImageModel model = TableImageModel.builder()
                .name("汇总")
                .title("-")
                .desc("18,745.999")
                .dateTime("-").build();
        TableImageModel model2 = TableImageModel.builder()
                .name("客户共享退回")
                .title("我是一个很长很长的代发神鼎飞丹砂防守打法手打打发第三方文本打扫房间阿里卡动静分离受打击发啦手打是否可见阿萨德")
                .desc("18,745.999")
                .dateTime("2025-09-09").build();
//        modelList.add(model);
        modelList.add(model2);
        // 默认生成表头
        // ByteArrayOutputStream outputStream = TableImageGeneratorUtil.generateTableImageStream(modelList);
        // 不生成表头
        ByteArrayOutputStream outputStream = TableImageGeneratorUtil.generateTableImageStream(modelList,false);

        // 保存图片
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
            outputStream.writeTo(fileOutputStream);
        }
        System.out.println("默认样式Excel表格生成成功: " + outputPath);
    }

}