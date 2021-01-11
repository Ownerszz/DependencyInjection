package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.model.TestInterfaceService;
import ownerszz.libraries.dependency.injection.model.TestObject;
import ownerszz.libraries.dependency.injection.model.TestObjectWithResolvableConstructor;
import ownerszz.libraries.dependency.injection.model.deep.dependencies.TestObjectWithDeepDependencies;
import ownerszz.libraries.dependency.injection.util.UsableClassesGenerator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DependencyManagerTest {

    @BeforeClass
    public static void setup() throws Throwable {
        DependencyManager.use(UsableClassesGenerator.generateConstructors());
        DependencyManager.invokeRegistrators();
    }

    @Test
    public void createSimpleInstance() throws Exception {
        TestObject testObject = (TestObject) DependencyManager.createInstance(TestObject.class);
        assertNotNull(testObject);
    }
    @Test
    public void createInstanceWithDependencies() throws Exception{
        TestObjectWithResolvableConstructor test = (TestObjectWithResolvableConstructor) DependencyManager.createInstance(TestObjectWithResolvableConstructor.class);
        assertNotNull(test);
    }

    @Test
    public void createInstanceWithDeepDependencies() throws Exception{
        TestObjectWithDeepDependencies test = (TestObjectWithDeepDependencies) DependencyManager.createInstance(TestObjectWithDeepDependencies.class);
        assertNotNull(test);
    }



    @Test
    public void runRunnableInstances() throws Exception {
        DependencyManager.runRunnableDependencies();
    }

    @Test
    public void resolveInterfacesImpl() throws Throwable{
       TestInterfaceService testInterfaceService = (TestInterfaceService) DependencyManager.createInstance(TestInterfaceService.class);
       assertEquals(3, testInterfaceService.getTestInterfaces().size());
    }

    @Test
    public void createAndFetchScopedInstances() throws Throwable{
        String firstKey = DependencyManager.getInstance().createScope();
        TestObject normalCreate  = (TestObject) DependencyManager.createInstance(TestObject.class);
        TestObject firstScoped = (TestObject) DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        TestObject shouldBeFirstScoped = (TestObject) DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        assertNotEquals(normalCreate, firstScoped);
        assertEquals(firstScoped,shouldBeFirstScoped);
        String secondKey = DependencyManager.getInstance().createScope();
        TestObject secondScoped = (TestObject) DependencyManager.getInstance().createOrGetScopedInstance(secondKey, TestObject.class);
        assertNotEquals(firstScoped, secondScoped);
        DependencyManager.getInstance().destroyScope(firstKey);
        DependencyManager.getInstance().destroyScope(secondKey);
    }

    @Test
    public void destroyScope() throws Throwable{
        String firstKey = DependencyManager.getInstance().createScope();
        TestObject firstScoped = (TestObject) DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        DependencyManager.getInstance().destroyScope(firstKey);
       try {
            firstScoped = (TestObject) DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
            fail("Key should've been destroyed");
       }catch (Throwable ignored){

       }
    }


    @Test
    public void run() throws Throwable {
        DependencyManager.run(false);
    }


    @AfterClass
    public static void tearDown(){
        DependencyManager.refreshContext();
    }
}