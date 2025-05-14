package com.example.batch_runner.job;

import org.quartz.*;

import java.util.Date;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SimpleJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("SimpleJob 실행 시간 : " + new Date());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println("SimpleJob 종료 시간 : " + new Date());
    }
}