package dependency.injection.core;

import dependency.injection.core.DependencyResolver;
import dependency.injection.distrib.SkeletonImpl;
import dependency.injection.distrib.Stubbed;
import dependency.injection.model.*;
import dependency.injection.util.UsableClassesGenerator;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.HashMap;

public class DependencyResolverTest {


    @Test
    public void resolveDependencies() throws Exception {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObject.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithResolvableConstructor.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithDefaultConstructor.class);
    }

    @Test
    public void dontResolve() throws Exception {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        temp.putIfAbsent(TestObjectWithNonResolvableConstructor.class,false);
        try {
            DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithNonResolvableConstructor.class);
            fail("Expected exception");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    @Test
    public void dontResolveSelfReferencing() throws Exception {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        temp.putIfAbsent(TestObjectWithNonResolvableSelfReferencingConstructor.class,false);
        try {
            DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithNonResolvableSelfReferencingConstructor.class);
            fail("Expected exception");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void dontResolveCircularDependency() throws Exception {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        temp.putIfAbsent(TestObjectWithNonResolvableCircularDependency1.class,false);
        temp.putIfAbsent(TestObjectWithNonResolvableCircularDependency2.class,false);
        try {
            DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithNonResolvableCircularDependency1.class);
            fail("Expected exception");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void resolveExtendedDependencies() throws Exception{
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        temp.putIfAbsent(SkeletonImpl.class,false);
        temp.putIfAbsent(Stubbed.class,false);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp,SkeletonImpl.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp,Stubbed.class);

    }
}