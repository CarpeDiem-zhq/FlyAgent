package cn.yeezi.common.util;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * jackson封装类
 *
 * @author wanghh
 */
@Slf4j
public class JsonUtil {

    public static String toJson(Object o) {
        try {
            return getObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.JSON_ERROR, e.getMessage());
        }
    }

    public static <T> T toObject(String json, Class<T> valueType) {
        try {
            return getObjectMapper().readValue(json, valueType);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.JSON_ERROR, e.getMessage());
        }
    }

    public static <T> T toObject(String json, TypeReference<T> typeReference) {
        try {
            return getObjectMapper().readValue(json, typeReference);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.JSON_ERROR, e.getMessage());
        }
    }

    /**
     * json字符串转成list
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> List<T> toList(String jsonString, Class<T> cls) {
        try {
            return getObjectMapper().readValue(jsonString, getCollectionType(cls));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.JSON_ERROR, e.getMessage());
        }
    }

    /**
     * 获取泛型的Collection Type
     */
    private static JavaType getCollectionType(Class<?>... elementClasses) {
        return getObjectMapper().getTypeFactory().constructParametricType(List.class, elementClasses);
    }


    /**
     * 读取json
     *
     * @param json json字符串
     * @return json node
     */
    public static JsonNode readTree(String json) {
        try {
            return getObjectMapper().readTree(json);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ResultCodeEnum.JSON_ERROR, e.getMessage());
        }
    }

    private static ObjectMapper getObjectMapper() {
        ApplicationContext applicationContext = ApplicationUtil.getApplicationContext();
        if (applicationContext != null) {
            return applicationContext.getBean(ObjectMapper.class);
        }
        return Holder.DEFAULT_OBJECT_MAPPER;
    }

    private static final class Holder {

        private static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();

        private Holder() {
        }
    }
}
