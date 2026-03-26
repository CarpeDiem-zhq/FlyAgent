package ${corePackage};

import cn.hutool.json.JSONUtil;
import ${config.packageConfig.parent}.db.entity.${entity};
import ${config.packageConfig.parent}.db.entity.${entity}Getter;
import ${config.packageConfig.parent}.db.repository.${coreName}Repository;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import cn.yeezi.common.util.ApplicationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
@RequiredArgsConstructor
public class ${coreName} {

    private final ${coreName}Entity entity;

    public long getId() {
        return entity.getId();
    }

    public ${coreName}EntityGetter getEntity() {
        return entity;
    }

    public void update(${entity} source) {
        BeanUtils.copyProperties(source, entity);
        repository.updateById(entity);
    }

    // ------------------------------------------- 相关静态方法 ------------------------------------------

    public static final ${coreName}Repository repository = ApplicationUtil.getBean(${coreName}Repository.class);

    public static ${coreName} build(${entity} entity) {
        return new ${coreName}(entity);
    }

    public static ${coreName} create(${entity} entity) {
        boolean save = repository.save(entity);
        if (save) {
            return build(entity);
        }
        throw new BusinessException(String.format("${coreName} create failed! entity:%s", JSONUtil.toJsonStr(entity)));
    }


    public static ${coreName} get(long id) {
        ${coreName}Entity entity = repository.getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND, String.format("${coreName}<%s> not found!", id));
        }
        return build(entity);
    }
}
