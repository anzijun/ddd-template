package com.dangkang.app.examplecontext.service;

import com.baidu.unbiz.fluentvalidator.annotation.FluentValid;
import com.dangkang.app.common.annotation.ExceptionResolver;
import com.dangkang.app.common.annotation.Validation;
import com.dangkang.app.examplecontext.service.transaction.ApplicationServiceTransaction;
import com.dangkang.client.examplecontext.api.ApplicationService;
import com.dangkang.client.examplecontext.dto.request.ApplicationServiceRequestDTO;
import com.dangkang.client.examplecontext.dto.response.ApplicationServiceResultDTO;
import com.dangkang.client.dto.response.Response;
import com.dangkang.domain.examplecontext.model.DomainObject;
import com.dangkang.domain.examplecontext.ability.service.DomainService;
import com.dangkang.domain.examplecontext.ability.rule.DomainLogicalRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用服务层:1、逻辑错误异常等统一处理  2、业务主逻辑步骤
 */
@Service
public class ApplicationServiceImpl implements ApplicationService{

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private DomainService domainService;
    @Autowired
    private DomainLogicalRule domainLogicalRule;
    @Autowired
    private ApplicationServiceTransaction applicationServiceTransaction;

    @ExceptionResolver
    @Validation
    @com.dangkang.app.common.annotation.ApplicationService(ServiceCode = "T001",ServiceName = "当康应用服务")
    public Response<ApplicationServiceResultDTO> execute(@FluentValid(isFailFast=false) ApplicationServiceRequestDTO applicationServiceRequestDTO) {

        Response<ApplicationServiceResultDTO> response = new Response<>();
        ApplicationServiceResultDTO applicationServiceResultDTO = new ApplicationServiceResultDTO();
            //todo 业务逻辑编排
            // 1 使用@FluentValid注解进行输入参数校验 (应用Fluent-Validator + Hibernate-Validator )


            // 2.1 调用领域内提供的查询服务(ddd Repository)
            DomainObject domainObject = domainService.findAndCheckEmpty(applicationServiceRequestDTO.getPhoneNumber());
            // 2.2 业务规则验证逻辑(ddd 业务规则封装)
            domainLogicalRule.check(domainObject);
            logger.info("DomainLogicalRule.check领域逻辑规则校验成功");

            // 3.1 领域服务执行(ddd领域服务对象)
            domainService.doService(domainObject);
            logger.info("DomainService.doService领域服务执行成功");

            // 3.2 带事务的领域服务执行
            applicationServiceTransaction.transaction(domainObject);
            logger.info("ApplicationServiceTransaction.transaction事务服务执行成功");

            // 4 构建成功返回
            response.buildSuccess(SERVICE_CODE, SERVICE_NAME);
            response.setData(applicationServiceResultDTO);
        return response;
    }

}
