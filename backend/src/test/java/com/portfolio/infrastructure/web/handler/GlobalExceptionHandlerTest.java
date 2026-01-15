package com.portfolio.infrastructure.web.handler;

import com.portfolio.shared.dto.ApiResponse;
import com.portfolio.shared.exception.BusinessException;
import com.portfolio.shared.exception.DomainException;
import com.portfolio.shared.exception.EntityNotFoundException;
import com.portfolio.shared.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for GlobalExceptionHandler
 * Note: This test uses a test controller to trigger exceptions
 */
@WebMvcTest(GlobalExceptionHandlerTest.TestController.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test controller to trigger exceptions
     */
    @RestController
    static class TestController {
        @GetMapping("/test/business-exception")
        public String throwBusinessException() {
            throw new BusinessException("ERR_001", "Business rule violated");
        }

        @GetMapping("/test/domain-exception")
        public String throwDomainException() {
            throw new DomainException("Domain rule violated");
        }

        @GetMapping("/test/entity-not-found")
        public String throwEntityNotFoundException() {
            throw new EntityNotFoundException("Product", "123");
        }

        @GetMapping("/test/validation-exception")
        public String throwValidationException() {
            throw new ValidationException("Validation failed");
        }

        @GetMapping("/test/generic-exception")
        public String throwGenericException() {
            throw new RuntimeException("Generic error");
        }
    }

    @Test
    @DisplayName("Should handle BusinessException")
    void shouldHandleBusinessException() throws Exception {
        // Note: This test assumes GlobalExceptionHandler exists and handles BusinessException
        // If handler doesn't exist, this test will fail and needs the handler to be implemented
        mockMvc.perform(get("/test/business-exception"))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Should handle DomainException")
    void shouldHandleDomainException() throws Exception {
        mockMvc.perform(get("/test/domain-exception"))
            .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException")
    void shouldHandleEntityNotFoundException() throws Exception {
        mockMvc.perform(get("/test/entity-not-found"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle ValidationException")
    void shouldHandleValidationException() throws Exception {
        mockMvc.perform(get("/test/validation-exception"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle generic exceptions")
    void shouldHandleGenericExceptions() throws Exception {
        mockMvc.perform(get("/test/generic-exception"))
            .andExpect(status().is5xxServerError());
    }
}
