package io.github.mucsi96.workout.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

class WeightTests extends BaseIntegrationTest {

	@Test
	void display_todays_weight() {
    setupMocks();
    WebElement element = webDriver
        .findElement(By.xpath("//app-heading[contains(text(), \"Weight\")]"));
    assertThat(element.getText()).isEqualToIgnoringWhitespace("Weight 65.75");
	}

}
