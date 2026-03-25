package co.edu.eci.mathservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TribonacciController.class)
class TribonacciControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void tribseqReturnsCorrectSequence() throws Exception {
        mockMvc.perform(get("/tribseq?value=13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("Secuencia de Tribonacci"))
                .andExpect(jsonPath("$.input").value(13))
                .andExpect(jsonPath("$.output").value("0, 0, 1, 1, 2, 4, 7, 13, 24, 44, 81, 149, 274, 504"));
    }

    @Test
    void tribseqForZeroReturnsZero() throws Exception {
        mockMvc.perform(get("/tribseq?value=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.output").value("0"));
    }

    @Test
    void tribseqForTwoReturnsTwoTerms() throws Exception {
        mockMvc.perform(get("/tribseq?value=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.output").value("0, 0, 1"));
    }
}
