package cn.yeezi.common.generator.engine;

import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import cn.yeezi.common.generator.FileTypeEnum;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author wanghh
 * @since 2022-11-11
 */
public class CustomTemplateEngine extends FreemarkerTemplateEngine {

    @Override
    protected void outputCustomFile(List<CustomFile> customFiles, TableInfo tableInfo, Map<String, Object> objectMap) {
        String entityName = tableInfo.getEntityName();
        String modelParent = String.valueOf(objectMap.get("modelParent"));
        for (CustomFile customFile : customFiles) {
            String fileName = customFile.getFileName();
            FileTypeEnum fileTypeEnum = FileTypeEnum.suffixOf(fileName);
            String coreName = String.valueOf(objectMap.get("coreName"));
            String classPrefix = String.valueOf(objectMap.get("classPrefix"));
            fileName = switch (fileTypeEnum) {
                case ENTITY_GETTER -> {
                    String entityPath = this.getPathInfo(OutputFile.entity);
                    yield String.format(entityPath + File.separator + entityName + "%s", fileName);
                }
                case CORE -> {
                    String corePath = String.valueOf(objectMap.get("corePath"));
                    yield String.format(corePath + File.separator + modelParent + File.separator + coreName + "%s", fileName);
                }
                case SERVICE -> {
                    String servicePath = String.valueOf(objectMap.get("servicePath"));
                    yield String.format(servicePath + File.separator + classPrefix + "%s", fileName);
                }
                case PARAM_ADD, PARAM_EDIT, PARAM_PAGE -> {
                    String modelPath = String.valueOf(objectMap.get("modelPath"));
                    yield String.format(modelPath + File.separator + "param" + File.separator + modelParent + File.separator + classPrefix + "%s", fileName);
                }
                case VO -> {
                    String modelPath2 = String.valueOf(objectMap.get("modelPath"));
                    yield String.format(modelPath2 + File.separator + "vo" + File.separator + modelParent + File.separator + classPrefix + "%s", fileName);
                }
                default -> throw new IllegalArgumentException();
            };
            System.out.println("自定义文件路径：" + fileName);
            this.outputFile(new File(fileName), objectMap, customFile.getTemplatePath(), true);
        }
    }
}
