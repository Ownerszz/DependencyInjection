package ownerszz.libraries.dependency.injection.model;

import ownerszz.libraries.dependency.injection.core.Dependency;
import ownerszz.libraries.dependency.injection.core.DependencyCreation;

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

    public String getSomething(){
        return "hey";
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }
}
