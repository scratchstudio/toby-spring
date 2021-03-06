package springbook.user.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import springbook.config.AppContext;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * @author Kj Nam
 * @since 2017-05-11
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = AppContext.class)
public class UserDaoTest {
    @Autowired private UserDao dao;
    @Autowired private DataSource dataSource;
    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() throws SQLException {
        dao.deleteAll();

        user1 = new User("iamkyu1", "kyukyu", "password", "iamkyu1@mail.net", Level.BASIC, 1, 0);
        user2 = new User("iamkyu2", "kyukyu", "password", "iamkyu2@mail.net", Level.SILVER, 55, 10);
        user3 = new User("iamkyu3", "kyukyu", "password", "iamkyu3@mail.net", Level.GOLD, 100, 40);
    }

    @Test
    public void add() throws Exception {
        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        User user2 = dao.get(user1.getId());
        assertThat(user1.getId(), is(user2.getId()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {
        //given when
        dao.get("unknown_id");

        //then
        //exception
    }

    @Test
    public void getAll() {
        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User user1, User user2) {
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user1.getEmail(), user2.getEmail());
        assertEquals(user1.getLevel(), user2.getLevel());
        assertEquals(user1.getRecommend(), user2.getRecommend());
        assertEquals(user1.getLogin(), user2.getLogin());
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        dao.add(user1);
        dao.add(user1);
    }

    @Test
    public void sqlExceptionTranslate() {
        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException e) {
            SQLException sqlEx = (SQLException) e.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
            assertEquals(set.translate(null, null, sqlEx).getClass(), DuplicateKeyException.class);
        }
    }

    @Test
    public void update() {
        dao.add(user1);
        dao.add(user2);

        user1.setName("updateName");
        user1.setPassword("updatePassword");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(99);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        checkSameUser(user1, user1update);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);
    }
}
