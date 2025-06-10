package com.study.authenticationservice.temporal.workflow.Implement;

import com.study.authenticationservice.dto.response.AccountCreateResponse;
import com.study.authenticationservice.temporal.activities.GetUserByIdActivities;
import com.study.authenticationservice.temporal.workflow.GetUserByIDWorkFlow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
@Slf4j
public class GetUserByIDWorkFlowImpl implements GetUserByIDWorkFlow {

    private final GetUserByIdActivities activities = Workflow.newActivityStub(
            GetUserByIdActivities.class,
            ActivityOptions.newBuilder()
                    .setScheduleToCloseTimeout(Duration.ofMinutes(2))
                    .build()
    );

    @Override
    public AccountCreateResponse getUserByID(String id) {

            return activities.GetUserByID(id);

    }
}

