package ${package.Controller};

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
<#if query||add||edit>
import com.baomidou.mybatisplus.core.metadata.IPage;
import ${paramPackage}.*;
</#if>
<#if query>
import ${voPackage}.*;
</#if>
import cn.yeezi.common.result.NoData;
import cn.yeezi.common.result.ResultVO;
import ${servicePackage}.${classPrefix}Service;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * <p>
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Tag(name = "${table.comment!}")
@RestController
@RequestMapping("/${tableName}")
@RequiredArgsConstructor
public class ${table.controllerName} {

    private final ${classPrefix}Service ${tableName}Service;

    <#if query>
    @Operation(summary = "${table.comment!}-列表")
    @GetMapping("/getList")
    public ResultVO<IPage<${classPrefix}VO>> getList(${classPrefix}PageParam param) {
        IPage<${classPrefix}VO> page = ${tableName}Service.getList(param);
        return ResultVO.success(page);
    }

    @Operation(summary = "${table.comment!}-详细信息")
    @GetMapping("/getInfo")
    public ResultVO<${classPrefix}VO> getInfo(Long id) {
        ${classPrefix}VO vo = ${tableName}Service.getInfo(id);
        return ResultVO.success(vo);
    }
    </#if>

    <#if add>
    @Operation(summary = "${table.comment!}-增加")
    @PostMapping("/add")
    public ResultVO<NoData> add(@RequestBody @Valid ${classPrefix}AddParam param) {
        ${tableName}Service.add(param);
        return ResultVO.success();
    }
    </#if>

    <#if edit>
    @Operation(summary = "${table.comment!}-编辑")
    @PostMapping("/edit")
    public ResultVO<NoData> edit(@RequestBody @Valid ${classPrefix}EditParam param) {
        ${tableName}Service.edit(param);
        return ResultVO.success();
    }
    </#if>

    <#if delete>
    @Operation(summary = "${table.comment!}-删除")
    @GetMapping("/delete")
    public ResultVO<NoData> delete(Long id) {
        ${tableName}Service.delete(id);
        return ResultVO.success();
    }
    </#if>
}
