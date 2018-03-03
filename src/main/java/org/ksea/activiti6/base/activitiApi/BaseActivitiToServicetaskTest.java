package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.zip.ZipInputStream;


public class BaseActivitiToServicetaskTest {


    private ProcessEngine processEngine;

    @Before
    public void before() {

        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void deployment() {
        ZipInputStream zipInputStream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/activitiApi/serviceTaskProcess.zip"));
        processEngine.getRepositoryService()
                .createDeployment()
                .addZipInputStream(zipInputStream)
                .name("serviceTaskProcess")
                .deploy();
    }

    @Test
    public void startProcessInstance() {
        processEngine.getRuntimeService().startProcessInstanceByKey("myProcess");
    }


    /**
     *
     * 这里需要说明的是 当使用serviceTask的时候，它默认开启的是同步，在配置文件中
     * <property name="asyncExecutorActivate" value="true"/> 这里默认是false，当设置成true的是开启异步执行
     * 任务到达这个节点就开始执行服务，其数据就会写入到act_ru_job表中
     *
     *
     */





}
