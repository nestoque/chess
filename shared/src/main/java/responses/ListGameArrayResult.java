package responses;

public record ListGameArrayResult(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName
) {
}
