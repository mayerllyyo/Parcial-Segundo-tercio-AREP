package co.edu.eci.proxyservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProxyController.class)
class ProxyControllerStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void statusEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/proxy/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.mathService1").exists())
                .andExpect(jsonPath("$.mathService2").exists());
    }

    @Test
    void corsHeadersPresentOnStatusEndpoint() throws Exception {
        mockMvc.perform(get("/proxy/status")
                        .header("Origin", "http://example.com"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
