import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

/**
 * @author xiaowenrou
 * @data 2022/8/12
 */
public class CSVTest {

    @Test
    @SneakyThrows
    public void test() {
        Flux<Integer> f1 = Flux.just(1);
        Flux.just(3, 4).zipWith(f1, (a, b) -> {
            System.out.println(a + " - " + b);
            return a + b;
        }).subscribe();
    }

}
