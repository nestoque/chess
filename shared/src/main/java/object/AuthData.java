package object;

import java.util.UUID;

public record AuthData(
        String authToken,
        String username) {
}
