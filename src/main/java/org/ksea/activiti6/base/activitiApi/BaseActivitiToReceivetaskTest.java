package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 该节点说一下对其receiveTask的引用如何出发下一个节点去做事情
 */
public class BaseActivitiToReceivetaskTest {


    private ProcessEngine processEngine;

    @Before
    public void before() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void deployment() {
        ZipInputStream zipInputStream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/activitiApi/loansReceiveTask.zip"));
        processEngine.getRepositoryService()
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .name("贷款审批流程")
                .deploy();
    }

    @Test
    public void startProcessInstance() {
        processEngine.getRuntimeService().startProcessInstanceByKey("loans_receivetask");
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

        /**
         *
         * receivetask 是不在act_ru_task表中的,而是存在act_ru_execution表中的
         * 在act_ru_execution我们就可以根据piid(processInstanceId)来进行查询
         *
         *
         */
        Execution execution = this.processEngine
                .getRuntimeService()
                .createExecutionQuery()
                .processInstanceId("57501")
                .onlyChildExecutions()
                .singleResult();

        System.out.println(execution.getId() + "_" + execution.getActivityId() + "_" + execution.getProcessInstanceId());


        //直接触发流程进入下一个节点
        this.processEngine.getRuntimeService().trigger(execution.getId());

        //  this.processEngine.getRuntimeService().signal(execution.getId()); 该方法适用于6.0之前 ，在6.0之后使用的trigger

    }


}
