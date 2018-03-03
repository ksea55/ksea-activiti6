package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 基于用户任务组件的Api学习
 */
public class BaseActivitiApiToUsertaskTest {


    /**
     * 任务候选人(组)
     * 任务持有人
     * 任务指定人
     */

    private ProcessEngine processEngine;


    @Before
    public void before() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }


    //创建用户与用户组
    @Test
    public void initUser() {
        IdentityService identityService = processEngine.getIdentityService();

        //创建id=1的用户组
        Group group1 = identityService.newGroup("1");

        //创建id=2的用户组
        Group group2 = identityService.newGroup("2");


        group1.setType("DEV");
        group1.setName("开发组");

        group2.setType("TEST");
        group2.setName("测试组");

        //保存用户组信息
        identityService.saveGroup(group1);
        identityService.saveGroup(group2);


        //创建用户
        User user1 = identityService.newUser("1");
        User user2 = identityService.newUser("2");
        User user3 = identityService.newUser("3");


        user1.setFirstName("k");
        user1.setLastName("sea");
        user1.setEmail("ksea@126.com");
        user1.setPassword("kseapwd");

        user2.setFirstName("m");
        user2.setLastName("kokk");
        user2.setEmail("kokk@126.com");
        user2.setPassword("kokkpwd");


        user3.setFirstName("jak");
        user3.setLastName("yoy");
        user3.setEmail("yoy@126.com");
        user3.setPassword("yoypwd");


        //保存用户信息
        identityService.saveUser(user1);
        identityService.saveUser(user2);
        identityService.saveUser(user3);


        //保存用户与用户组之间的关联关系
        identityService.createMembership(user1.getId(), group1.getId());
        identityService.createMembership(user2.getId(), group1.getId());
        identityService.createMembership(user3.getId(), group2.getId());


    }


    //部署流程并启动流程实例
    @Test
    public void deployProcess() {

        ZipInputStream bpmnFile = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/activitiApi/leaveProcess.zip"));

        //部署文件
        Deployment deployment = processEngine.getRepositoryService()
                .createDeployment()
                .addZipInputStream(bpmnFile)
                .name("请假流程")
                .key("leaveKey")
                .tenantId("leave_tenant")
                .deploy();


        //根据部署id查询到流程定义信息
        ProcessDefinition processDefinition = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();


        //此处在实际开发中，应该由实际请假人，填写好请假单之后，启动流程实例

        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("userId", "1"); //这里设置用户id=1的用户
        /**
         * 这里的变量是因为我们在发起流程节点 设置其 assignee=#{userId}
         */

        processEngine.getRuntimeService()
                // .startProcessInstanceByKey("key")   这里也可以根据key进行流程实例的启动，这里的key，是我们在定义bpmn文件信息中的id
                // .startProcessInstanceById(processDefinition.getId()); //此处没有设置启动变量
                .startProcessInstanceById(processDefinition.getId(), variables); //设置启动人的变量


    }

    //此刻流程到达发起流程节点,请假人进行任务的提交
    @Test
    public void completeTask() {

        //根据userId=1查询自己的任务，提交，并设置候选人candidate,或者候选人组candidateGroup，有或者下一个持有人owner，下一个指定的人assignee

        List<Task> taskList =
                processEngine.getTaskService()
                        .createTaskQuery()
                        .taskAssignee("1")
                        .orderByTaskCreateTime()
                        .desc()
                        .list(); //根据assignee查询当前指定人所有任务

        Task task = taskList.get(0); //取当前第一个

        processEngine.getTaskService().complete(task.getId());


    }


    //此刻流程任务节点到达 部门领导审批 节点，假设此刻该部门可以是 部门候选人，也可以是部门候选人组均可以审批，那么我们先给该节点设置其候选人
    @Test
    public void setCandidate() {
        List<Task> taskList = processEngine.getTaskService().createTaskQuery().list();
        Task task = taskList.get(0);
        processEngine.getTaskService().addCandidateGroup(task.getId(), "1");//给当前任务,设置候选人组 1=DEV

    }


    //此处根据候选人组 查看一下当前任务中候选人组有那些人，有或者根据当前登录人所在的候选人组查询任务
    @Test
    public void querytaskByCandidateGroup() {

        List<Task> taskList = processEngine.getTaskService()
                .createTaskQuery()
                //.taskCandidateUser("") //根据候选人查询
                //.taskCandidateOrAssigned("") //根据候选人或者指定人查询
                //.taskCandidateGroupIn("") //根据候选人组的集合
                .taskCandidateGroup("1")
                .list();

        for (Task task : taskList) {
            System.out.println("taskId:" + task.getId() + "_taskName:" + task.getName());
        }


        //取值第一个并为下一个节点设置任务持有人或者指定人
        Task task = taskList.get(0);

        processEngine.getTaskService().complete(task.getId());


    }


    @Test
    public void setOwnerOrAssignee() {
        //查询下一个节点为下一个节点设置

        List<Task> tasks = processEngine.getTaskService().createTaskQuery().list();
        Task task = tasks.get(0);

        //给当前任务设置任务持有人
        processEngine.getTaskService().setOwner(task.getId(), "1");
        //给当前任务设置指定人
        processEngine.getTaskService().setAssignee(task.getId(), "1");

    }


    @Test
    public void queryTaskByOwnerOrAssignee() {

        Task task = processEngine.getTaskService().createTaskQuery().taskOwner("1").singleResult();
        System.out.println(task.getName());

        task = processEngine.getTaskService().createTaskQuery().taskAssignee("1").singleResult();
        System.out.println(task.getName());


        //提交最终任务
        processEngine.getTaskService().complete(task.getId());
    }

}
