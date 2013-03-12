package org.robotframework.jdave.contract;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jdave.ExpectationFailedException;
import jdave.IContract;

import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywordOverload;

public class RobotKeywordContract implements IContract {
    private final String methodName;

    public RobotKeywordContract(String methodName) {
        this.methodName = methodName;
    }

    public void isSatisfied(Object obj) throws ExpectationFailedException {
        Method[] methods = findMethod(obj);
        boolean keywordAnnotationPresent = false;
        for (Method method: methods) {
            if (method.isAnnotationPresent(RobotKeyword.class)) {
                if (keywordAnnotationPresent) {
                    throw new ExpectationFailedException(methodName + " is annotated twice with @RobotKeyword");
                }
                keywordAnnotationPresent = true;
            } else if (!method.isAnnotationPresent(RobotKeywordOverload.class)) {
                throw new ExpectationFailedException("All instances of "+ methodName + " must be annotated either with @RobotKeyword or @RobotKeywordOverload");
            }
        }
        if (!keywordAnnotationPresent) {
            throw new ExpectationFailedException(methodName + " is not annotated with @RobotKeyword");
        }
    }

    private Method[] findMethod(Object obj) {
        List<Method> results = new ArrayList<Method>();
        for (Method method : obj.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                results.add(method);
            }
        }
        if (results.isEmpty())
            throw new ExpectationFailedException(methodName + " could not be found from " + obj.getClass().getName());
        return results.toArray(new Method[results.size()]);
    }
}
