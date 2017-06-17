package springbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springbook.user.dao.UserDao;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.CommonLevelUpgradePolicy;
import springbook.user.domain.UserLevelUpgradePolicy;
import springbook.user.service.DummyMailSender;
import springbook.user.service.OxmSqlService;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTest.TestUserService;
import springbook.user.sqlservice.SqlRegistry;
import springbook.user.sqlservice.SqlService;
import springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistry;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

/**
 * @author Kj Nam
 * @since 2017-06-17
 */
@Configuration
@EnableTransactionManagement
public class TestApplicationContext {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(H2)
                .addScript("classpath:/ddl-for-mysql.sql")
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }

    @Bean
    public UserDao userDao() {
        UserDaoJdbc dao = new UserDaoJdbc();
        dao.setDataSource(dataSource());
        dao.setSqlService(sqlService());
        return dao;
    }

    @Bean
    public UserService userService() {
        UserServiceImpl service = new UserServiceImpl();
        service.setUserDao(userDao());
        service.setMailSender(mailSender());
        service.setUserLevelUpgradePolicy(userLevelUpgradePolicy());
        return service;
    }

    @Bean
    public UserService testUserService() {
        TestUserService testService = new TestUserService();
        testService.setUserDao(userDao());
        testService.setMailSender(mailSender());
        testService.setUserLevelUpgradePolicy(userLevelUpgradePolicy());
        return testService;
    }

    @Bean
    public UserLevelUpgradePolicy userLevelUpgradePolicy() {
        return new CommonLevelUpgradePolicy();
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public SqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        sqlService.setSqlMapFile(new ClassPathResource("sqlmap.xml"));
        return sqlService;
    }

    private SqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(dataSource());
        return sqlRegistry;
    }

    private Unmarshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("springbook.user.sqlservice.jaxb");
        return marshaller;
    }
}
