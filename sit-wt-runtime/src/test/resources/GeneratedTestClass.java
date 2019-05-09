import org.junit.Test;
import static org.junit.Assert.*;
import io.sitoolkit.wt.domain.tester.SitTesterTestBase;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

/**
 *
 */
public class GeneratedTestClass extends SitTesterTestBase {

    @Test
    public void test001() {
        test("001", null);
    }
    @Test
    public void test00_2() {
        test("00.2", null);
    }

    @Override
    protected String getTestScriptPath() {
        return "/path/to/script";
    }

    @Override
    protected String getSheetName() {
        return "TestScript";
    }
}
