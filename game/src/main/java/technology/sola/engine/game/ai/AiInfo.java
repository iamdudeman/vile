package technology.sola.engine.game.ai;

import technology.sola.engine.game.state.VialsBoard;

import java.util.function.Function;

public record AiInfo(
  String name,
  String assetId, Function<VialsBoard,
  String> greeting
) {
}
