package org.ksea.activiti6.base.activitiApi;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class MyServicetaskJavaDelegate implements JavaDelegate {
    public void execute(DelegateExecution execution) {
        System.out.println("此处已经正在处理了");

    }
}
