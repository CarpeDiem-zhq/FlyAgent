package ${servicePackage};

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
<#if query||add||edit>
<#if query>
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${voPackage}.*;
</#if>
import org.springframework.beans.BeanUtils;
import ${paramPackage}.*;
</#if>
import cn.yeezi.common.util.ObjectUtils;
import ${package.Entity}.${entityName};
import ${corePackage}.${coreName};
<#if isNeedBrandId||isNeedUserId>
import cn.yeezi.common.web.SessionHolder;
</#if>
<#if isNeedBrandId>
import cn.yeezi.common.check.BusinessChecker;
</#if>

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
@Service
@RequiredArgsConstructor
public class ${classPrefix}Service {

    <#if query>
    public IPage<${classPrefix}VO> getList(${classPrefix}PageParam param) {
        <#if isNeedBrandId>
        Long brandId = SessionHolder.getBrandId();
        </#if>
        <#if isNeedUserId>
        Long userId = SessionHolder.getUserId();
        </#if>
        LambdaQueryWrapper<${entityName}> queryWrapper = new LambdaQueryWrapper<>();
        <#if isNeedBrandId>
        queryWrapper.eq(${entityName}::getBrandId, brandId);
        </#if>
        <#if isNeedUserId>
        queryWrapper.eq(${entityName}::getUserId, userId);
        </#if>
        //todo 增加查询条件
        queryWrapper.orderByDesc(${entityName}::getCreateTime);
        IPage<${entityName}> page = new Page<>(param.getPage(), param.getSize());
        page = ${coreName}.repository.page(page, queryWrapper);
        return ObjectUtils.entityToVO(page, entity -> {
            //todo to VO
            return null;
        });
    }

    public ${classPrefix}VO getInfo(Long id) {
        <#if isNeedBrandId>
        Long brandId = SessionHolder.getBrandId();
        </#if>
        <#if isNeedUserId>
        Long userId = SessionHolder.getUserId();
        </#if>
        ${coreName} ${tableName} = ${coreName}.get(id);
        <#if isNeedBrandId>
        BusinessChecker.checkIsolation(brandId, ${tableName}.getEntity().getBrandId());
        </#if>
        ${classPrefix}VO vo = new ${classPrefix}VO();
        BeanUtils.copyProperties(${tableName}.getEntity(), vo);
        //todo to VO
        return vo;
    }
    </#if>

    <#if add>
    public void add(${classPrefix}AddParam param) {
        <#if isNeedBrandId>
        Long brandId = SessionHolder.getBrandId();
        </#if>
        <#if isNeedUserId>
        Long userId = SessionHolder.getUserId();
        </#if>
        ${entityName} entity = new ${entityName}();
        BeanUtils.copyProperties(param, entity);
        <#if isNeedBrandId>
        entity.setBrandId(brandId);
        </#if>
        <#if isNeedUserId>
        entity.setUserId(userId);
        </#if>
        //todo 添加属性
        ${coreName}.create(entity);
    }
    </#if>

    <#if edit>
    public void edit(${classPrefix}EditParam param) {
        <#if isNeedBrandId>
        Long brandId = SessionHolder.getBrandId();
        </#if>
        <#if isNeedUserId>
        Long userId = SessionHolder.getUserId();
        </#if>
        ${coreName} ${tableName} = ${coreName}.get(param.getId());
        <#if isNeedBrandId>
        BusinessChecker.checkIsolation(brandId, ${tableName}.getEntity().getBrandId());
        </#if>
        ${entityName} entity = new ${entityName}();
        BeanUtils.copyProperties(param, entity);
        //todo 修改属性
        ${tableName}.update(entity);
    }
    </#if>

    <#if delete>
    public void delete(Long id) {
        <#if isNeedBrandId>
        Long brandId = SessionHolder.getBrandId();
        ${coreName} ${tableName} = ${coreName}.get(id);
        BusinessChecker.checkIsolation(brandId, ${tableName}.getEntity().getBrandId());
        </#if>
        <#if isNeedUserId>
        Long userId = SessionHolder.getUserId();
        </#if>
        ${coreName}.repository.removeById(id);
    }
    </#if>
}
