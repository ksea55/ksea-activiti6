package org.ksea.activiti6.base.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BaseActiviti6Test2 {


    private ProcessEngine processEngine;

    @Before
    public void before() {
        //获取工作流引擎
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }


    /**
     * 完整案例 工作流引擎
     */
    //第一步部署流程实例
    @Test
    public void deploymentProcessInstanceTest() {
        //部署流程实例bpmn文件
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("bpmn/BaseActiviti6Test2/leave.bpmn")
                .addClasspathResource("bpmn/BaseActiviti6Test2/leave.png")
                .deploy();
    }


    //第二步启动流程实例
    @Test
    public void startProcessInstance() {
        processEngine.getRuntimeService()
                .startProcessInstanceByKey("myLeave");

    }


    //第三步查询当前任务
    @Test
    public void findTaskList() {

        List<Task> list = processEngine.getTaskService().createTaskQuery().list();
        System.out.println(list.size());

        //执行任务
        Task task = list.get(0);
        processEngine.getTaskService().complete(task.getId());

    }


    //执行最终节点任务
    @Test
    public void bossTask() {
        processEngine.getTaskService().complete("5002");
    }
}
