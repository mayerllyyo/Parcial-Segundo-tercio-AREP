package co.edu.eci.proxyservice;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private static final String USER_AGENT = "Mozilla/5.0";

    private final String[] mathServiceUrls;
    private final AtomicInteger counter = new AtomicInteger(0);

    public ProxyController() {
        String url1 = System.getenv("MATH_SERVICE_1_URL");
        String url2 = System.getenv("MATH_SERVICE_2_URL");
        if (url1 == null || url1.isBlank()) {
            url1 = "http://localhost:8080";
        }
        if (url2 == null || url2.isBlank()) {
            url2 = "http://localhost:8080";
        }
        this.mathServiceUrls = new String[]{url1, url2};
    }

    @GetMapping("/tribseq")
    public String tribseq(@RequestParam(value = "value", defaultValue = "0") String value) throws IOException {
        String backendUrl = nextBackendUrl() + "/tribseq?value=" + value;
        return callBackend(backendUrl);
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
                "status", "UP",
                "mathService1", mathServiceUrls[0],
                "mathService2", mathServiceUrls[1]
        );
    }

    private String nextBackendUrl() {
        int index = Math.abs(counter.getAndIncrement() % mathServiceUrls.length);
        return mathServiceUrls[index];
    }

    private String callBackend(String targetUrl) throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setConnectTimeout(5000);
        con.setReadTimeout(10000);

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }
        } else {
            throw new IOException("Backend returned HTTP " + responseCode + " for " + targetUrl);
        }
    }
}
