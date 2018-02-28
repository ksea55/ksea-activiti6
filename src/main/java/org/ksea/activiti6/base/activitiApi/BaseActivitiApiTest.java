package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * 用于activiti 的Api方法的一些基本查询
 */
public class BaseActivitiApiTest {

    /**
     * 这里以act_id_group表为例,其所有方法均通用
     */


    private ProcessEngine processEngine;
    private IdentityService identityService;

    @Before
    public void before() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        identityService = processEngine.getIdentityService();
    }

    @Test //添加数据
    public void save() {
        for (int i = 1; i <= 10; i++) {
            Group group = identityService.newGroup(String.valueOf(i));
            group.setName("groupName_" + i);
            group.setType("groupType_" + i);
            identityService.saveGroup(group);
        }

        processEngine.close();

        System.exit(0);

    }


    //查询所有列表
    @Test
    public void list() {
        List<Group> groupList = identityService.createGroupQuery().list();
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }

    }


    @Test //分页查询，开始下标从0开始
    public void listPager() {

        List<Group> groupList = identityService.createGroupQuery().listPage(0, 2);
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }

        System.out.println("-------------------------------------------------------");

        groupList = identityService.createGroupQuery().listPage(1, 2);
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }

    }


    @Test //统计总条数
    public void count() {
        long count = identityService.createGroupQuery().count();
        System.out.println(count);
    }


    @Test //升序排序
    public void asc() {
        List<Group> groupList = identityService.createGroupQuery().orderByGroupId().asc().list();
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }
    }

    @Test //降序排序
    public void desc() {
        List<Group> groupList = identityService.createGroupQuery().orderByGroupId().desc().list();
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }
    }


    //双重排序
    @Test
    public void order() {

        List<Group> groupList = identityService.createGroupQuery()
                .orderByGroupId().desc() //先按id降序排序
                .orderByGroupName().asc() //再按名称升序排序
                .list();
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }
    }


    //组合条件查询
    @Test
    public void condition() {

        List<Group> groupList = identityService.createGroupQuery().groupNameLike("%group%") //先根据名称模糊查询,注意这里模糊查询需要自己写%号
                .groupType("groupType_1") //在根据类型
                .list();
        for (Group group : groupList) {
            System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
        }
    }

    @Test //返回单条结果集
    public void singleResult() {
        Group group = identityService.createGroupQuery().groupId("1").singleResult();
        System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
    }


    //使用sql语句查询
    public void nativeSql() {
        Group group = identityService.createNativeGroupQuery().sql("SELECT * FROM act_id_group WHERE ID_=#{id}")//这里#{id}就跟mybaits看着一样，其实是占位符
                                                              .parameter("id", "1") //前面是参数名,后面是参数值
                                                              .singleResult();

        System.out.println(group.getId() + "-" + group.getName() + "-" + group.getType());
    }

}
