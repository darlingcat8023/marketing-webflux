import com.estar.marketing.MarketingApplication;
import com.estar.marketing.admin.dao.AccountRepository;
import com.estar.marketing.admin.model.request.AccountSaveRequest;
import com.estar.marketing.admin.service.AccountService;
import com.estar.marketing.base.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

/**
 * @author xiaowenrou
 * @date 2023/4/17
 */
@SpringBootTest(classes = MarketingApplication.class)
public class MarketingTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    public void testAccount() {
        var string = """
                {
                    "account": "18701572636",
                    "mobile": "18643921235",
                    "password": "www123",
                    "accountName": "武雨萱",
                    "businessName": "市场",
                    "organizationId": "1",
                    "organizationName": "nwpwq",
                    "bindDevice": 0,
                    "deviceId": "",
                    "orderNumber": "1234556",
                    "active": 0,
                    "activeTime": null,
                    "type": "",
                    "accesses": [1,2]
                }
                """;
        var request = this.objectMapper.readValue(string, AccountSaveRequest.class);
        PublisherProbe<Void> probe = PublisherProbe.empty();
//        StepVerifier.create(this.accountService.saveAccount(Mono.just(request))).expectErrorMessage("mobile已经存在").verify();
    }

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void testBatch() {
        Mono.just(123).flatMap(item -> Mono.error(new BusinessException("1"))).flatMap(item -> Mono.just("234")).subscribe(System.out::println);
    }

}
