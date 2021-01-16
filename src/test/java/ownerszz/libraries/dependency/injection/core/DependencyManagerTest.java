package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.core.cold.dependency.ColdDependency;
import ownerszz.libraries.dependency.injection.model.TestInterfaceService;
import ownerszz.libraries.dependency.injection.model.TestObject;
import ownerszz.libraries.dependency.injection.model.TestObjectWithResolvableConstructor;
import ownerszz.libraries.dependency.injection.model.TestObjectWithSlowConstructor;
import ownerszz.libraries.dependency.injection.model.deep.dependencies.TestObjectChainedSlowConstructor;
import ownerszz.libraries.dependency.injection.model.deep.dependencies.TestObjectWithDeepDependencies;
import ownerszz.libraries.dependency.injection.model.deep.dependencies.TestObjectWithDeepSlowConstructor;
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
    public void createSimpleInstance() throws Throwable {
        TestObject testObject = DependencyManager.createInstance(TestObject.class);
        assertNotNull(testObject);
    }
    @Test
    public void createInstanceWithDependencies() throws Throwable {
        TestObjectWithResolvableConstructor test =  DependencyManager.createInstance(TestObjectWithResolvableConstructor.class);
        assertNotNull(test);
    }

    @Test
    public void createInstanceWithDeepDependencies() throws Throwable {
        TestObjectWithDeepDependencies test =  DependencyManager.createInstance(TestObjectWithDeepDependencies.class);
        assertNotNull(test);
    }



    @Test
    public void runRunnableInstances() throws Throwable {
        DependencyManager.runRunnableDependencies();
    }

    @Test
    public void resolveInterfacesImpl() throws Throwable{
       TestInterfaceService testInterfaceService = DependencyManager.createInstance(TestInterfaceService.class);
       assertEquals(3, testInterfaceService.getTestInterfaces().size());
    }

    @Test
    public void createAndFetchScopedInstances() throws Throwable{
        String firstKey = DependencyManager.getInstance().createScope();
        TestObject normalCreate  =  DependencyManager.createInstance(TestObject.class);
        TestObject firstScoped =  DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        TestObject shouldBeFirstScoped =  DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        assertNotEquals(normalCreate, firstScoped);
        assertEquals(firstScoped,shouldBeFirstScoped);
        String secondKey = DependencyManager.getInstance().createScope();
        TestObject secondScoped = DependencyManager.getInstance().createOrGetScopedInstance(secondKey, TestObject.class);
        assertNotEquals(firstScoped, secondScoped);
        DependencyManager.getInstance().destroyScope(firstKey);
        DependencyManager.getInstance().destroyScope(secondKey);
    }

    @Test
    public void destroyScope() throws Throwable{
        String firstKey = DependencyManager.getInstance().createScope();
        TestObject firstScoped =  DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
        DependencyManager.getInstance().destroyScope(firstKey);
       try {
            firstScoped =  DependencyManager.getInstance().createOrGetScopedInstance(firstKey, TestObject.class);
            fail("Key should've been destroyed");
       }catch (Throwable ignored){

       }
    }

    @Test
    public void createColdDependency() throws Throwable{
        long before = System.currentTimeMillis();
        //Constructor takes 10s to complete
        TestObjectWithSlowConstructor testObjectWithSlowConstructor = DependencyManager.createInstance(TestObjectWithSlowConstructor.class);
        long after = System.currentTimeMillis();
        assertTrue((after - before) / 1000 <= 3);
    }

    @Test
    public void invokeColdDependencyMethod() throws Throwable{
        long before = System.currentTimeMillis();
        //Constructor takes 10s to complete
        TestObjectWithSlowConstructor testObjectWithSlowConstructor = DependencyManager.createInstance(TestObjectWithSlowConstructor.class);
        long after = System.currentTimeMillis();
        assertTrue((after - before) / 1000 <= 3);
        testObjectWithSlowConstructor.setTextField("test");
        assertEquals("test",testObjectWithSlowConstructor.getTextField());
    }

    @Test
    public void createDeepColdDependencies() throws Throwable{
        long before = System.currentTimeMillis();
        //Constructor takes 10s to complete
        TestObjectWithDeepSlowConstructor testObjectWithDeepSlowConstructor = DependencyManager.createInstance(TestObjectWithDeepSlowConstructor.class);
        long after = System.currentTimeMillis();
        assertTrue((after - before) / 1000 <= 3);
        assertNotNull(testObjectWithDeepSlowConstructor);
        assertNotNull(testObjectWithDeepSlowConstructor.getObjectWithSlowConstructor());
        assertNotNull(testObjectWithDeepSlowConstructor.getTestObjectWithDeepDependencies());
        testObjectWithDeepSlowConstructor.getObjectWithSlowConstructor().setTextField("test");
        assertEquals("test",testObjectWithDeepSlowConstructor.getObjectWithSlowConstructor().getTextField());
    }

    @Test
    public void createChainedColdDependencies() throws Throwable{
        long before = System.currentTimeMillis();
        //Constructor takes 10s to complete
        TestObjectChainedSlowConstructor testObjectWithDeepSlowConstructor = DependencyManager.createInstance(TestObjectChainedSlowConstructor.class);
        long after = System.currentTimeMillis();
        assertTrue((after - before) / 1000 <= 3);
        assertNotNull(testObjectWithDeepSlowConstructor);
        assertNotNull(testObjectWithDeepSlowConstructor.getTestObjectWithDeepSlowConstructor());
        //COLD -> WARM -> COLD -> method
        testObjectWithDeepSlowConstructor.getTestObjectWithDeepSlowConstructor().getObjectWithSlowConstructor().setTextField("test");
        assertEquals("test",testObjectWithDeepSlowConstructor.getTestObjectWithDeepSlowConstructor().getObjectWithSlowConstructor().getTextField());
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