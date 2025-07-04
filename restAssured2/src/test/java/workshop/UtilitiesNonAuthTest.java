package workshop;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeTest;

public class UtilitiesNonAuthTest extends UtilitiesTest {

    @BeforeTest
    public void SetEnvironmentAndAuthenticate() {

        setEnvironmentParameters();
    }
}
