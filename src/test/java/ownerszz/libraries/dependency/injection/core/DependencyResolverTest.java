package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.distrib.SkeletonImpl;
import ownerszz.libraries.dependency.injection.distrib.Stubbed;
import ownerszz.libraries.dependency.injection.util.UsableClassesGenerator;
import static org.junit.Assert.*;

import org.junit.Test;
import ownerszz.libraries.dependency.injection.model.*;

import java.util.HashMap;

public class DependencyResolverTest {


    @Test
    public void resolveDependencies() throws Throwable {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObject.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithResolvableConstructor.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestObjectWithDefaultConstructor.class);
    }

    @Test
    public void dontResolve() throws Throwable {
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
    public void dontResolveSelfReferencing() throws Throwable {
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
    public void dontResolveCircularDependency() throws Throwable {
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
    public void resolveExtendedDependencies() throws Throwable {
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        temp.putIfAbsent(SkeletonImpl.class,false);
        temp.putIfAbsent(Stubbed.class,false);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp,SkeletonImpl.class);
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp,Stubbed.class);

    }

    @Test
    public void resolveInterfacesImpl() throws Throwable{
        HashMap<Class, Boolean> temp = UsableClassesGenerator.generateClasses();
        DependencyResolver.verifyClassDependencies(new HashMap<>(),temp, TestInterfaceService.class);
    }
}