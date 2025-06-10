package com.study.authenticationservice.config;

import com.study.authenticationservice.temporal.activities.Implement.GetUserByIDActivitiesImpl;
import com.study.authenticationservice.temporal.workflow.Implement.GetUserByIDWorkFlowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {

    public static final String TASK_QUEUE_GET = "GetUserByIDTaskQueue";

    private WorkerFactory workerFactory; // dùng để gọi start() trong PostConstruct

    @Bean
    public WorkflowServiceStubs workflowServiceStubs() {
        return WorkflowServiceStubs.newInstance();
    }

    @Bean
    public WorkflowClient workflowClient(WorkflowServiceStubs serviceStubs) {
        return WorkflowClient.newInstance(serviceStubs);
    }

    @Bean
    public WorkerFactory workerFactory(WorkflowClient workflowClient,
                                       GetUserByIDActivitiesImpl getUserByIDActivities) {
        WorkerFactory factory = WorkerFactory.newInstance(workflowClient);

        Worker getUserByIdWorker = factory.newWorker(TASK_QUEUE_GET);
        getUserByIdWorker.registerWorkflowImplementationTypes(GetUserByIDWorkFlowImpl.class);
        getUserByIdWorker.registerActivitiesImplementations(getUserByIDActivities);

        factory.start();

        return factory;
    }


}
