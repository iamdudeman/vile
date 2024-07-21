package technology.sola.engine.game.render.gui;

import technology.sola.engine.game.ai.Ai;
import technology.sola.engine.game.state.Knowledge;
import technology.sola.engine.game.state.Vial;
import technology.sola.engine.game.state.VialsBoard;
import technology.sola.engine.graphics.Color;
import technology.sola.engine.graphics.gui.GuiDocument;
import technology.sola.engine.graphics.gui.GuiElement;
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

  public VialsBoardGuiBuilder(GuiDocument guiDocument) {
    this.guiDocument = guiDocument;
  }

  public GuiElement<?> build(VialsBoard vialsBoard) {
    currentRolledPH = null;

    var topSection = new SectionGuiElement()
      .appendChildren(
        elementPlayerSection(vialsBoard),
        elementsAiSection(vialsBoard.ai)
      ).setStyle(List.of(ConditionalStyle.always(
        BaseStyles.create()
          .setDirection(Direction.ROW)
          .setGap(30)
          .build()
      )));

    var vialsBoardSection = new SectionGuiElement()
      .appendChildren(
        topSection,
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
            .setBorderColor(isPlayerVial ? new Color(255, 194, 10) : new Color(12, 123, 220))
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
    var playerSection = new SectionGuiElement()
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
            .setBorderColor(Color.WHITE)
            .build()
        )
      ));

    var winLoseText = new TextGuiElement()
      .setText("")
      .setId("winLoseText")
      .setStyle(List.of(
        ConditionalStyle.always(
          TextStyles.create()
            .setHeight(70)
            .setWidth(350)
            .build()
        ))
      );
    var tryAgainButton = new ButtonGuiElement()
      .setOnAction(() -> {
        vialsBoard.reset();

        guiDocument.setRootElement(
          new VialsBoardGuiBuilder(guiDocument)
            .build(vialsBoard)
        );
      })
      .setId("tryAgainButton")
      .appendChildren(
        new TextGuiElement()
          .setText("Retry")
      )
      .setStyle(List.of(visibilityHiddenStyle));
    var resultsSection = new SectionGuiElement()
      .setId("resultsSection")
      .appendChildren(
        winLoseText,
        tryAgainButton
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles.create()
            .setPadding(8)
            .setBorderColor(Color.WHITE)
            .build()
        ), visibilityHiddenStyle)
      );

    return new SectionGuiElement()
      .appendChildren(
        playerSection,
        resultsSection
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(4)
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

            int nextPh = vialsBoard.rollNextPh();

            setRolledPh(nextPh);
          })
          .setId("rollButton")
          .appendChildren(
            new TextGuiElement()
              .setText("Roll")
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
      .setText("Reroll " + knowledge.getCurrentRerolls() + "/" + knowledge.getRerolls());

    rerollButton.appendChildren(rerollText);

    rerollButton.setOnAction(() -> {
      if (!vialsBoard.isPlayerTurn()) {
        return;
      }

      if (currentRolledPH != null) {
        knowledge.reroll();
      }

      int nextPh = vialsBoard.rollNextPh();

      setRolledPh(nextPh);
      rerollText.setText("Reroll " + knowledge.getCurrentRerolls() + "/" + knowledge.getRerolls());

      if (knowledge.getCurrentRerolls() == 0) {
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
        .setText("Rerolls " + ai.getKnowledge().getCurrentRerolls() + "/" + ai.getKnowledge().getRerolls())
        .setId("aiRerolls")
    );

    if (ai.getKnowledge().getNeutralizeAgents() > 0) {
      knowledgeSection.appendChildren(
        new TextGuiElement()
          .setText("Neutralize " + ai.getKnowledge().getCurrentNeutralizeAgents() + "/" + ai.getKnowledge().getNeutralizeAgents())
          .setId("aiNeutralizeAgents")
      );
    }

    var aiDialog = new TextGuiElement()
      .setId("aiDialog")
      .setStyle(List.of(
        ConditionalStyle.always(
          TextStyles.create()
            .setPadding(8)
            .setBorderColor(Color.WHITE)
            .setHeight(100)
            .setWidth(200)
            .build()
        ), visibilityHiddenTextStyle)
      );

    var infoSection = new SectionGuiElement()
      .appendChildren(
        new TextGuiElement().setText(ai.getName()),
        knowledgeSection,
        aiDialog
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
            .setPadding(8)
            .setBorderColor(Color.WHITE)
            .build()
        )
      ));

    return new SectionGuiElement()
      .appendChildren(
        infoSection,
        aiDialog
      )
      .setStyle(List.of(
        ConditionalStyle.always(
          BaseStyles
            .create()
            .setGap(10)
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
      neutralizeButton.setDisabled(false);
    } else {
      rollText.setText(String.valueOf(newPh));
      rollButton.setDisabled(true);
      neutralizeButton.setDisabled(true);
      neutralizeButton.styles().removeStyle(neutralizeActiveStyle);
      isNeutralizing = false;

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
          guiDocument.findElementById("tryAgainButton", ButtonGuiElement.class).styles().removeStyle(visibilityHiddenStyle);
        }

        text = switch (vialsBoard.getLives()) {
          case 0 -> "Your life has come to an end along with your greed for knowledge. Game over!";
          case 1 -> "You have one chance remaining. Make the most of it!";
          default -> "You lost, but you still have " + vialsBoard.getLives() + " chances remaining";
        };
      }

      guiDocument.findElementById("winLoseText", TextGuiElement.class)
        .setText(text);
      guiDocument.findElementById("resultsSection", SectionGuiElement.class)
        .styles()
        .removeStyle(visibilityHiddenStyle);
    }
  }

  private void startAiTurn(VialsBoard vialsBoard) {
    Ai ai = vialsBoard.ai;
    Random random = new Random();

    var aiDialog  = guiDocument.findElementById("aiDialog", TextGuiElement.class);
    String startTurnText = ai.startTurn(vialsBoard);

    aiDialog.setText(startTurnText)
      .styles()
      .removeStyle(visibilityHiddenTextStyle);

    handleAiTurn(vialsBoard, random);
  }

  private void handleAiTurn(VialsBoard vialsBoard, Random random) {
    Timer timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (vialsBoard.ai.isDone()) {
          guiDocument.findElementById("aiDialog", TextGuiElement.class)
            .styles()
            .addStyle(visibilityHiddenTextStyle);

          vialsBoard.endTurn();

          checkAndHandleGameDone(vialsBoard);
          cancel();
          timer.cancel();
        } else {
          String actionText = vialsBoard.ai.nextAction(vialsBoard);

          guiDocument.findElementById("aiDialog", TextGuiElement.class)
            .setText(actionText)
            .styles()
            .removeStyle(visibilityHiddenTextStyle);

          updateGameStateUi(vialsBoard);

          cancel();
          timer.cancel();
          handleAiTurn(vialsBoard, random);
        }
      }
    }, random.nextLong(2000, 3000));
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
      .setText("Rerolls " + ai.getKnowledge().getCurrentRerolls() + "/" + ai.getKnowledge().getRerolls());
    guiDocument.findElementById("aiNeutralizeAgents", TextGuiElement.class)
      .setText("Neutralize " + ai.getKnowledge().getCurrentNeutralizeAgents() + "/" + ai.getKnowledge().getNeutralizeAgents());

    guiDocument.findElementById("playerHealth", TextGuiElement.class)
      .setText("Health: " + vialsBoard.playerKnowledge.getFormattedCurrentHealth());
    guiDocument.findElementById("aiHealth", TextGuiElement.class)
      .setText("Health: " + vialsBoard.ai.getKnowledge().getFormattedCurrentHealth());
  }
}
