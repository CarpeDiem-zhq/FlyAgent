package ${package.Entity};

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import cn.yeezi.common.generator.EntityCloner;

/**
 * ${table.comment!}
 *
 * @author ${author}
 * @since ${date}
 */
public interface ${entity}Getter extends EntityCloner<${entity}> {

<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    /**
    * ${field.comment}
    */
    ${field.propertyType} get${field.capitalName}();
</#list>
<#-- ----------  BEGIN 字段循环遍历  ---------->

    @Override
    ${entity} cloneEntity();
}
