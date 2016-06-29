package applica.puccj.tests.consoleapp.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by bimbobruno on 27/10/15.
 */
public class EnhancedCGLibServiceFactory {

    public class MyInnerClass {

        public class MyInnerInnerClass {
            void innerInnerPrint() {
                System.out.println("inner inner print");
            }
        }

        void innerPrint() {
            System.out.println("inner print");
        }

    }

    public static CGLibService create(ClassLoader classLoader) {
        System.out.println("CGLib printed");

        Enhancer en = new Enhancer();
        if (classLoader != null) {
            en.setClassLoader(classLoader);
        }
        en.setSuperclass(CGLibService.class);
        en.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("Intercepting " + method);
                if (method.getName().equals("getValue")) {
                    return "enhanced";
                } else {
                    return proxy.invokeSuper(obj, args);
                }
            }
        });
        CGLibService svc = ((CGLibService) en.create());
        return svc;
    }
    
}
