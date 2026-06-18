package gerenciamento.frotas.TrabFinal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "gerenciamento.frotas.TrabFinal.model.entity")
public class TrabFinalApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrabFinalApplication.class, args);
    }
}
