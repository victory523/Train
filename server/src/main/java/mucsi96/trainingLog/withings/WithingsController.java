package mucsi96.trainingLog.withings;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mucsi96.trainingLog.withings.data.WeightResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/withings")
@RequiredArgsConstructor
public class WithingsController {

    private final WithingsService withingsService;


    @GetMapping(value = "/weight", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody WeightResponse weight() {
        WeightResponse weightResponse = new WeightResponse();
        weightResponse.setWeight(withingsService.getFirstMeasureValue(withingsService.getMeasure()));
        return weightResponse;
    }

}
