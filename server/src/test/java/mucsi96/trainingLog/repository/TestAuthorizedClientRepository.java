package mucsi96.trainingLog.repository;

import mucsi96.trainingLog.model.TestAuthorizedClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAuthorizedClientRepository extends JpaRepository<TestAuthorizedClient, String> {
  
}
