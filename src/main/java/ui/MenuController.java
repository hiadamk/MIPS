package ui;

import audio.AudioController;
import audio.Sounds;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Client;
import utils.enums.RenderingMode;
import utils.enums.ScreenResolution;

/**
 * @author Adam Kona Class which handles the creation and functionality of components in the main
 * menu.
 */
public class MenuController {

  private AudioController audioController;
  private Client client;

  private boolean viewSettings = false;
  private boolean soundFX = true;
  private boolean music = true;
  private Stage primaryStage;
  private Stack<ArrayList<Node>> backTree = new Stack<>();
  private ArrayList<Node> itemsOnScreen = new ArrayList<>();

  private Button singlePlayerBtn;
  private Button multiplayerBtn;
  private Button startGameBtn;
  private Button backBtn;
  private Button playBtn;
  private Button musicBtn;
  private Button soundFxBtn;
  private Button creditsBtn;
  private Button settingsBtn;
  private Button quitBtn;
  private Button createGameBtn;
  private Button joinGameBtn;
  private Button createLobbyBtn;
  private Button incrVolumeBtn;
  private Button decrVolumeBtn;
  private Button startMGameBtn;
  private Button lowRes;
  private Button medRes;
  private Button highRes;

  private ImageView volumeImg;
  private ImageView lowResImageView;
  private ImageView medResImageView;
  private ImageView highResImageView;
  private ImageView playView;
  private ImageView logo;
  private ImageView starting;
  private ImageView startingM;
  private ImageView singlePlayerImageView;
  private ImageView multiplayerImageView;
  private ImageView musicOnView;
  private ImageView fxView;
  private ImageView incrView;
  private ImageView decrView;
  private ImageView creditsView;
  private ImageView quitView;
  private ImageView joinGameView;
  private ImageView createGameView;
  private ImageView createLobbyView;
  private ImageView continueView;
  private ImageView settingsView;
  private ImageView backImageView;

  private Image highResW;
  private Image highResG;
  private Image medResW;
  private Image medResG;
  private Image lowResW;
  private Image lowResG;

  private Label lobbyStatusLbl;
  private Label loadingDots;
  private Label playersInLobby;

  private TextField nameEntry;
  private Button nameEntryBtn;

  private VBox multiplayerOptions;
  private VBox gameModeOptions;
  private VBox resolutionOptions;
  private VBox nameEntryOptions;
  private VBox searchingForMutiplayers;
  private Font font;

  private List<ImageView> imageViews;
  private List<Double> originalViewWidths;
  private List<Double> minimumViewWidths;

  private ButtonGenerator buttonGenerator;

  private boolean isHome = true;

  /**
   * @param audio Global audio controller which is passed around the system
   * @param stage The game window
   * @author Adam Kona Constructor takes in the audio controller stage and game scene
   */
  public MenuController(AudioController audio, Stage stage, Client client) {
    this.audioController = audio;
    this.primaryStage = stage;
    this.client = client;
    originalViewWidths = new ArrayList<>();
    minimumViewWidths = new ArrayList<>();
    this.buttonGenerator = new ButtonGenerator();
  }

  /**
   * Hides the components on the screen
   */
  private void hideItemsOnScreen() {
    for (Node item : itemsOnScreen) {
      FadeTransition ft = new FadeTransition(Duration.millis(1000), item);
      ft.setFromValue(1.0);
      ft.setToValue(0);
      ft.play();
//      item.setVisible(false);
    }
    for (Node item : itemsOnScreen) {
      item.setVisible(false);
    }
  }

  /**
   * @author Adam Kona Hides the items currently on the screen and moves them onto the stack which
   * will store which components were previously showing
   */
  private void moveItemsToBackTree() {
    hideItemsOnScreen();
    ArrayList<Node> components = new ArrayList<>(itemsOnScreen);
    backTree.push(components);
    itemsOnScreen.clear();
  }

  /**
   * @author Adam Kona Shows items on the screen which have been previously set to hidden.
   */
  private void showItemsOnScreen() {
    for (Node item : itemsOnScreen) {
//      item.setVisible(true);
      FadeTransition ft = new FadeTransition(Duration.millis(1000), item);
      ft.setFromValue(0);
      ft.setToValue(1.0);
      ft.play();
    }
    for (Node item : itemsOnScreen) {
      item.setVisible(true);
    }
  }

  private void alignComboText(JFXComboBox comboBox) {
    comboBox.setCellFactory(lv -> new ListCell<String>() {

      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        setStyle("-fx-alignment: center");

        if (item != null && !empty) {
          setText(item);
        } else {
          setText(null);
        }
      }
    });
  }

  /**
   * @param s The screen resolution we want to update the game to.
   * @author Adam Kona Handles the changing of images corresponding to the different resolutions
   * available in the game.
   */
  private void updateView(ScreenResolution s) {
    switch (s) {
      case LOW:
        lowResImageView.setImage(lowResW);
        medResImageView.setImage(medResG);
        highResImageView.setImage(highResG);

        break;
      case MEDIUM:
        lowResImageView.setImage(lowResG);
        medResImageView.setImage(medResW);
        highResImageView.setImage(highResG);
        break;
      case HIGH:
        lowResImageView.setImage(lowResG);
        medResImageView.setImage(medResG);
        highResImageView.setImage(highResW);
        break;
    }
    client.updateResolution(s);
  }

  /**
   * Creates all the menu items and defines their functionality.
   *
   * @return The node containing the menu which will be returned to the game main window.
   */
  public Node createMainMenu() {

    try {
      this.font =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/ui/PressStart2P.ttf")), 26);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    StackPane root = new StackPane();
    root.setPrefSize(1920, 1080);

    ImageView bg = new ImageView("sprites/default/backgrounds/default.png");

    bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
    root.getChildren().add(bg);
    StackPane.setAlignment(bg, Pos.CENTER);

    logo = new ImageView("ui/MIPS-B.png");
    logo.preserveRatioProperty();
    StackPane.setAlignment(logo, Pos.TOP_CENTER);
    StackPane.setMargin(logo, new Insets(200, 0, 0, 0));
    logo.setPreserveRatio(true);
    root.getChildren().add(logo);
    logo.setVisible(true);

    starting = new ImageView("ui/start.png");
    startGameBtn = buttonGenerator.generate(false, root, "Start", UIColours.GREEN, 45);
    StackPane.setAlignment(startGameBtn, Pos.CENTER);
    StackPane.setMargin(startGameBtn, new Insets(160, 0, 0, 0));
    startGameBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          client.startSinglePlayerGame();
        });

    singlePlayerImageView = new ImageView("ui/Single-Player.png");
    this.singlePlayerBtn = buttonGenerator
        .generate(true, root, "Singleplayer", UIColours.WHITE, 45);
    this.singlePlayerBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          itemsOnScreen.add(startGameBtn);
          showItemsOnScreen();
        });

    multiplayerImageView = new ImageView("ui/Multiplayer.png");
    this.multiplayerBtn = buttonGenerator.generate(true, root, "Multiplayer", UIColours.WHITE, 45);
    this.multiplayerBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          itemsOnScreen.add(nameEntryOptions);
          showItemsOnScreen();
        });

    lowResW = new Image("ui/1366x768-W.png");
    lowResG = new Image("ui/1366x768-G.png");
    lowResImageView = new ImageView(lowResG);
    lowRes = buttonGenerator.generate(true, root, lowResImageView);
    lowResImageView.setFitWidth(350);
    lowRes.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          updateView(ScreenResolution.LOW);
        });

    medResW = new Image("ui/1920x1080-W.png");
    medResG = new Image("ui/1920x1080-G.png");
    medResImageView = new ImageView(medResG);
    medResImageView.setFitWidth(350);
    medRes = buttonGenerator.generate(true, root, medResImageView);
    medRes.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          updateView(ScreenResolution.MEDIUM);
        });

    highResW = new Image("ui/2650x1440-W.png");
    highResG = new Image("ui/2650x1440-G.png");
    highResImageView = new ImageView(highResG);
    highRes = buttonGenerator.generate(true, root, highResImageView);
    highResImageView.setFitWidth(350);
    highRes.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          updateView(ScreenResolution.HIGH);
        });

    resolutionOptions = new VBox(20, lowRes, medRes, highRes);
    resolutionOptions.setAlignment(Pos.CENTER_RIGHT);
    StackPane.setAlignment(resolutionOptions, Pos.CENTER_RIGHT);
    StackPane.setMargin(resolutionOptions, new Insets(100, 20, 0, 0));
    root.getChildren().add(resolutionOptions);

    resolutionOptions.setVisible(false);

    gameModeOptions = new VBox(10, singlePlayerBtn, multiplayerBtn);
    gameModeOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(gameModeOptions, Pos.CENTER);
    StackPane.setMargin(gameModeOptions, new Insets(100, 0, 0, 0));
    root.getChildren().add(gameModeOptions);
    gameModeOptions.setVisible(false);

    playBtn = buttonGenerator.generate(true, root, "Play", UIColours.GREEN, 35);
    playBtn.setText("Play");
    playView = new ImageView("ui/play.png");
    StackPane.setAlignment(playBtn, Pos.CENTER);
    StackPane.setMargin(playBtn, new Insets(160, 0, 0, 0));
    playBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          isHome = false;
          backBtn.setVisible(true);
          moveItemsToBackTree();
          itemsOnScreen.add(gameModeOptions);
          showItemsOnScreen();
        });

    ToggleGroup renderingModeGroup = new ToggleGroup();
    RadioButton standardScalingBtn = new RadioButton("standard");
    initialiseRenderingButtons(
        standardScalingBtn, 0, 0, root, renderingModeGroup, RenderingMode.STANDARD_SCALING);
    RadioButton noScalingBtn = new RadioButton("no scaling");
    initialiseRenderingButtons(
        noScalingBtn, 0, 60, root, renderingModeGroup, RenderingMode.NO_SCALING);
    RadioButton integerScalingBtn = new RadioButton("integer");
    initialiseRenderingButtons(
        integerScalingBtn, 0, 120, root, renderingModeGroup, RenderingMode.INTEGER_SCALING);
    RadioButton smoothScalingBtn = new RadioButton("smooth");
    initialiseRenderingButtons(
        smoothScalingBtn, 0, 180, root, renderingModeGroup, RenderingMode.SMOOTH_SCALING);

    //default rendering settings
    smoothScalingBtn.setSelected(true);
    client.setRenderingMode(RenderingMode.SMOOTH_SCALING);

    Image musicOn = new Image("ui/Music-On.png");
    Image musicOff = new Image("ui/Music-Off.png");
    musicOnView = new ImageView(musicOn);
    musicBtn = buttonGenerator.generate(false, root, musicOnView);
    StackPane.setAlignment(musicBtn, Pos.CENTER_LEFT);
    StackPane.setMargin(musicBtn, new Insets(0, 0, 25, 0));
    musicBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          if (music) {
            musicBtn.setGraphic(new ImageView(musicOff));
            music = false;
            audioController.setMusicVolume(0);
          } else {
            musicBtn.setGraphic(new ImageView(musicOn));
            music = true;
            audioController.setMusicVolume(0.5);
          }
        });

    Image soundFXOn = new Image("ui/SoundFX-On.png");
    Image soundFXOff = new Image("ui/SoundFX-Off.png");
    fxView = new ImageView(soundFXOn);
    soundFxBtn = buttonGenerator.generate(false, root, fxView);
    StackPane.setAlignment(soundFxBtn, Pos.CENTER_LEFT);
    StackPane.setMargin(soundFxBtn, new Insets(150, 0, 0, 0));
    fxView.setFitWidth(500);
    soundFxBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          if (soundFX) {
            fxView.setImage(soundFXOff);
            soundFX = false;
            audioController.setSoundVolume(0);
          } else {
            fxView.setImage(soundFXOn);
            soundFX = true;
            audioController.setSoundVolume(0.5);
          }
        });

    volumeImg = new ImageView("ui/Volume.png");
    StackPane.setAlignment(volumeImg, Pos.CENTER_LEFT);
    StackPane.setMargin(volumeImg, new Insets(400, 0, 0, 100));
    volumeImg.setVisible(false);
    volumeImg.setPreserveRatio(true);
    root.getChildren().add(volumeImg);
    volumeImg.setFitWidth(250);



    creditsView = new ImageView("ui/Credits.png");
    creditsBtn = buttonGenerator.generate(false, root, creditsView);
    StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));

    joinGameView = new ImageView("ui/join-game.png");
    joinGameBtn = buttonGenerator.generate(true, root, "Join a game", UIColours.WHITE, 40);
    joinGameBtn.setPickOnBounds(true);
    joinGameBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          lobbyStatusLbl.setText("Waiting for game to start");
          itemsOnScreen.add(searchingForMutiplayers);
          showItemsOnScreen();
          client.joinMultiplayerLobby();

        });

    createGameView = new ImageView("ui/create-game.png");
    createGameBtn = buttonGenerator.generate(true, root, "Create game", UIColours.WHITE, 40);
    createGameBtn.setPickOnBounds(true);
    createGameBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          itemsOnScreen.add(searchingForMutiplayers);
          itemsOnScreen.add(startMGameBtn);
          showItemsOnScreen();
          client.createMultiplayerLobby();
        });

    multiplayerOptions = new VBox(10, createGameBtn, joinGameBtn);
    multiplayerOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(multiplayerOptions, Pos.CENTER);
    StackPane.setMargin(multiplayerOptions, new Insets(100, 0, 0, 0));
    root.getChildren().add(multiplayerOptions);
    multiplayerOptions.setVisible(false);

    createLobbyBtn = buttonGenerator
        .generate(false, root, "Create lobby", UIColours.GREEN.WHITE, 40);
    StackPane.setAlignment(createGameBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(createGameBtn, new Insets(0, 0, 300, 0));
    Image createLobbyImg = new Image("ui/Create-Lobby.png");
    createLobbyView = new ImageView(createLobbyImg);

    lobbyStatusLbl = new Label("Searching for players");
    lobbyStatusLbl.setTextFill(Color.WHITE);
    lobbyStatusLbl.setFont(this.font);

    loadingDots = new Label(" .");
    loadingDots.setTextFill(Color.WHITE);
    loadingDots.setFont(this.font);

    playersInLobby = new Label("Players in lobby: 0");
    playersInLobby.setTextFill(Color.WHITE);
    playersInLobby.setFont(this.font);

    searchingForMutiplayers = new VBox(5, lobbyStatusLbl, loadingDots, playersInLobby);
    searchingForMutiplayers.setAlignment(Pos.CENTER);
    StackPane.setAlignment(searchingForMutiplayers, Pos.CENTER);
    root.getChildren().add(searchingForMutiplayers);
    searchingForMutiplayers.setVisible(false);

    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                event -> {
                  String statusText = loadingDots.getText();
                  loadingDots
                      .setText((" . . .".equals(statusText)) ? " ." : statusText + " .");
                }),
            new KeyFrame(Duration.millis(1000)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();

    nameEntry = new TextField();
    Font nameEntryFont = Font.font("Verdana");
    try {
      nameEntryFont =
          Font.loadFont(
              new FileInputStream(new File("src/main/resources/ui/PressStart2P.ttf")), 16);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    nameEntry.setPromptText("Please enter your player name...");
    nameEntry.setStyle(
        "-fx-text-inner-color: white; "
            + "-fx-prompt-text-fill: white; "
            + "-fx-background-color: transparent;");
    nameEntry.setFont(nameEntryFont);
    nameEntry.setAlignment(Pos.CENTER);

    Line clear = new Line(0, 100, 600, 100);
    clear.setStroke(Color.WHITE);
    VBox nameAndLine = new VBox(nameEntry, clear);
    nameAndLine.setAlignment(Pos.CENTER);

    continueView = new ImageView("ui/continue.png");
    nameEntryBtn = buttonGenerator.generate(true, root, "Continue", UIColours.GREEN.GREEN, 40);
    nameEntryBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          hideItemsOnScreen();
          this.client.setName(nameEntry.getText());
          itemsOnScreen.add(multiplayerOptions);
          showItemsOnScreen();
        });

    nameEntryOptions = new VBox(30, nameAndLine, nameEntryBtn);
    nameEntryOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(nameEntryOptions, Pos.CENTER);
    StackPane.setMargin(nameEntryOptions, new Insets(100, 250, 0, 250));
    nameEntryOptions.setPrefWidth(300);
    nameEntryOptions.setVisible(false);
    root.getChildren().add(nameEntryOptions);

    quitView = new ImageView("ui/quit.png");
    quitBtn = buttonGenerator.generate(true, root, "quit", UIColours.QUIT_RED, 30);
    StackPane.setAlignment(quitBtn, Pos.TOP_RIGHT);
    StackPane.setMargin(quitBtn, new Insets(50, 50, 0, 0));
    quitBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          System.exit(0);
        });

    //Creating the settings tab pane
    JFXTabPane settingsTabs = new JFXTabPane();
    settingsTabs.getStyleClass().add("floating");
    settingsTabs.setMaxWidth(850);
    settingsTabs.setMaxHeight(600);
    settingsTabs.setVisible(false);

    //Creating the sound tab
    Tab soundTab = new Tab();
    soundTab.setText("Sound");

    StackPane soundTabLayout = new StackPane();

    Label musicLbl = new Label("Music:");
    musicLbl.setStyle(" -fx-font-size: 18pt ;");
    JFXToggleButton musicToggle = new JFXToggleButton();

    StackPane.setAlignment(musicLbl, Pos.TOP_CENTER);
    StackPane.setMargin(musicLbl, new Insets(150, 200, 0, 0));

    StackPane.setAlignment(musicToggle, Pos.TOP_CENTER);
    StackPane.setMargin(musicToggle, new Insets(128, 0, 0, 200));

    Label soundFXLbl = new Label("SoundFX:");
    soundFXLbl.setStyle(" -fx-font-size: 18pt ;");
    JFXToggleButton soundFXToggle = new JFXToggleButton();

    StackPane.setAlignment(soundFXLbl, Pos.CENTER);
    StackPane.setMargin(soundFXLbl, new Insets(0, 200, 0, 0));

    StackPane.setAlignment(soundFXToggle, Pos.CENTER);
    StackPane.setMargin(soundFXToggle, new Insets(0, 0, 0, 200));

    Label volumeLbl = new Label("Volume:");
    volumeLbl.setStyle(" -fx-font-size: 18pt ;");

    incrView = new ImageView(new Image("ui/increaseVolume.png"));
    incrVolumeBtn = buttonGenerator.generate(true, soundTabLayout, incrView);
    incrView.setFitWidth(50);
    incrVolumeBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          audioController.increaseVolume();
        });

    decrView = new ImageView("ui/decreaseVolume.png");
    decrVolumeBtn = buttonGenerator.generate(true, soundTabLayout, decrView);
    decrView.setFitWidth(50);
    decrVolumeBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          audioController.decreaseVolume();
        });

    StackPane.setAlignment(volumeLbl, Pos.BOTTOM_CENTER);
    StackPane.setMargin(volumeLbl, new Insets(0, 200, 150, 0));

    StackPane.setAlignment(decrVolumeBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(decrVolumeBtn, new Insets(0, 0, 165, 200));

    StackPane.setAlignment(incrVolumeBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(incrVolumeBtn, new Insets(0, 0, 160, 400));

    soundTabLayout.getChildren()
        .addAll(musicLbl, musicToggle, soundFXLbl, soundFXToggle, volumeLbl);
    soundTab.setContent(soundTabLayout);

    //Creates the tab for graphics
    Tab graphicsTab = new Tab();
    graphicsTab.setText("Graphics");
    StackPane graphicsTabLayout = new StackPane();

    //Adding resolution label and combo box
    Label resolutionLbl = new Label("Resolution: ");
    resolutionLbl.setStyle(" -fx-font-size: 14pt ;");

    JFXComboBox<String> resolutionCombo = new JFXComboBox<>();
    resolutionCombo.getItems().add("1366x768");
    resolutionCombo.getItems().add("1920x1080");
    resolutionCombo.getItems().add("2560x1440");
    resolutionCombo.setEditable(false);
    resolutionCombo.setPromptText("Select a resolution");

    // Provide our own ListCells for the ComboBox
    alignComboText(resolutionCombo);

    StackPane.setAlignment(resolutionLbl, Pos.CENTER);
    StackPane.setAlignment(resolutionCombo, Pos.CENTER);

    StackPane.setMargin(resolutionLbl, new Insets(0, 300, 200, 0));
    StackPane.setMargin(resolutionCombo, new Insets(0, 0, 200, 300));

    //Adding resolution label and combo box
    Label scalingLbl = new Label("Resolution Scaling: ");
    scalingLbl.setStyle(" -fx-font-size: 14pt ;");

    JFXComboBox<String> scalingCombo = new JFXComboBox<>();
    scalingCombo.getItems().add("None");
    scalingCombo.getItems().add("Standard");
    scalingCombo.getItems().add("Smooth");
    scalingCombo.getItems().add("Smooth");
    scalingCombo.setEditable(false);
    scalingCombo.setPromptText("Select a resolution");

    // Provide our own ListCells for the ComboBox
    alignComboText(scalingCombo);

    StackPane.setAlignment(scalingLbl, Pos.CENTER);
    StackPane.setAlignment(scalingCombo, Pos.CENTER);

    StackPane.setMargin(scalingLbl, new Insets(200, 400, 200, 0));
    StackPane.setMargin(scalingCombo, new Insets(200, 0, 200, 300));

    graphicsTabLayout.getChildren()
        .addAll(resolutionLbl, resolutionCombo, scalingLbl, scalingCombo);
    graphicsTab.setContent(graphicsTabLayout);

    //Creates the tab for graphics
    Tab controlsTab = new Tab();
    controlsTab.setText("Controls");

    //Adds the tabs to the tab pane
    settingsTabs.getTabs().addAll(soundTab, graphicsTab, controlsTab);

    //Calculates the width of the tabs
    double tabWidth = settingsTabs.getMaxWidth() / settingsTabs.getTabs().size();
    settingsTabs.setTabMinWidth(tabWidth - 5);
    settingsTabs.setTabMaxWidth(tabWidth - 5);

    SingleSelectionModel<Tab> selectionModel = settingsTabs.getSelectionModel();
    selectionModel.select(0);

    root.getChildren().addAll(settingsTabs);

    settingsView = new ImageView("ui/settings.png");
    settingsBtn = buttonGenerator.generate(true, root, settingsView);
    StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
    StackPane.setMargin(settingsBtn, new Insets(50, 0, 0, 50));
    settingsView.setFitHeight(50);
    settingsView.setFitWidth(50);
    settingsBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);
          if (!viewSettings) {
//            musicBtn.setVisible(true);
//            soundFxBtn.setVisible(true);
//            creditsBtn.setVisible(true);
//            volumeImg.setVisible(true);
//            resolutionOptions.setVisible(true);
            viewSettings = true;
//            incrVolumeBtn.setVisible(true);
//            decrVolumeBtn.setVisible(true);
            logo.setVisible(false);
            quitBtn.setVisible(false);
            hideItemsOnScreen();
            backBtn.setVisible(false);
//            standardScalingBtn.setVisible(true);
//            noScalingBtn.setVisible(true);
//            integerScalingBtn.setVisible(true);
//            smoothScalingBtn.setVisible(true);
            settingsTabs.setVisible(true);

          } else {
//            musicBtn.setVisible(false);
//            soundFxBtn.setVisible(false);
            viewSettings = false;
            logo.setVisible(true);
            quitBtn.setVisible(true);
//            creditsBtn.setVisible(false);
//            resolutionOptions.setVisible(false);
//            volumeImg.setVisible(false);
//            incrVolumeBtn.setVisible(false);
//            decrVolumeBtn.setVisible(false);
//            standardScalingBtn.setVisible(false);
//            noScalingBtn.setVisible(false);
//            integerScalingBtn.setVisible(false);
//            smoothScalingBtn.setVisible(false);
            settingsTabs.setVisible(false);
            showItemsOnScreen();
            if (!isHome) {
              backBtn.setVisible(true);
            }
          }
        });

    startingM = new ImageView("ui/start.png");
    startMGameBtn = buttonGenerator.generate(false, root, "Start", UIColours.GREEN, 40);
    StackPane.setAlignment(startMGameBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(startMGameBtn, new Insets(0, 0, 200, 0));
    startMGameBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          client.startMultiplayerGame();
        });

    backImageView = new ImageView("ui/back.png");
    backBtn = buttonGenerator.generate(false, root, "back", UIColours.RED, 30);
    StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(backBtn, new Insets(0, 0, 100, 0));
    backBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.click);

          if (!backTree.isEmpty()) {
            hideItemsOnScreen();
            itemsOnScreen.clear();
            ArrayList<Node> toShow = backTree.pop();
            for (Node item : toShow) {
              itemsOnScreen.add(item);
            }
            showItemsOnScreen();
            if (backTree.isEmpty()) {
              backBtn.setVisible(false);
              isHome = true;
            }
          }
        });

    backTree.empty();
    itemsOnScreen.add(playBtn);

    imageViews =
        Arrays.asList(
            volumeImg,
            lowResImageView,
            medResImageView,
            highResImageView,
            playView,
            logo,
            starting,
            singlePlayerImageView,
            multiplayerImageView,
            musicOnView,
            fxView,
            incrView,
            decrView,
            creditsView,
            quitView,
            joinGameView,
            createGameView,
            createLobbyView,
            continueView,
            settingsView,
            backImageView,
            startingM);

    for (int i = 0; i < imageViews.size(); i++) {
      originalViewWidths.add(imageViews.get(i).getBoundsInLocal().getWidth());
      minimumViewWidths.add(originalViewWidths.get(i) * 0.4);
    }

    return root;
  }

  /**
   * @param newVal the new screen width.
   * @param oldVal the old screen width.
   * @author Adam Kona Updates the current size of all the images in the menu whilst preserving
   * their aspect ratio. The percentage change in the screen width is calculated and the size of the
   * images is changed along with it as long as this does not fall below 40% the original image size
   * and does not rise above the original image size.
   */
  public void scaleImages(double newVal, double oldVal) {

    for (int i = 0; i < imageViews.size(); i++) {
      ImageView currentView = imageViews.get(i);
      double currentWidth = currentView.getBoundsInLocal().getWidth();
      currentView.setPreserveRatio(true);
      currentView.setSmooth(true);

      double proposedWidth = Math.floor(currentWidth * (newVal / oldVal));
      if (proposedWidth > originalViewWidths.get(i)) {
        currentView.setFitWidth(originalViewWidths.get(i));
      } else if (proposedWidth < minimumViewWidths.get(i)) {
        currentView.setFitWidth(minimumViewWidths.get(i));
      } else {
        currentView.setFitWidth(Math.floor(currentWidth * (newVal / oldVal)));
      }
    }
  }

  public void initialiseRenderingButtons(
      RadioButton rBtn, int xMargin, int yMargin, StackPane root, ToggleGroup g, RenderingMode rm) {
    rBtn.setVisible(false);
    StackPane.setAlignment(rBtn, Pos.CENTER);
    StackPane.setMargin(rBtn, new Insets(yMargin, xMargin, 0, xMargin));
    root.getChildren().add(rBtn);
    rBtn.setOnAction(event -> client.setRenderingMode(rm));
    rBtn.setToggleGroup(g);
  }
}
