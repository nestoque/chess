package responses;

import object.GameData;

import java.util.Collection;

public record ListGamesResult(
        Collection<ListGameArrayResult> gameList
) {
}
