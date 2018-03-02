package org.ksea.activiti6.base.activitiApi;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.zip.ZipInputStream;

/**
 * 前面BaseActivitiApiTest内容中说明了activiti中基本查询的api方法
 * 现在将对activiti中如何部署bpmn文件进行学习
 */
public class BaseActivitiApiToDeploymentTest {


    private ProcessEngine processEngine;

    @Before
    public void before() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }


    /**
     * 通过addClasspathResource方式进行部署
     */
    @Test
    public void addClasspathResourceDeployment() {

        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("bpmn/BaseActiviti6Test2/leave.bpmn")//bpmn文件
                .addClasspathResource("bpmn/BaseActiviti6Test2/leave.png") //流程图片
                .deploy();

    }


    /**
     * 通过流的方式addInputStream 方法来进行部署
     */
    @Test
    public void addInputStreamDeployment() {

        InputStream processInputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/BaseActiviti6Test2/leave.bpmn");
        InputStream processImageInputStream = this.getClass().getClassLoader().getResourceAsStream("bpmn/BaseActiviti6Test2/leave.png");
        processEngine.getRepositoryService()
                .createDeployment()
                .addInputStream("leaveProcess", processInputStream)
                .addInputStream("leaveProcessImage", processImageInputStream)
                .deploy();
        //注意这里前面的资源名称 将在act_ge_bytearray 映射其字段name_的值，如leaveProcess,leaveProcessImage
    }


    /**
     * 通过流的方式，一次性部署多个，通过压缩流addZipInputStream
     * 注意这里压缩之后的格式必须是.zip
     */
    @Test
    public void addZipInputStreamDeployment() {


        ZipInputStream processZipInputstream = new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream("bpmn/BaseActiviti6Test2/leave.zip"));

        processEngine.getRepositoryService()
                .createDeployment()
                .addZipInputStream(processZipInputstream)
                .deploy();
    }

    /**
     * 通过addBpmnModel 动态构建bpmnModel来部署流程，
     * 这种方式通过java api的方式来动态通过程序代码来构建bpmnModel方式
     * 是一种动态的方式，那么我们就可以通过这种方式动态的创建流程
     */
    @Test
    public void addBpmnModelDeployment() {

        //创建BPMN模型对象
        BpmnModel bpmnModel = new BpmnModel();

        //创建流程定义
        Process process = new Process();
        process.setName("defindProcess");
        process.setId("leaveProcess");

        //将流程定义添加到bpmnModel上
        bpmnModel.addProcess(process);

        //在bpmn中每个事件都是一个对象


        //创建开始事件对象
        StartEvent startEvent = new StartEvent();
        startEvent.setName("leaveStart");
        startEvent.setId("leaveStart");
        process.addFlowElement(startEvent); //将开始事件添加到流程上去


        //创建任务节点对象
        UserTask userTask = new UserTask();
        userTask.setId("dept");
        userTask.setName("部门领导审批");
        process.addFlowElement(userTask); //将任务节点绑定到流程中


        //创建结束事件对象
        EndEvent endEvent = new EndEvent();
        endEvent.setId("leaveEnd");
        endEvent.setName("leaveEnd");
        process.addFlowElement(endEvent);


        //创建链接对象
        SequenceFlow startToUsertask = new SequenceFlow();
        startToUsertask.setId("toDept");
        startToUsertask.setName("提交申请");

        startToUsertask.setSourceFlowElement(startEvent); //设置起始连线
        startToUsertask.setTargetFlowElement(userTask); //设置目标连线


        //将sequenceflow绑定到流程中
        process.addFlowElement(startToUsertask);


        //结束连线
        SequenceFlow endSequenceFlow = new SequenceFlow(userTask.getId(), endEvent.getId());
        endEvent.setName("审批结束");
        endEvent.setId("endFlow");
        process.addFlowElement(endSequenceFlow);


        processEngine.getRepositoryService()
                .createDeployment()
                .addBpmnModel("processLeave", bpmnModel)
                .deploy();
    }


    /**
     * 通过文本的方式进行部署addString
     * 这种方式不常用 了解即可
     */
    @Test
    public void addStringDeployment() {

        processEngine.getRepositoryService()
                .createDeployment()
                .addString("leave", "this is activiti6 process desc")
                .deploy();

    }


    /**
     * 通过addBytes通过字节的方式部署，这种方式与addString同理，了解即可
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void addBytesDeployment() throws UnsupportedEncodingException {
        processEngine.getRepositoryService()
                .createDeployment()
                .addBytes("leaveBytes", "this is activiti6 process desc to bytes ".getBytes("utf-8"))
                .deploy();
    }


    /**
     * activiti中的效验,默认是开启了对activiti部署文件的效验的，有的时候可能你不需要在部署的时候进行效验
     */
    @Test
    public void validation() {
        processEngine.getRepositoryService()
                .createDeployment()
                .disableSchemaValidation() //执行.bpmn文件进行部署的时候，禁止对其进行效验，哪怕文件是错误的也均可通过部署
                .disableBpmnValidation() //这种方式是在动态方式 通过BpmnModel方式，禁止对其效验
                .addClasspathResource("")
                .deploy();
    }


    /**
     * 查询流程bpmn文件信息 以及流程图片 通过getResourceAsStream方法的方式
     */
    @Test
    public void getResourceAsStream() throws IOException {

        InputStream leaveProcess = processEngine.getRepositoryService()
                .getResourceAsStream("12501", "leaveProcess"); //注意这种方式通过部署id以及资源名称，其资源名称必须与数据库保存的一致
        OutputStream outputStream = System.out;
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = leaveProcess.read(bytes)) > 0) {
            outputStream.write(bytes);
        }
        outputStream.flush();

        leaveProcess = processEngine.getRepositoryService()
                .getResourceAsStream("12501", "leaveProcessImage"); //获取流程定义的png图片

        outputStream = new FileOutputStream(new File("out.png"));
        while ((len = leaveProcess.read(bytes)) > 0) {
            outputStream.write(bytes);
        }
        outputStream.flush();

    }

    /**
     * 获取流程图片
     *
     * @throws IOException
     */
    @Test
    public void getProcessDiagram() throws IOException {

        String processDefinitionId = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .deploymentId("15001").singleResult().getId(); //根据部署id获取到流程定义id


        //获取到流程图片输入流
        InputStream inputStream = processEngine.getRepositoryService().getProcessDiagram(processDefinitionId);
        //将输入流转换成图片流
        BufferedImage image = ImageIO.read(inputStream);

        OutputStream outputStream = new FileOutputStream(new File("leave.png"));
        ImageIO.write(image, "png", outputStream);

    }


    @Test
    public void deleteDeployment() {
        //删除部署没有级联删除
        processEngine.getRepositoryService().deleteDeployment("1205");
        //true级联删除，包括在活动中的流程信息
        processEngine.getRepositoryService().deleteDeployment("1205", true);

    }

}
