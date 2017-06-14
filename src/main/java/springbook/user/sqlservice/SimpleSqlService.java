package springbook.user.sqlservice;

import springbook.user.exception.SqlRetrievalFailureException;

import java.util.Map;

/**
 * @author Kj Nam
 * @since 2017-06-13
 */
public class SimpleSqlService implements SqlService {
    private Map<String, String> sqlMap;

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public String getSql(String key) throws SqlRetrievalFailureException {
        String sql = sqlMap.get(key);
        if (sql == null) {
            throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없음");
        } else {
            return sql;
        }
    }
}
