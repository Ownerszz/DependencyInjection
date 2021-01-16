package ownerszz.libraries.dependency.injection.model;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyCreation;
import ownerszz.libraries.dependency.injection.core.cold.dependency.ColdDependency;

@Dependency(creationType = DependencyCreation.COLD)
public class TestObjectWithSlowConstructor {
    private String textField;
    /**
     * This class's constructor takes 10s to finish.
     */
    public TestObjectWithSlowConstructor() throws Exception{
        Thread.sleep(10000);
    }


    public String getTextField() {
        return textField;
    }


    public void setTextField(String textField) {
        this.textField = textField;
    }

}
