package mucsi96.trainingLog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class HomeController {
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "<a href=\"/oauth2/authorization/withings-client\">Login</a>";
    }

    @ResponseBody
    @RequestMapping(value = "/login_success", method = RequestMethod.GET)
    public String login_success() {
        return "login success page";
    }
}
