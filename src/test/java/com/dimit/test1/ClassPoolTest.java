package com.dimit.test1;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Test;

import com.dimit.util.ReflectUtil;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
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
			// 当调用了writeFile(), toClass(), or
			// toBytecode()这三个方法后该CtClass对象将不允许继续进行修改
			// 这种现象叫做冷冻
			// 如果还想继续修改则可以调用Ctclass的defrost()方法进行除霜操作
			ct.toBytecode();
			 ct.defrost();
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

	/**
	 * 类搜索测试 javassist ClassPool支持多种Class search path 默认使用加载classpath下的类文件
	 * 
	 * @throws NotFoundException
	 */
	@Test
	public void clazzSearchTest() throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		// 在web容器中比如Jboss/ tomcat 中由于会使用多种类加载器 所以不能使用默认方式加载 可以使用如下方法进行加载
		pool.insertClassPath(new ClassClassPath(this.getClass()));

		// 指定加载路径
		/* pool.insertClassPath("/usr/local/javalib"); */

		// 通过网络加载
		/*
		 * ClassPath cp = new URLClassPath("www.javassist.org", 80, "/java/",
		 * "org.javassist."); pool.insertClassPath(cp);
		 */

		// 直接加载字节数组
		/*
		 * byte[] b = a byte array; String name = class name;
		 * cp.insertClassPath(new ByteArrayClassPath(name, b)); CtClass cc =
		 * cp.get(name);
		 */

		// 通过输入流加载
		/*
		 * InputStream ins = an input stream for reading a class file; CtClass
		 * cc = cp.makeClass(ins);
		 */
	}

	/**
	 * 修改类名测试 通过反射调用方法 注意:使用ClassPool.getDefault()方式获取的ClassPoll对象是一个单列对象
	 * 如果想不使用单列模式则可以使用new 进行创建
	 */
	@Test
	public void upNameTest() {
		ClassPool pool = ClassPool.getDefault();
		try {
			CtClass cc = pool.get("com.dimit.test1.Point");
			CtClass cc1 = pool.get("com.dimit.test1.Point"); // cc1 is identical
																// to cc.
			cc.setName("Pair");
			CtClass cc2 = pool.get("Pair"); // cc2 is identical to cc.
			CtClass cc3 = pool.get("com.dimit.test1.Point");

			CtClass ctClazz = pool.get("com.dimit.test1.Point");
			ctClazz.setName("Pair");
			Class<?> clz = ctClazz.toClass();
			Object obj = clz.newInstance();
			ReflectUtil.doMethod(clz, (m) -> {
				try {
					m.invoke(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} , (m) -> {
				return m.getName().equals("test");
			});

			// detach 从ClassPool中移除指定增强对象
			ctClazz.detach();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	// 加载文件
	@Test
	public void loadPathTest() {
		ClassPool pool = ClassPool.getDefault();
		try {
			pool.insertClassPath("E:/jar/javassist/ws/test");
			CtClass ct = pool.get("com.eyu.ahxy.module.alchemy.facade.AlchemyFacadeImpl");
			ct.getAnnotations();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void upMethodTest() throws InstantiationException, IllegalAccessException {
		// 直接new对象 当一个类已经被类加载器加载过 再调用toClass时会抛出异常 这时必须使用toClass(ClassLoader
		// loader)这个方法通过自定义类加载器进行加载
		/*
		 * Point p1 = new Point(); p1.say();
		 */
		ClassPool pool = ClassPool.getDefault();
		try {
			CtClass ct = pool.get("com.dimit.test1.Point");
			CtMethod ctm = ct.getDeclaredMethod("say");
			ctm.insertBefore("{System.out.println(\"jassist add before\");}");
			ctm.insertAfter("{System.out.println(\"jassist add after\");}");
			Point obj = ReflectUtil.getObject(ct);
			obj.say();
			// 直接new对象
			Point p = new Point();
			p.say();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (CannotCompileException e) {
			e.printStackTrace();
		}
	}
}
