package applica.puccj.tests.consoleapp;


import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.tests.consoleapp.annotations.AnnotationsTest;
import applica.puccj.tests.consoleapp.cglib.CGLibTest;
import applica.puccj.tests.consoleapp.constructors.ConstructorTest;
import applica.puccj.tests.consoleapp.equality.EqualityTest;
import applica.puccj.tests.consoleapp.getputfield.GetPutFieldTest;
import applica.puccj.tests.consoleapp.inheritance.InheritanceTest;
import applica.puccj.tests.consoleapp.methodsign.MethodSignTest;
import applica.puccj.tests.consoleapp.spring.SpringTest;
import applica.puccj.tests.consoleapp.springxml.SpringXmlTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static SpringXmlTest springXmlTest = new SpringXmlTest();
    private static CGLibTest cglibTest = new CGLibTest();
    private static ConstructorTest constructorTest = new ConstructorTest();
    private static GetPutFieldTest getPutFieldTest = new GetPutFieldTest();
    private static InheritanceTest inheritanceTest = new InheritanceTest();
    private static MethodSignTest methodSignTest = new MethodSignTest();
    private static SpringTest springTest = new SpringTest();
    private static EqualityTest equalityTest = new EqualityTest();
    private static AnnotationsTest annotationsTest = new AnnotationsTest();

    private static Log logger = LogFactory.getLog(App.class);

    public static void main( String[] args ) {

        logger.info("CIAO");
        Logger.getGlobal().setLevel(Level.ALL);
        logger.info("CIAO2");


        test();
        TestUtils.modifyAndWait(DynamicRuntime.instance().getSources(), true);
        test();
        TestUtils.modifyAndWait(DynamicRuntime.instance().getSources(), false);
        test();
    }

    private static void test() {
        //springXmlTest.doIt();
        cglibTest.doIt();
        constructorTest.doIt();
        getPutFieldTest.doIt();
        inheritanceTest.doIt();
        methodSignTest.doIt();
        //springTest.doIt();
        equalityTest.doIt();
        annotationsTest.doIt();
    }
}


