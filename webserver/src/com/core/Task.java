package com.core;

import com.mongodb.client.MongoDatabase;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class Task implements Job{
    private MongoDB mb = new MongoDB();
    private IDB dbManager = new DBM();
    public JobDetail job = null;
    private static Map<String, Scheduler> scheduler = new HashMap();
    private static SchedulerFactory sf = new StdSchedulerFactory();
    private static Map<String, JobKey> keys = new HashMap();
    public String name;
    private static Map<String, String> params = new HashMap();
    private boolean silence = false;
    MongoDatabase mdb = null;
    public boolean onceTask = false;
    public JhLog L = new JhLog();

    public Task() {
    }

    public abstract String execute(String var1, Connection var2) throws Exception;

    public void stop() throws Exception {
        if(this.name != null && this.name.length() > 0) {
            try {
                JobKey jobId = (JobKey)keys.get(this.name);
                Scheduler sche = (Scheduler)scheduler.get(this.name);
                if(sche == null) {
                    sche = sf.getScheduler();
                    scheduler.put(this.name, sche);
                }

                if(sche.isStarted()) {
                    sche.deleteJob(jobId);
                }
            } catch (Exception var3) {
                ;
            }

        } else {
            throw new Exception("Task class:" + this.getClass().getName() + ",没有设置名称");
        }
    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        String name = context.getJobDetail().getJobDataMap().getString("myJobName");
        String regulation = context.getJobDetail().getJobDataMap().getString("myJobRegulation");
        this.doExecute(name, regulation);
    }

    private void doExecute(final String name, final String regulation) {
        if(Resources.DEVELOPMENT && !this.onceTask) {
            Logger.warn("当前为测试环境，暂时不执行Task【" + name + "】。。");
        } else {
            final String executeId = DBUtils.uuid();
            this.name = name;
            final String rs = "";
            Jedis jedis = null;

            try {
                jedis = RedisUtils.getConnection();
                String e = jedis.get(SystemUtils.PROJECT_NAME + "__task__" + name);
                if(!"running".equals(e)) {
                    jedis.set(SystemUtils.PROJECT_NAME + "__task__" + name, "running");
                    if(!this.isSilence()) {
                        this.L.info("开始执行Task【" + name + "】,executeId【" + executeId + "】");
                    } else {
                        this.L.warn("开始执行Task【" + name + "】,executeId【" + executeId + "】");
                    }

                    final boolean ok = false;
                    Utils.execute.execute(new Runnable() {
                        public void run() {
                            Connection conn2 = null;

                            try {
                                conn2 = Task.this.dbManager.getConnection();
                                conn2.setAutoCommit(false);
                                EntityImpl e = new EntityImpl("sys_job", conn2);
                                e.setValue("id", executeId);
                                e.setValue("task_name", name);
                                e.setValue("start_time", new Date());
                                e.setValue("regulation", regulation);
                                e.setValue("status", "开始工作");
                                e.create();
                                conn2.commit();
                            } catch (Exception var6) {
                                Task.this.L.error(var6);
                            } finally {
                                Task.this.dbManager.freeConnection(conn2);
                            }

                        }
                    });
                    Connection conn = null;
                    DBM db = new DBM();
                    long start = System.currentTimeMillis();

                    try {
                        conn = db.getConnection();
                        conn.setAutoCommit(false);
                        rs = this.execute(executeId, conn);
                        conn.commit();
                        long isOK = System.currentTimeMillis();
                        long es = isOK - start;
                        if(!this.isSilence()) {
                            this.L.info("成功执行完Task【" + name + "】,executeId【" + executeId + "】.............ok(" + es + "ms)");
                        } else {
                            this.L.warn("成功执行完Task【" + name + "】,executeId【" + executeId + "】.............ok(" + es + "ms)");
                        }

                        ok = true;
                    } catch (Exception var30) {
                        try {
                            if(conn != null) {
                                conn.rollback();
                            }
                        } catch (Exception var29) {
                            ;
                        }

                        long result = System.currentTimeMillis();
                        long es1 = result - start;
                        this.L.error("错误执行Task【" + name + "】,executeId【" + executeId + "】:.............not ok(" + es1 + "ms)");
                        this.L.error(var30);
                        rs = var30.getMessage();
                    } finally {
                        db.freeConnection(conn);
                        this.mb.closeMongoDB();
                    }

                    Utils.execute.execute(new Runnable() {
                        public void run() {
                            Connection conn2 = null;

                            try {
                                conn2 = Task.this.dbManager.getConnection();
                                conn2.setAutoCommit(false);
                                String e = "执行成功";
                                if(!ok) {
                                    e = "执行失败";
                                }

                                String rsx = rs;
                                if(rs != null && rs.length() > 4000) {
                                    rsx = rs.substring(0, 4000);
                                }

                                try {
                                    EntityImpl sys_job = new EntityImpl("sys_job", conn2);
                                    sys_job.setValue("end_time", new Date());
                                    sys_job.setValue("status", e);
                                    sys_job.setValue("result", rsx);
                                    sys_job.setValue("id", executeId);
                                    sys_job.update();
                                    conn2.commit();
                                } catch (Exception var9) {
                                    ;
                                }

                                conn2.commit();
                            } catch (Exception var10) {
                                Task.this.L.error(var10);
                            } finally {
                                Task.this.dbManager.freeConnection(conn2);
                            }

                        }
                    });
                    return;
                }

                if(!this.isSilence()) {
                    this.L.info("开始执行Task【" + name + "】，直接退出，集群其他机器正在试行当前任务,executeId【" + executeId + "】");
                } else {
                    this.L.warn("开始执行Task【" + name + "】，直接退出，集群其他机器正在试行当前任务,executeId【" + executeId + "】");
                }
            } catch (Exception var32) {
                this.L.error(var32);
                return;
            } finally {
                jedis.del(new String[]{SystemUtils.PROJECT_NAME + "__task__" + name});
                RedisUtils.freeConnection(jedis);
            }

        }
    }

    public void setRegulation(String regulation) throws Exception {
        if(this.name != null && this.name.length() > 0) {
            if(!this.onceTask) {
                JobKey jobId = (JobKey)keys.get(this.name);
                Scheduler sche = (Scheduler)scheduler.get(this.name);
                if(sche == null) {
                    sche = sf.getScheduler();
                    scheduler.put(this.name, sche);
                }

                if(sche.isStarted()) {
                    sche.deleteJob(jobId);
                    this.L.info("重新设置task【" + this.name + "】执行规则【" + regulation + "】");
                } else {
                    this.L.info("设置task【" + this.name + "】执行规则【" + regulation + "】");
                }

                this.job = JobBuilder.newJob(this.getClass()).build();
                this.job.getJobDataMap().put("myJobName", this.name);
                this.job.getJobDataMap().put("myJobRegulation", regulation);
                jobId = this.job.getKey();
                keys.put(this.name, jobId);
                CronTrigger trigger = (CronTrigger)TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(regulation)).build();
                Date ft = sche.scheduleJob(this.job, trigger);
                if(!this.isSilence()) {
                    this.L.info(this.getClass().toString() + " has been scheduled to run at: " + Utils.parseData(ft, "yyyy-MM-dd HH:mm:ss") + " and repeat\r\n based on expression: " + trigger.getCronExpression());
                } else {
                    this.L.warn(this.getClass().toString() + " has been scheduled to run at: " + Utils.parseData(ft, "yyyy-MM-dd HH:mm:ss") + " and repeat\r\n based on expression: " + trigger.getCronExpression());
                }

                if(!sche.isStarted()) {
                    sche.start();
                }
            } else {
                this.doExecute(this.name, "--");
            }

        } else {
            throw new Exception("Task class:" + this.getClass().getName() + ",没有设置名称");
        }
    }

    public boolean isSilence() {
        return this.silence;
    }

    public void setSilence(boolean silence) {
        this.silence = silence;
    }

    public String getParameterValue(String key) {
        return (String)params.get(this.name + "__" + key);
    }

    public void setParamter(String key, String val) {
        params.put(this.name + "__" + key, val);
    }

    public MongoDatabase getMdb() throws Exception {
        if(this.mdb == null) {
            this.mdb = this.mb.getDB();
        }

        return this.mdb;
    }

    public String getRegulation() {
        return "";
    }
}
