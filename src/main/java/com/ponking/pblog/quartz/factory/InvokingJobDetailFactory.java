package com.ponking.pblog.quartz.factory;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * @author peng
 * @date 2020/10/20--15:07
 * @Des
 **/
public class InvokingJobDetailFactory extends QuartzJobBean {

    /**
     * 计划任务所在类
     */
    private String targetObject;

    /**
     * 具体需要执行的计划任务
     */
    private String targetMethod;

    private ApplicationContext ctx;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Object obj = ctx.getBean(targetObject);
            Method m = null;
            try {
                m = obj.getClass().getMethod(targetMethod);
                //调用被代理对象的方法
                m.invoke(obj);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.ctx = applicationContext;
    }

    public void setTargetObject(String targetObject) {
        this.targetObject = targetObject;
    }

    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
}