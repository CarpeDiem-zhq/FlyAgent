package cn.yeezi.controller;

import cn.yeezi.common.result.ResultVO;
import cn.yeezi.model.param.DifyChatAssistantParam;
import cn.yeezi.model.vo.DifyChatAssistantVO;
import cn.yeezi.service.DifyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/dify")
public class DifyController {

    private final DifyService difyService;


    @Operation(summary = "对话助手")
    @PostMapping("/chatAssistant")
    public ResultVO<DifyChatAssistantVO> chatAssistant(@Valid @RequestBody DifyChatAssistantParam params) {
        DifyChatAssistantVO difyChatAssistantVO = difyService.chatAssistant(params);
        return ResultVO.success(difyChatAssistantVO);
    }
}
