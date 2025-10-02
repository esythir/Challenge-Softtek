package br.com.fiap.challenge_softteck.integration;

import br.com.fiap.challenge_softteck.framework.auth.MockFirebaseAuthService;
import br.com.fiap.challenge_softteck.port.out.firebase.FormResponseRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para AnalysisWorkloadController.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AnalysisWorkloadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FormResponseRepositoryPort formResponseRepository;

    @MockitoBean
    private MockFirebaseAuthService mockFirebaseAuthService;

    @Test
    void testGetWorkloadAlerts_WithValidToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/analysis/workload-alerts")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.months").exists())
                .andExpect(jsonPath("$.data.totalAlerts").exists());
    }

    @Test
    void testGetWorkloadAlerts_WithMonthsParameter_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/analysis/workload-alerts")
                .param("months", "6")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetWorkloadAlerts_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/analysis/workload-alerts")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWorkloadAlerts_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/analysis/workload-alerts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetWorkloadAlerts_WithInvalidMonths_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/analysis/workload-alerts")
                .param("months", "-1")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isBadRequest());
    }
}
