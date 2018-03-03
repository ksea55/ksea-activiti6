package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.zip.ZipInputStream;


public class BaseActivitiToMessageCatchingEventTest {


    private ProcessEngine processEngine;

    @Before
    public void before() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void deployment() {
        ZipInputStream zipInputStream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/activitiApi/loansMessageCatchingEvent.zip"));
        processEngine.getRepositoryService()
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .name("贷款审批流程")
                .deploy();
    }

    @Test
    public void startProcessInstance() {
        processEngine.getRuntimeService().startProcessInstanceByKey("messageCatchingEventProcess");
    }

    @Test
    public void complete() {
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().list();
        Task task = taskList.get(0);
        processEngine.getTaskService().complete(task.getId());
    }


    //此刻任务到达receivetask节点,该节点给贷款人下划短信通知，通知贷款人，贷款审批已经通过
    @Test
    public void receivetask() {
        Execution execution = this.processEngine
                .getRuntimeService()
                .createExecutionQuery()
                .processInstanceId("102501")
                .onlyChildExecutions()
                .singleResult();

        System.out.println(execution.getId() + "_" + execution.getActivityId() + "_" + execution.getProcessInstanceId());


       //根据消息通知执行
        this.processEngine.getRuntimeService().messageEventReceived("贷款服务通知",execution.getId());

    }


}
