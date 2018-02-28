package org.ksea.activiti6.base.demo;


import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

public class BaseActiviti6Test1 {

    //初识activiti
    @Test
    public void baseActiviti() {
        /**
         * 获取流程引擎
         */
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();


        //关闭引擎
        processEngine.close();
        System.exit(0);//退出系统

    }


}
