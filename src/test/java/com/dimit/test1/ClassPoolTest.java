package com.dimit.test1;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Test;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ClassPoolTest {

	/**
	 * 动态修改类的继承关系测试
	 */
	@Test
	public void updateSuperClzTest() {
		ClassPool pool = ClassPool.getDefault();
		try {
			CtClass ct = pool.get("com.dimit.test1.Rectangle");
			ct.setSuperclass(pool.get("com.dimit.test1.Point"));
			// 当调用了writeFile(), toClass(), or toBytecode()这三个方法后该CtClass对象将不允许继续进行修改
			// 这种现象叫做冷冻
			// 如果还想继续修改则可以调用Ctclass的defrost()方法进行除霜操作
			ct.toBytecode();
			// ct.defrost();
			ct.addMethod(CtMethod.make("public void init(){}", ct));
			Class<?> c = ct.toClass();
			System.out.println(c.getSuperclass().getName());
			for (Method m : c.getDeclaredMethods()) {
				System.out.println(m.getName());
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
