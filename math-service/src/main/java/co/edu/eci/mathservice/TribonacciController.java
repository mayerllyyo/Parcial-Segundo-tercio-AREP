package co.edu.eci.mathservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TribonacciController {

    private static final int MAX_N = 1000;

    @GetMapping("/tribseq")
    public MathResponse tribseq(@RequestParam(value = "value", defaultValue = "0") int n) {
        if (n < 0) {
            n = 0;
        }
        if (n > MAX_N) {
            n = MAX_N;
        }
        String sequence = computeTribonacci(n);
        return new MathResponse("Secuencia de Tribonacci", n, sequence);
    }

    private String computeTribonacci(int n) {
        long[] t = new long[n + 1];
        for (int i = 0; i <= n; i++) {
            if (i == 0) {
                t[i] = 0;
            } else if (i == 1) {
                t[i] = 0;
            } else if (i == 2) {
                t[i] = 1;
            } else {
                t[i] = t[i - 1] + t[i - 2] + t[i - 3];
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= n; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(t[i]);
        }
        return sb.toString();
    }
}
