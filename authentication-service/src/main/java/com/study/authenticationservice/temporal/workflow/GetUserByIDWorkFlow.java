package com.study.authenticationservice.temporal.workflow;

import com.study.authenticationservice.dto.request.AccountCreateRequest;
import com.study.authenticationservice.dto.response.AccountCreateResponse;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface GetUserByIDWorkFlow {
    @WorkflowMethod
    AccountCreateResponse getUserByID(String id);
}
