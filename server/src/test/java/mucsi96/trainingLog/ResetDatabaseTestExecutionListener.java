package mucsi96.trainingLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;

public class ResetDatabaseTestExecutionListener extends AbstractTestExecutionListener {
  @Autowired
  private DataSource dataSource;

  @Override
  public void prepareTestInstance(TestContext testContext) throws Exception {
    super.prepareTestInstance(testContext);
  }
}
