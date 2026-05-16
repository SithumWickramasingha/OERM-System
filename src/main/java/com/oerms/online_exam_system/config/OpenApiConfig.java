package com.oerms.online_exam_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig — configures the Swagger UI with JWT Bearer token support.
 *
 * What this does:
 *  1. @SecurityScheme   → Registers "bearerAuth" as a global HTTP Bearer scheme.
 *                         This is what makes the 🔒 "Authorize" button appear in
 *                         Swagger UI with a text input for the JWT token.
 *
 *  2. @OpenAPIDefinition → Sets API metadata (title, version, contact) and declares
 *                          "bearerAuth" as a global security requirement, so every
 *                          endpoint automatically shows the 🔒 lock icon and sends
 *                          the Authorization: Bearer <token> header when a token
 *                          has been entered.
 *
 * Usage in Swagger UI:
 *  1. Call POST /api/auth/login → copy the token value from the response.
 *  2. Click the 🔒 "Authorize" button (top-right of Swagger UI).
 *  3. In the "bearerAuth" field, paste ONLY the token (NOT "Bearer <token>").
 *     Swagger adds the "Bearer " prefix automatically.
 *  4. Click "Authorize" → "Close".
 *  5. All subsequent requests will include: Authorization: Bearer <token>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "Online Examination & Result Management System API",
                version     = "1.0",
                description = "REST API for managing exams, subjects, results, and users. "
                            + "Use the Authorize button to enter your JWT Bearer token.",
                contact     = @Contact(
                        name  = "OERMS Dev Team",
                        email = "admin@oerms.com"
                )
        ),
        // Applies "bearerAuth" security globally — every endpoint shows the 🔒 icon
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name        = "bearerAuth",           // Must match the name in @SecurityRequirement
        description = "Paste your JWT token here (without the 'Bearer ' prefix).",
        scheme      = "bearer",               // HTTP scheme type
        type        = SecuritySchemeType.HTTP, // HTTP-based security (not apiKey/oauth2)
        bearerFormat = "JWT",                 // Informational — tells Swagger it's a JWT
        in          = SecuritySchemeIn.HEADER // Token goes in the Authorization header
)
public class OpenApiConfig {
    // No bean methods needed — the annotations above are sufficient.
    // springdoc-openapi reads them at startup and configures the Swagger UI automatically.
}
