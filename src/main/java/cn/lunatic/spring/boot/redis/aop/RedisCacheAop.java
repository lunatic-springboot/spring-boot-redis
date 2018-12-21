package cn.lunatic.spring.boot.redis.aop;

import cn.lunatic.spring.boot.redis.annotation.CacheRemove;
import cn.lunatic.spring.boot.redis.annotation.Cached;
import cn.lunatic.spring.boot.redis.annotation.Param;
import cn.lunatic.spring.boot.redis.bo.ParamFieldBO;
import cn.lunatic.spring.boot.redis.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ganfeng
 * @date 2018/12/21
 */
@Aspect
@Component
@Slf4j
public class RedisCacheAop {
    @Resource
    RedisCacheService redisCacheService;

    @Pointcut("@annotation(cn.lunatic.spring.boot.redis.annotation.Cached)")
    public void cached() {
    }

    @Around("cached()")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method sourceMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        Cached cacheAble = sourceMethod.getAnnotation(Cached.class);
        int expire = cacheAble.expire();
        String cacheKey = String.format("%s_%s", cacheAble.cacheName(), getKey(cacheAble.key(), joinPoint.getArgs(), sourceMethod));
        if (redisCacheService.hasKey(cacheKey)) {
            log.info("[Redis Cache]Hit The Cache... cacheKey={}", cacheKey);
            return redisCacheService.get(cacheKey);
        }
        // 执行方法
        Object processResult = joinPoint.proceed();
        // 加入缓存
        log.info("[Redis Cache]Add Cache... cacheKey={}", cacheKey);
        redisCacheService.set(cacheKey, processResult, expire);
        // 返回
        return processResult;
    }

    @Pointcut("@annotation(cn.lunatic.spring.boot.redis.annotation.CacheRemove)")
    public void cacheRemove() {
    }

    @After("cacheRemove()")
    public void cacheRemove(JoinPoint joinPoint) throws NoSuchMethodException {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method sourceMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        CacheRemove cacheAble = sourceMethod.getAnnotation(CacheRemove.class);
        String cacheKey = String.format("%s_%s", cacheAble.cacheName(), getKey(cacheAble.key(), joinPoint.getArgs(), sourceMethod));
        if (redisCacheService.hasKey(cacheKey)) {
            log.info("[Redis Cache]Remove Cache... cacheKey={}", cacheKey);
            redisCacheService.remove(cacheKey);
        }
    }

    /**
     * @param keys   缓存枚举配置 @Cached 的Key
     * @param args   缓存方法参数
     * @param method 缓存方法
     * @return
     */
    private String getKey(String[] keys, Object[] args, Method method) {
        if (ArrayUtils.isEmpty(keys) || ArrayUtils.isEmpty(args)) {
            return "";
        }
        Map<String, Integer> paramOrder = this.getMethodParameterNamesByAnnotation(method);
        List<ParamFieldBO> paramFieldBOList = this.convert(keys, paramOrder, args);
        if (CollectionUtils.isEmpty(paramFieldBOList)) {
            return "";
        } else {
            StringBuilder key = new StringBuilder();
            paramFieldBOList.forEach(paramFieldBO -> key.append(getFieldsObject(paramFieldBO)));
            return key.toString();
        }
    }

    private Map<String, Integer> getMethodParameterNamesByAnnotation(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (ArrayUtils.isEmpty(parameterAnnotations)) {
            return null;
        }
        Map<String, Integer> paramOrder = new HashMap();
        for ( int i = 0, lenI = parameterAnnotations.length; i < lenI; ++i ) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            for ( int j = 0, lenJ = parameterAnnotation.length; j < lenJ; ++j ) {
                Annotation annotation = parameterAnnotation[j];
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    paramOrder.put(param.value(), i);
                }
            }
        }
        return paramOrder;
    }

    private List<ParamFieldBO> convert(String[] keys, Map<String, Integer> paramOrder, Object[] objects) {
        List<ParamFieldBO> dataList = new ArrayList();
        for ( int i = 0, lenI = keys.length; i < lenI; i++ ) {
            String key = keys[i];
            String[] elements = key.split("\\.");
            ParamFieldBO data = ParamFieldBO.builder().object(objects[paramOrder.get(elements[0])]).fieldSet(key).build();
            dataList.add(data);
        }
        return dataList;
    }

    private Object getFieldsObject(ParamFieldBO paramFieldBO) {
        Object actualResult = paramFieldBO.getObject();
        String[] fields = paramFieldBO.getFieldSet().split("\\.");
        for ( int i = 1, lenI = fields.length; i < lenI && actualResult != null; ++i ) {
            actualResult = this.getFieldObject(actualResult, fields[i]);
        }
        return actualResult;
    }

    private Object getFieldObject(Object object, String fieldName) {
        Class clazz = object.getClass();
        while (true) {
            if (clazz != Object.class) {
                Field field;
                try {
                    field = clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                    continue;
                }
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                Object value;
                try {
                    value = field.get(object);
                } catch (IllegalAccessException e) {
                    return null;
                }
                if (field.isAccessible()) {
                    field.setAccessible(false);
                }
                return value;
            }
            return null;
        }
    }
}
