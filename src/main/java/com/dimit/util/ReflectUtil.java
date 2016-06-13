package com.dimit.util;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import javassist.CannotCompileException;
import javassist.CtClass;

public class ReflectUtil {
	public static void doMethod(Class<?> clz, CallBack cb, Predicate<Method> f)
			throws IllegalAccessException, IllegalArgumentException {
		Method[] mtds = clz.getMethods();
		for (Method m : mtds) {
			if (m != null && !f.test(m)) {
				continue;
			}
			cb.callBack(m);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObject(CtClass Ctclass) {
		try {
			Class<?> clz = Ctclass.toClass();
			T result = (T) clz.newInstance();
			Ctclass.detach();
			return result;
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new IllegalStateException();
	}
}
