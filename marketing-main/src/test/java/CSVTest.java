import com.estar.marketing.base.exception.BusinessException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author xiaowenrou
 * @data 2022/8/12
 */
public class CSVTest {

    @Test
    @SneakyThrows
    public void test() {
        var duration = Duration.ofSeconds(3);
        Thread.sleep(5000);
    }

    @Test
    public void testDate() {
        LocalDateTime localDateTime = LocalDateTime.now().withYear(2022).withMonth(9).withDayOfMonth(27).minusDays(850L);
        System.out.println(localDateTime);
    }

    @Test
    public void testBatch() {
        Mono.empty().flatMap(item -> Mono.error(new BusinessException("1"))).switchIfEmpty(Mono.just("234")).subscribe(System.out::println);
    }

}
