package ui;

import audio.AudioController;
import audio.Sounds;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
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
  private Stage primaryStage;
  private Stack<ArrayList<Node>> backTree = new Stack<>();
  private ArrayList<Node> itemsOnScreen = new ArrayList<>();

  private Button singlePlayerBtn;
  private Button multiplayerBtn;
  private Button startGameBtn;
  private Button backBtn;
  private Button playBtn;
  private Button creditsBtn;
  private Button settingsBtn;
  private Button quitBtn;
  private Button createGameBtn;
  private Button joinGameBtn;
  private Button createLobbyBtn;
  private Button incrVolumeBtn;
  private Button decrVolumeBtn;
  private Button startMGameBtn;


  private ImageView logo;
  private ImageView incrView;
  private ImageView decrView;
  private ImageView creditsView;
  private ImageView settingsView;

  private Label lobbyStatusLbl;
  private Label loadingDots;
  private Label playersInLobby;

  private TextField nameEntry;
  private Button nameEntryBtn;

  private VBox multiplayerOptions;
  private VBox gameModeOptions;
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

    startGameBtn = buttonGenerator.generate(false, root, "Start", UIColours.GREEN, 45);
    StackPane.setAlignment(startGameBtn, Pos.CENTER);
    StackPane.setMargin(startGameBtn, new Insets(160, 0, 0, 0));
    startGameBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          client.startSinglePlayerGame();
        });

    this.singlePlayerBtn = buttonGenerator
        .generate(true, root, "Singleplayer", UIColours.WHITE, 45);
    this.singlePlayerBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          itemsOnScreen.add(startGameBtn);
          showItemsOnScreen();
        });

    this.multiplayerBtn = buttonGenerator.generate(true, root, "Multiplayer", UIColours.WHITE, 45);
    this.multiplayerBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          moveItemsToBackTree();
          itemsOnScreen.add(nameEntryOptions);
          showItemsOnScreen();
        });

    gameModeOptions = new VBox(10, singlePlayerBtn, multiplayerBtn);
    gameModeOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(gameModeOptions, Pos.CENTER);
    StackPane.setMargin(gameModeOptions, new Insets(100, 0, 0, 0));
    root.getChildren().add(gameModeOptions);
    gameModeOptions.setVisible(false);

    playBtn = buttonGenerator.generate(true, root, "Play", UIColours.GREEN, 35);
    playBtn.setText("Play");
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

//    smoothScalingBtn.setSelected(true);
    client.setRenderingMode(RenderingMode.SMOOTH_SCALING);

    creditsView = new ImageView("ui/Credits.png");
    creditsBtn = buttonGenerator.generate(false, root, creditsView);
    StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));

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

    nameEntryBtn = buttonGenerator.generate(true, root, "Continue", UIColours.GREEN, 40);
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
    musicLbl.setStyle(" -fx-font-size: 16pt ;");
    JFXToggleButton musicToggle = new JFXToggleButton();
    musicToggle.setSelected(true);
    musicToggle.setOnAction(event -> {
      audioController.playSound(Sounds.click);
      if (musicToggle.isSelected()) {
        audioController.setMusicVolume(0.5);
      } else {
        audioController.setMusicVolume(0);
      }
    });

    StackPane.setAlignment(musicLbl, Pos.TOP_CENTER);
    StackPane.setMargin(musicLbl, new Insets(150, 200, 0, 0));

    StackPane.setAlignment(musicToggle, Pos.TOP_CENTER);
    StackPane.setMargin(musicToggle, new Insets(128, 0, 0, 200));

    Label soundFXLbl = new Label("SoundFX:");
    soundFXLbl.setStyle(" -fx-font-size: 16pt ;");
    JFXToggleButton soundFXToggle = new JFXToggleButton();
    soundFXToggle.setSelected(true);
    soundFXToggle.setOnAction(event -> {
      audioController.playSound(Sounds.click);
      if (soundFXToggle.isSelected()) {
        audioController.setSoundVolume(0.5);
      } else {
        audioController.setSoundVolume(0);
      }
    });

    StackPane.setAlignment(soundFXLbl, Pos.CENTER);
    StackPane.setMargin(soundFXLbl, new Insets(0, 200, 0, 0));

    StackPane.setAlignment(soundFXToggle, Pos.CENTER);
    StackPane.setMargin(soundFXToggle, new Insets(0, 0, 0, 200));

    Label volumeLbl = new Label("Volume:");
    volumeLbl.setStyle(" -fx-font-size: 16pt ;");

    JFXSlider volumeSlider = new JFXSlider(0, 1, 0.5);

    volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> ov,
          Number old_val, Number new_val) {
        audioController.setSoundVolume(new_val.doubleValue());
        audioController.setMusicVolume(new_val.doubleValue());
      }
    });
    volumeSlider.setMaxWidth(200);
    volumeSlider.setMaxWidth(200);
    StackPane.setAlignment(volumeSlider, Pos.BOTTOM_CENTER);
    StackPane.setMargin(volumeSlider, new Insets(0, 0, 155, 220));


    incrView = new ImageView(new Image("ui/increaseVolume.png"));
//    incrVolumeBtn = buttonGenerator.generate(true, soundTabLayout, incrView);
//    incrView.setFitWidth(50);
//    incrVolumeBtn.setOnAction(
//        event -> {
//          audioController.playSound(Sounds.click);
//          audioController.increaseVolume();
//        });

    decrView = new ImageView("ui/decreaseVolume.png");
//    decrVolumeBtn = buttonGenerator.generate(true, soundTabLayout, decrView);
//    decrView.setFitWidth(50);
//    decrVolumeBtn.setOnAction(
//        event -> {
//          audioController.playSound(Sounds.click);
//          audioController.decreaseVolume();
//        });

    StackPane.setAlignment(volumeLbl, Pos.BOTTOM_CENTER);
    StackPane.setMargin(volumeLbl, new Insets(0, 200, 150, 0));

    soundTabLayout.getChildren()
        .addAll(musicLbl, musicToggle, soundFXLbl, soundFXToggle, volumeLbl, volumeSlider);
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
    resolutionCombo.setPromptText("Select a resolution...");
    resolutionCombo.setOnAction(event -> {
      System.out.println(resolutionCombo.getValue());
      audioController.playSound(Sounds.click);
      switch (resolutionCombo.getValue()) {
        case "1366x768":
          client.updateResolution(ScreenResolution.LOW);
          break;
        case "1920x1080":
          client.updateResolution(ScreenResolution.MEDIUM);
          break;
        case "2560x1440":
          client.updateResolution(ScreenResolution.HIGH);
          break;
        default:
          System.out.println("FAILED");
      }
    });

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
    scalingCombo.getItems().add("Integer");
    scalingCombo.getItems().add("Smooth");
    scalingCombo.setEditable(false);
    scalingCombo.setPromptText("Select a scaling method...");
    scalingCombo.setOnAction(event -> {
      switch (scalingCombo.getValue()) {
        case "None":
          client.setRenderingMode(RenderingMode.NO_SCALING);
          break;
        case "Standard":
          client.setRenderingMode(RenderingMode.STANDARD_SCALING);
          break;
        case "Smooth":
          client.setRenderingMode(RenderingMode.SMOOTH_SCALING);
          break;
        case "Integer":
          client.setRenderingMode(RenderingMode.INTEGER_SCALING);
          break;
        default:
          System.out.println("Setting rendering mode failed.");
      }
    });

    // Provide our own ListCells for the ComboBox
    alignComboText(scalingCombo);

    StackPane.setAlignment(scalingLbl, Pos.CENTER);
    StackPane.setAlignment(scalingCombo, Pos.CENTER);

    StackPane.setMargin(scalingLbl, new Insets(200, 400, 200, 0));
    StackPane.setMargin(scalingCombo, new Insets(200, 0, 200, 350));

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
            viewSettings = true;

            logo.setVisible(false);
            quitBtn.setVisible(false);
            hideItemsOnScreen();
            backBtn.setVisible(false);
            settingsTabs.setVisible(true);

          } else {

            viewSettings = false;
            logo.setVisible(true);
            quitBtn.setVisible(true);
            settingsTabs.setVisible(false);
            showItemsOnScreen();
            if (!isHome) {
              backBtn.setVisible(true);
            }
          }
        });

    startMGameBtn = buttonGenerator.generate(false, root, "Start", UIColours.GREEN, 40);
    StackPane.setAlignment(startMGameBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(startMGameBtn, new Insets(0, 0, 200, 0));
    startMGameBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.click);
          client.startMultiplayerGame();
        });

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
            logo,
            settingsView, decrView, incrView
        );

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

}
