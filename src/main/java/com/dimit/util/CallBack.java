package com.dimit.util;

import java.lang.reflect.Method;

public interface CallBack {
	void callBack(Method m) throws IllegalAccessException, IllegalArgumentException;
}
