package responses;

import java.util.Collection;

public record ListGamesResult(
        Collection<ListGameArrayResult> games
) {
}
