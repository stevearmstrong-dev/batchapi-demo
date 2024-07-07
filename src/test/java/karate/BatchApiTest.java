package karate;

import com.intuit.karate.junit5.Karate;

public class BatchApiTest {
    @Karate.Test
    Karate testBatchApi() {
        return Karate.run("classpath:karate/batch-api.feature");
    }
}
