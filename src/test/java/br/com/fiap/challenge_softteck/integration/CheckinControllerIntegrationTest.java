package br.com.fiap.challenge_softteck.integration;

import br.com.fiap.challenge_softteck.framework.auth.MockFirebaseAuthService;
import br.com.fiap.challenge_softteck.port.out.firebase.FormRepositoryPort;
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
 * Testes de integração para CheckinController.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class CheckinControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FormRepositoryPort formRepository;

    @MockitoBean
    private FormResponseRepositoryPort formResponseRepository;

    @MockitoBean
    private MockFirebaseAuthService mockFirebaseAuthService;

    @Test
    void testGetCheckins_WithValidToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/checkins")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.pagination").exists());
    }

    @Test
    void testGetCheckins_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/checkins")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCheckins_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/checkins"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCheckins_WithPagination_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/checkins")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.page").value(0))
                .andExpect(jsonPath("$.pagination.size").value(10));
    }

    @Test
    void testGetCheckins_WithDateFilter_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/checkins")
                .param("year", "2024")
                .param("month", "1")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetCheckins_WithInvalidPagination_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/checkins")
                .param("page", "-1")
                .param("size", "0")
                .header("Authorization", "Bearer test-user-123"))
                .andExpect(status().isBadRequest());
    }
}
