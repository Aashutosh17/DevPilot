package devPilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(
        // Keep max in sync with maxLength in client/components/chat/chat-composer.tsx
        @NotBlank @Size(max = 4000) String content) {
}