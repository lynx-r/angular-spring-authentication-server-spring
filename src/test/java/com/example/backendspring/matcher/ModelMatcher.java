package com.example.backendspring.matcher;

import com.example.backendspring.JsonUtil;
import com.example.backendspring.TestUtil;
import org.junit.Assert;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

public class ModelMatcher<T, R> {
    protected Function<T, R> entityConverter;
    protected Class<T> entityClass;

    public ModelMatcher(Function<T, R> entityConverter, Class<T> entityClass) {
        this.entityConverter = entityConverter;
        this.entityClass = entityClass;
    }

    private T fromJsonValue(String json) {
        return JsonUtil.readValue(json, entityClass);
    }

    public void assertEquals(T expected, T actual) {
        Assert.assertEquals(entityConverter.apply(expected), entityConverter.apply(actual));
    }

    public void assertListEquals(List<T> expected, List<T> actual) {
        Assert.assertEquals(map(expected, entityConverter), map(actual, entityConverter));
    }

    public static <S, T> List<T> map(List<S> list, Function<S, T> converter) {
        return list.stream().map(converter).collect(Collectors.toList());
    }

    public ResultMatcher contentMatcher(T expect) {
        return content().string(
                new TestMatcher<T>(expect) {
                    @Override
                    protected boolean compare(T expected, String body) {
                        R actualForCompare = entityConverter.apply(fromJsonValue(body));
                        R expectedForCompare = entityConverter.apply(expected);
                        return expectedForCompare.equals(actualForCompare);
                    }
                });
    }

    public T fromJsonAction(ResultActions action) throws UnsupportedEncodingException {
        return fromJsonValue(TestUtil.getContent(action));
    }
}