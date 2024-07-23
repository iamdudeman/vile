package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.AssetIds;
import technology.sola.engine.game.GameSettings;
import technology.sola.engine.game.ai.Ai;
import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
import technology.sola.engine.graphics.gui.elements.ImageGuiElement;
import technology.sola.engine.graphics.gui.elements.SectionGuiElement;
import technology.sola.engine.graphics.gui.elements.TextGuiElement;
import technology.sola.engine.graphics.gui.elements.TextStyles;
import technology.sola.engine.graphics.gui.elements.input.ButtonGuiElement;
import technology.sola.engine.graphics.gui.style.BaseStyles;
import technology.sola.engine.graphics.gui.style.ConditionalStyle;
import technology.sola.engine.graphics.gui.style.property.CrossAxisChildren;
import technology.sola.engine.graphics.gui.style.property.Direction;
import technology.sola.engine.graphics.gui.style.property.MainAxisChildren;
import technology.sola.engine.graphics.gui.style.property.Visibility;
import technology.sola.engine.graphics.gui.style.theme.DefaultThemeBuilder;
import technology.sola.engine.graphics.gui.style.theme.GuiTheme;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

public class VialsBoardGuiBuilder {
  private final GuiTheme guiTheme = DefaultThemeBuilder.buildDarkTheme()
    .addStyle(ButtonGuiElement.class, List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setPadding(4)
        .build()
    )));
  private final GuiDocument guiDocument;
  private final Color playerColor = new Color(255, 194, 10);
  private final Color opponentColor = new Color(12, 123, 220);
  private Integer currentRolledPH = null;
  private boolean isNeutralizing = false;
  private final ConditionalStyle<BaseStyles> neutralizeActiveStyle = ConditionalStyle.always(
    BaseStyles.create().setBackgroundColor(new Color(195, 177, 104)).build()
  );
  private final ConditionalStyle<BaseStyles> visibilityHiddenStyle = ConditionalStyle.always(
    BaseStyles.create().setVisibility(Visibility.HIDDEN).build()
  );
  private final ConditionalStyle<TextStyles> visibilityHiddenTextStyle = ConditionalStyle.always(
    TextStyles.create().setVisibility(Visibility.HIDDEN).build()
  );
  private final ConditionalStyle<BaseStyles> portraitContainerStyle = ConditionalStyle.always(
    BaseStyles.create()
      .setWidth(128)
      .setHeight(128)
      .setPadding(4)
      .setBackgroundColor(new Color(96, 96, 96))
      .build()
  );
  private final ConditionalStyle<BaseStyles> portraitImageStyle = ConditionalStyle.always(
    BaseStyles.create()
      .setWidth("100%")
      .setHeight("100%")
      .build()
  );

  public VialsBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?> build(VialsBoard vialsBoard) {
    currentRolledPH = null;

    ImageGuiElement playerPortrait = new ImageGuiElement();
    playerPortrait.setAssetId(AssetIds.Images.PLAYER);
    playerPortrait.setStyle(List.of(portraitImageStyle));

    ImageGuiElement opponentPortrait = new ImageGuiElement();
    opponentPortrait.setAssetId(vialsBoard.ai.getAssetId());
    opponentPortrait.setStyle(List.of(portraitImageStyle));

    var topSection = new SectionGuiElement()
      .appendChildren(
        new SectionGuiElement()
          .setStyle(List.of(
            portraitContainerStyle,
            ConditionalStyle.always(BaseStyles.create().setBorderColor(playerColor).build())
          ))
          .appendChildren(playerPortrait),
        elementPlayerSection(vialsBoard),
        elementsAiSection(vialsBoard.ai),
        new SectionGuiElement()
          .setStyle(List.of(
            portraitContainerStyle,
            ConditionalStyle.always(BaseStyles.create().setBorderColor(opponentColor).build())
          ))
          .appendChildren(opponentPortrait)
      ).setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setGap(30)
          .build()
      )));

    var vialsBoardSection = new SectionGuiElement()
      .appendChildren(
        topSection,
        new TextGuiElement()
          .setId("dialogText")
          .setStyle(List.of(
            ConditionalStyle.always(
              TextStyles.create()
                .setPadding(8)
                .setBorderColor(Color.WHITE)
                .setHeight(100)
                .setWidth(400)
                .build()
            ),
            visibilityHiddenTextStyle
          )),
        elementVials(vialsBoard)
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setDirection(Direction.COLUMN)
            .setMainAxisChildren(MainAxisChildren.CENTER)
            .setCrossAxisChildren(CrossAxisChildren.CENTER)
            .setGap(12)
            .setPadding(8)
            .setHeight("100%")
            .setWidth("100%")
            .build()
        )
      ));

    guiTheme.applyToTree(vialsBoardSection);

    return vialsBoardSection;
  }

  private GuiElement<?> elementVials(VialsBoard vialsBoard) {
    var playerVials = vialsBoard.getPlayerVials();
    var opponentVials = vialsBoard.getOpponentVials();
    GuiElement<?>[] vialElements = new GuiElement<?>[playerVials.length + opponentVials.length];

    for (int i = 0; i < playerVials.length; i++) {
      vialElements[i] = elementVial(playerVials[i], vialsBoard, true, false);
    }

    for (int i = 0; i < opponentVials.length; i++) {
      vialElements[i + playerVials.length] = elementVial(opponentVials[i], vialsBoard, false, i == 0);
    }

    return new SectionGuiElement()
      .setId("vialsContainer")
      .appendChildren(vialElements)
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setDirection(Direction.ROW)
            .build()
        ))
      );
  }

  private GuiElement<?> elementVial(Vial vial, VialsBoard vialsBoard, boolean isPlayerVial, boolean hasExtraPadding) {
    Supplier<GuiElement<?>[]> buildVialContents = () -> {
      var contentsLength = vial.getContents().length;
      var contentGuiItems = new GuiElement[contentsLength];

      for (int i = 0; i < contentsLength; i++) {
        Integer value = vial.getContents()[i];

        contentGuiItems[i] = new TextGuiElement()
          .setText(value == null ? "--" : String.valueOf(value))
          .setStyle(List.of(ConditionalStyle.always(
            TextStyles.create()
              .setTextAlignment(TextStyles.TextAlignment.CENTER)
              .setPadding(4)
              .setWidth(50)
              .build()
          )));
      }

      return contentGuiItems;
    };

    var contentGuiItemsContainer = new SectionGuiElement()
      .appendChildren(buildVialContents.get())
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(30)
            .setPadding(4)
            .setCrossAxisChildren(CrossAxisChildren.CENTER)
            .setBorderColor(isPlayerVial ? playerColor : opponentColor)
            .build()
        )
      ));

    var pourText = new TextGuiElement()
      .setText("Pour");
    var pourButton = new ButtonGuiElement();

    pourButton.appendChildren(
      pourText
    );

    pourButton.setOnAction(() -> {
      if (!vialsBoard.isPlayerTurn()) {
        return;
      }

      if (currentRolledPH == null && !isNeutralizing) {
        return;
      }

      if (isNeutralizing) {
        vial.neutralizeTop();
        Knowledge knowledge = isPlayerVial ? vialsBoard.playerKnowledge : vialsBoard.ai.getKnowledge();

        knowledge.neutralize();

        var neutralizeButton = guiDocument.findElementById("neutralizeButton", ButtonGuiElement.class);

        if (knowledge.getCurrentNeutralizeAgents() == 0) {
          neutralizeButton.setDisabled(true);
        }

        isNeutralizing = false;
        neutralizeButton.styles().removeStyle(neutralizeActiveStyle);
        guiDocument.findElementById("neutralizeText", TextGuiElement.class)
          .setText("Neutralize " + knowledge.getCurrentNeutralizeAgents() + "/" + knowledge.getNeutralizeAgents());
      } else {
        vial.addLiquidToTop(currentRolledPH);
      }

      setRolledPh(null);
      guiDocument.findElementById("rollButton", ButtonGuiElement.class).requestFocus();

      var contents = vial.getContents();
      var guiChildren = contentGuiItemsContainer.getChildren();

      for (int i = 0; i < contents.length; i++) {
        Integer value = contents[i];
        TextGuiElement guiElement = (TextGuiElement) guiChildren.get(i);

        guiElement.setText(value == null ? "--" : String.valueOf(value));
      }

      vialsBoard.endTurn();

      if (vial.isFull()) {
        if (isPlayerVial) {
          vialsBoard.playerKnowledge.takeDamage(vial.getDamage());
        } else {
          vialsBoard.ai.getKnowledge().takeDamage(vial.getDamage());
        }

        vial.reset();
        updateGameStateUi(vialsBoard);
        checkAndHandleGameDone(vialsBoard);
      }

      if (!vialsBoard.isGameDone()) {
        startAiTurn(vialsBoard);
      }
    });

    return new SectionGuiElement()
      .appendChildren(
        pourButton,
        contentGuiItemsContainer
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setCrossAxisChildren(CrossAxisChildren.CENTER)
            .setPaddingLeft(hasExtraPadding ? 20 : 0)
            .setGap(10)
            .build()
        )
      ))
      ;
  }

  private GuiElement<?> elementPlayerSection(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        elementRollButton(vialsBoard),
        new TextGuiElement()
          .setText("Health: " + vialsBoard.playerKnowledge.getFormattedCurrentHealth())
          .setId("playerHealth"),
        elementKnowledgeSection(vialsBoard.playerKnowledge, vialsBoard)
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(8)
            .setBorderColor(playerColor)
            .build()
        )
      ));
  }

  private GuiElement<?> elementRollButton(VialsBoard vialsBoard) {
    return new SectionGuiElement()
      .appendChildren(
        new ButtonGuiElement()
          .setOnAction(() -> {
            if (!vialsBoard.isPlayerTurn()) {
              return;
            }

            int nextPh = vialsBoard.brewNextPh();

            setRolledPh(nextPh);
          })
          .setId("rollButton")
          .appendChildren(
            new TextGuiElement()
              .setText("Brew")
              .setId("rollButtonText")
          ),
        new TextGuiElement()
          .setText("--")
          .setId("rollText")
      );
  }

  private GuiElement<?> elementKnowledgeSection(Knowledge knowledge, VialsBoard vialsBoard) {
    SectionGuiElement knowledgeSectionGuiElement = new SectionGuiElement();

    knowledgeSectionGuiElement.setStyle(List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setGap(4)
        .build()
    )));

    knowledgeSectionGuiElement
      .appendChildren(new TextGuiElement().setText("Knowledge"));

    ButtonGuiElement rerollButton = new ButtonGuiElement();
    TextGuiElement rerollText = new TextGuiElement()
      .setText("Rebrew " + knowledge.getCurrentRebrews() + "/" + knowledge.getReBrews());

    rerollButton.appendChildren(rerollText);

    rerollButton.setOnAction(() -> {
      if (!vialsBoard.isPlayerTurn()) {
        return;
      }

      if (currentRolledPH != null) {
        knowledge.reBrew();
      }

      int nextPh = vialsBoard.brewNextPh();

      setRolledPh(nextPh);
      rerollText.setText("Rebrew " + knowledge.getCurrentRebrews() + "/" + knowledge.getReBrews());

      if (knowledge.getCurrentRebrews() == 0) {
        rerollButton.setDisabled(true);
      }
    });

    knowledgeSectionGuiElement.appendChildren(rerollButton);

    if (knowledge.getNeutralizeAgents() > 0) {
      ButtonGuiElement neutralizeButton = new ButtonGuiElement();
      TextGuiElement neutralizeText = new TextGuiElement()
        .setText("Neutralize " + knowledge.getCurrentNeutralizeAgents() + "/" + knowledge.getNeutralizeAgents());

      neutralizeButton.setId("neutralizeButton");
      neutralizeText.setId("neutralizeText");
      neutralizeButton.appendChildren(neutralizeText);

      neutralizeButton.setOnAction(() -> {
        if (!vialsBoard.isPlayerTurn()) {
          return;
        }

        isNeutralizing = !isNeutralizing;

        if (isNeutralizing) {
          neutralizeButton.styles().addStyle(neutralizeActiveStyle);
        } else {
          neutralizeButton.styles().removeStyle(neutralizeActiveStyle);
        }

        guiDocument.findElementById("vialsContainer", SectionGuiElement.class).requestFocus();
      });

      knowledgeSectionGuiElement.appendChildren(neutralizeButton);
    }

    return knowledgeSectionGuiElement;
  }

  private GuiElement<?> elementsAiSection(Ai ai) {
    SectionGuiElement knowledgeSection = new SectionGuiElement();

    knowledgeSection.setStyle(List.of(ConditionalStyle.always(
      BaseStyles.create()
        .setGap(4)
        .build()
    )));

    knowledgeSection.appendChildren(
      new TextGuiElement()
        .setText("Health: " + ai.getKnowledge().getFormattedCurrentHealth())
        .setId("aiHealth"),
      new TextGuiElement().setText("Knowledge")
    );

    knowledgeSection.appendChildren(
      new TextGuiElement()
        .setText("Rerolls " + ai.getKnowledge().getCurrentRebrews() + "/" + ai.getKnowledge().getReBrews())
        .setId("aiRerolls")
    );

    if (ai.getKnowledge().getNeutralizeAgents() > 0) {
      knowledgeSection.appendChildren(
        new TextGuiElement()
          .setText("Neutralize " + ai.getKnowledge().getCurrentNeutralizeAgents() + "/" + ai.getKnowledge().getNeutralizeAgents())
          .setId("aiNeutralizeAgents")
      );
    }

    return new SectionGuiElement()
      .appendChildren(
        new TextGuiElement().setText(ai.getName()),
        knowledgeSection
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(8)
            .setBorderColor(opponentColor)
            .build()
        )
      ));
  }

  private void setRolledPh(Integer newPh) {
    var rollButton = guiDocument.findElementById("rollButton", ButtonGuiElement.class);
    var rollText = guiDocument.findElementById("rollText", TextGuiElement.class);
    var neutralizeButton = guiDocument.findElementById("neutralizeButton", ButtonGuiElement.class);

    if (newPh == null) {
      rollText.setText("--");
      rollButton.setDisabled(false);

      if (neutralizeButton != null) {
        neutralizeButton.setDisabled(false);
      }
    } else {
      rollText.setText(String.valueOf(newPh));
      rollButton.setDisabled(true);

      if (neutralizeButton != null) {
        neutralizeButton.setDisabled(true);
        neutralizeButton.styles().removeStyle(neutralizeActiveStyle);
        isNeutralizing = false;
      }

      guiDocument.findElementById("vialsContainer", SectionGuiElement.class).requestFocus();
    }
    currentRolledPH = newPh;
  }

  private void checkAndHandleGameDone(VialsBoard vialsBoard) {
    if (vialsBoard.isGameDone()) {
      boolean isPlayerWin = vialsBoard.isPlayerWin();

      String text = "You live another round!";

      if (isPlayerWin) {
        vialsBoard.playerKnowledge.incrementBattlesWon();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            guiDocument.setRootElement(
              new EventBoardGuiBuilder(guiDocument)
                .build(vialsBoard.playerKnowledge)
            );
            cancel();
            timer.cancel();
          }
        }, 3000);
      } else {
        vialsBoard.reduceLives();

        if (vialsBoard.getLives() > 0) {
          guiDocument.findElementById("rollButton", ButtonGuiElement.class)
              .setOnAction(() -> {
                vialsBoard.reset();

                guiDocument.setRootElement(
                  new VialsBoardGuiBuilder(guiDocument)
                    .build(vialsBoard)
                );
              });
          guiDocument.findElementById("rollButtonText", TextGuiElement.class)
              .setText("Retry");
        }

        text = switch (vialsBoard.getLives()) {
          case 0 -> "Your life has come to an end along with your greed for knowledge. Game over!";
          case 1 -> "You have one chance remaining. Make the most of it!";
          default -> "You lost, but you still have " + vialsBoard.getLives() + " chances remaining";
        };
      }

      guiDocument.findElementById("dialogText", TextGuiElement.class)
        .setText(text)
        .styles()
        .removeStyle(visibilityHiddenTextStyle);
    }
  }

  private void startAiTurn(VialsBoard vialsBoard) {
    Ai ai = vialsBoard.ai;
    Random random = new Random();

    var dialogText = guiDocument.findElementById("dialogText", TextGuiElement.class);
    String startTurnText = ai.startTurn(vialsBoard);

    dialogText.setText(startTurnText)
      .styles()
      .removeStyle(visibilityHiddenTextStyle);

    handleAiTurn(vialsBoard, random);
  }

  private void handleAiTurn(VialsBoard vialsBoard, Random random) {
    Timer timer = new Timer();

    long min = switch (GameSettings.AI_SPEED) {
      case 1 -> 3000;
      case 2 -> 2000;
      case 3 -> 750;
      default -> 2000;
    };
    long max = switch (GameSettings.AI_SPEED) {
      case 1 -> 5000;
      case 2 -> 3000;
      case 3 -> 1500;
      default -> 3000;
    };

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (vialsBoard.ai.isDone()) {
          guiDocument.findElementById("dialogText", TextGuiElement.class)
            .styles()
            .addStyle(visibilityHiddenTextStyle);

          vialsBoard.endTurn();

          checkAndHandleGameDone(vialsBoard);
          cancel();
          timer.cancel();
        } else {
          String actionText = vialsBoard.ai.nextAction(vialsBoard);

          guiDocument.findElementById("dialogText", TextGuiElement.class)
            .setText(actionText)
            .styles()
            .removeStyle(visibilityHiddenTextStyle);

          updateGameStateUi(vialsBoard);

          cancel();
          timer.cancel();
          handleAiTurn(vialsBoard, random);
        }
      }
    }, random.nextLong(min, max));
  }

  private void updateGameStateUi(VialsBoard vialsBoard) {
    SectionGuiElement vialsSection = guiDocument.findElementById("vialsContainer", SectionGuiElement.class);

    var playerVials = vialsBoard.getPlayerVials();
    var opponentVials = vialsBoard.getOpponentVials();
    Vial[] vials = new Vial[playerVials.length + opponentVials.length];

    for (int i = 0; i < playerVials.length; i++) {
      var vial = playerVials[i];
      vials[i] = vial;

      if (vial.isFull()) {
        vialsBoard.playerKnowledge.takeDamage(vial.getDamage());
        vial.reset();
      }
    }

    for (int i = 0; i < opponentVials.length; i++) {
      var vial = opponentVials[i];
      vials[i + playerVials.length] = opponentVials[i];

      if (vial.isFull()) {
        vialsBoard.ai.getKnowledge().takeDamage(vial.getDamage());
        vial.reset();
      }
    }

    for (int i = 0; i < vials.length; i++) {
      var vial = vials[i];
      var vialChild = vialsSection.getChildren().get(i);
      var contentsChildren = vialChild.getChildren().get(1);

      for (int j = 0; j < vial.getContents().length; j++) {
        Integer value = vial.getContents()[j];
        TextGuiElement contentsChild = (TextGuiElement) contentsChildren.getChildren().get(j);

        contentsChild.setText(
          value == null ? "--" : String.valueOf(value)
        );
      }
    }

    Ai ai = vialsBoard.ai;

    guiDocument.findElementById("aiRerolls", TextGuiElement.class)
      .setText("Rebrew " + ai.getKnowledge().getCurrentRebrews() + "/" + ai.getKnowledge().getReBrews());
    var aiNeutralizeText = guiDocument.findElementById("aiNeutralizeAgents", TextGuiElement.class);

    if (aiNeutralizeText != null) {
      aiNeutralizeText.setText("Neutralize " + ai.getKnowledge().getCurrentNeutralizeAgents() + "/" + ai.getKnowledge().getNeutralizeAgents());
    }

    guiDocument.findElementById("playerHealth", TextGuiElement.class)
      .setText("Health: " + vialsBoard.playerKnowledge.getFormattedCurrentHealth());
    guiDocument.findElementById("aiHealth", TextGuiElement.class)
      .setText("Health: " + vialsBoard.ai.getKnowledge().getFormattedCurrentHealth());
  }
}
