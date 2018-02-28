Activiti6 基础学习篇章

一、Activiti的核心流程引擎 ProcessEngine,通过ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();就可以获取到流程引擎
当执行该方法之后,我们可以看到一下日志

10:01:37,984 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on engine with resource org/activiti/db/create/activiti.mysql.create.engine.sql
10:01:37,985 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - Found MySQL: majorVersion=5 minorVersion=7
10:02:16,426 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on history with resource org/activiti/db/create/activiti.mysql.create.history.sql
10:02:16,427 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - Found MySQL: majorVersion=5 minorVersion=7
10:02:23,888 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - performing create on identity with resource org/activiti/db/create/activiti.mysql.create.identity.sql
10:02:23,888 [main] INFO  org.activiti.engine.impl.db.DbSqlSession  - Found MySQL: majorVersion=5 minorVersion=7
10:02:27,042 [main] INFO  org.activiti.engine.impl.ProcessEngineImpl  - ProcessEngine default created
10:02:27,158 [main] INFO  org.activiti.engine.ProcessEngines  - initialised process engine default

通过日志分析，ProcessEngines.getDefaultProcessEngine();默认去项目根路径中取activiti.cfg.xml配置文件，通过该配置文件
其
performing create on engine with resource org/activiti/db/create/activiti.mysql.create.engine.sql
performing create on history with resource org/activiti/db/create/activiti.mysql.create.history.sql
create on identity with resource org/activiti/db/create/activiti.mysql.create.identity.sql

依次执行这三个文件,将创建起activiti的表结构