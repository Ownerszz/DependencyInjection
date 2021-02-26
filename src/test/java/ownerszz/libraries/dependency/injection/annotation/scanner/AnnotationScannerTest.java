package ownerszz.libraries.dependency.injection.annotation.scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ownerszz.libraries.dependency.injection.annotation.scanner.model.Annotation1;
import ownerszz.libraries.dependency.injection.annotation.scanner.model.Annotation2;
import ownerszz.libraries.dependency.injection.annotation.scanner.model.ClassWithMultipleAnnotations;
import ownerszz.libraries.dependency.injection.annotation.scanner.model.ClassWithNoAnnotations;
import ownerszz.libraries.dependency.injection.core.Dependency;

import java.lang.annotation.Annotation;

import static org.junit.Assert.*;
import static org.junit.Assert.*;
public class AnnotationScannerTest {


    @After
    public void tearDown() throws Exception {
        AnnotationScanner.refresh();
    }

    @Test
    public void isResolvable() {
        assertTrue(AnnotationScanner.isResolvable(ClassWithMultipleAnnotations.class,0));
    }

    @Test
    public void getAnnotationsOfClass() {
        AnnotationScanner.isResolvable(ClassWithMultipleAnnotations.class,0);
        assertEquals(4,AnnotationScanner.getAnnotationsOfClass(ClassWithMultipleAnnotations.class).size());
    }

    @Test
    public void isAnnotationPresent() {
        AnnotationScanner.isResolvable(ClassWithMultipleAnnotations.class,0);
        assertTrue(AnnotationScanner.isAnnotationPresent(ClassWithMultipleAnnotations.class, Annotation1.class));
        assertTrue(AnnotationScanner.isAnnotationPresent(ClassWithMultipleAnnotations.class, Annotation2.class));
    }

    @Test
    public void getAnnotation() {
        AnnotationScanner.isResolvable(ClassWithMultipleAnnotations.class,0);
        assertNotNull(AnnotationScanner.getAnnotation(ClassWithMultipleAnnotations.class,Annotation1.class));
        assertNotNull(AnnotationScanner.getAnnotation(ClassWithMultipleAnnotations.class,Annotation2.class));

    }

    @Test
    public void findAnnotationsInClass() {
        assertEquals(2,AnnotationScanner.findAnnotationsInClass(ClassWithMultipleAnnotations.class, Dependency.class).size());
    }

    @Test
    public void addAnnotationToClass() {
        Annotation1 annotation1 = new Annotation1(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation1.class;
            }
        };
        Annotation2 annotation2 = new Annotation2(){

            @Override
            public Class<? extends Annotation> annotationType() {
                return Annotation2.class;
            }
        };
        AnnotationScanner.addAnnotationToClass(ClassWithNoAnnotations.class,annotation1);
        AnnotationScanner.addAnnotationToClass(ClassWithNoAnnotations.class,annotation2);
        assertEquals(2,AnnotationScanner.getAnnotationsOfClass(ClassWithNoAnnotations.class).size());

    }


}