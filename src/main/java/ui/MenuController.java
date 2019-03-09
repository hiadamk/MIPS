package ui;

import audio.AudioController;
import audio.Sounds;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.Client;
import server.NetworkUtility;
import utils.*;
import utils.Map;
import utils.enums.InputKey;
import utils.enums.RenderingMode;
import utils.enums.ScreenResolution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * @author Adam Kona Class which handles the creation and functionality of components in the main
 *         menu.
 */
public class MenuController {

  private AudioController audioController;
  private Client client;

  private boolean viewSettings = false;
  private Stage primaryStage;
  private Stack<ArrayList<Node>> backTree = new Stack<>();
  private ArrayList<Node> itemsOnScreen = new ArrayList<>();
  private ResourceLoader resourceLoader;

  private Button startGameBtn;
  private Button backBtn;
  private Button startMGameBtn;
  private Button settingsBtn;

  private Label lobbyStatusLbl;
  private Label loadingDots;
  private Label playersInLobby;
  private int numberOfPlayers;

  private TextField nameEntry;

  private VBox multiplayerOptions;
  private VBox gameModeOptions;
  private VBox nameEntryOptions;
  private VBox searchingForMultiplayers;

  private List<ImageView> imageViews;
  private List<Double> originalViewWidths = new ArrayList<>();
  private List<Double> minimumViewWidths = new ArrayList<>();

  private final String defaultToggleText = "Click to remap";
  private final String remapToggleText = "Click to save changes";
  private ArrayList<ToggleButton> keyToggleList = new ArrayList<>();
  private Label keyToggleStatus;

  private final String remapReady = "Press a key";
  private final String remapComplete = "Key remapped";
  private final String remapCancelled = "Remap cancelled";
  private KeyRemapping keyRemapper = new KeyRemapping();
  private InputKey toRemap = null;
  private KeyCode proposedChange = null;

  private Label upLbl;
  private Label leftLbl;
  private Label rightLbl;
  private Label downLbl;
  private Label useLbl;

  private MapPreview mapPreview = new MapPreview(1920, 1080);
  private ImageView mapView;
  private int mapsIndex = 0;
  private int numberOfMaps;
  private ArrayList<Image> mapImages = new ArrayList<>();
  private Button moveMapsLeftBtn;
  private Button moveMapsRightBtn;
  private Map currentMap;
  private ArrayList<Map> validMaps = new ArrayList<>();

  private boolean isHome = true;
  private boolean isInstructions = false;
  private boolean inLobby;
  private Thread playerNumberDiscovery;
  private MulticastSocket socket;

  /**
   * @param audio Global audio controller which is passed around the system
   * @param stage The game window
   * @author Adam Kona Constructor takes in the audio controller stage and game scene
   */
  public MenuController(AudioController audio, Stage stage, Client client,
                        ResourceLoader resourceLoader) {
    this.audioController = audio;
    this.primaryStage = stage;
    this.client = client;
    this.resourceLoader = resourceLoader;
  }

  Runnable lobbyPlayers = (() -> {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Thread.sleep(1000);
        System.out.println("Listening for number of players...");
        socket = new MulticastSocket(NetworkUtility.CLIENT_M_PORT);
        socket.setSoTimeout(3500);
        InetAddress group = NetworkUtility.GROUP;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
          NetworkInterface iface = interfaces.nextElement();
          if (iface.isLoopback() || !iface.isUp()) {
            continue;
          }

          Enumeration<InetAddress> addresses = iface.getInetAddresses();
          while (addresses.hasMoreElements()) {
            InetAddress addr = addresses.nextElement();
            socket.setInterface(addr);
            socket.joinGroup(group);
          }
        }

        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        byte[] data = new byte[packet.getLength()];
        System.arraycopy(buf, 0, data, 0, packet.getLength());
        String lobbyStatus = new String(data);
        System.out.println(new String(data));
        String[] statusPackets = lobbyStatus.split("\\|");
        System.out.println("Array: " + Arrays.toString(statusPackets));
        int players = Integer.parseInt(statusPackets[0]);
        int hostStatus = Integer.parseInt(statusPackets[1]);
        if (hostStatus == 0) {
          System.out.println("Server left lobby");
          client.leaveLobby();
          Platform.runLater(() -> {
            lobbyStatusLbl.setText("Host left the game");
            loadingDots.setVisible(false);
            playersInLobby.setVisible(false);
          });
          socket.close();
          Thread.currentThread().interrupt();
        }


        if (numberOfPlayers != players) {
          System.out.println("Updating player count");
          Platform.runLater(() -> {
            playersInLobby.setText("Players in lobby: " + players);
          });
        }
        socket.close();
      } catch (SocketTimeoutException e) {
        System.out.println("Server has disconnected");
        client.leaveLobby();
        Platform.runLater(() -> {
          lobbyStatusLbl.setText("Host left the game");
          loadingDots.setVisible(false);
          playersInLobby.setVisible(false);
        });
        Thread.currentThread().interrupt();
      } catch (InterruptedException e1) {
        System.out.println("Lobby players thread was interrupted. ");
      } catch (IOException e2) {

      }
    }
    if (!socket.isClosed() && socket != null) {
      socket.close();
    }

    System.out.println("Lobby players thread fully ended");
  });

  /**
   * Hides the components on the screen
   */
  private void hideItemsOnScreen() {
    for (Node item : itemsOnScreen) {
      if (!(isHome && item.equals(settingsBtn))) {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), item);
        ft.setFromValue(1.0);
        ft.setToValue(0);
        ft.play();
        item.setVisible(false);
      }

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
      FadeTransition ft = new FadeTransition(Duration.millis(1000), item);
      ft.setFromValue(0);
      ft.setToValue(1.0);
      ft.play();
    }
    for (Node item : itemsOnScreen) {
      item.setVisible(true);
    }
  }

  /**
   * Aligns text in the centre of combo box
   *
   * @param comboBox the combo box whose text needs to be aligned
   */
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

  private void showNextMap() {
    mapsIndex++;
    if (mapsIndex >= (numberOfMaps - 1)) {
      mapsIndex = numberOfMaps - 1;
      moveMapsRightBtn.setVisible(false);
    }
    moveMapsLeftBtn.setVisible(true);
    mapView.setImage(mapImages.get(mapsIndex));
    currentMap = validMaps.get(mapsIndex);
  }

  private void showPreviousMap() {
    mapsIndex--;
    if (mapsIndex <= 0) {
      mapsIndex = 0;
      moveMapsLeftBtn.setVisible(false);
    }
    moveMapsRightBtn.setVisible(true);
    mapView.setImage(mapImages.get(mapsIndex));
    currentMap = validMaps.get(mapsIndex);
  }

  /**
   * Creates all the menu items and defines their functionality.
   *
   * @return The node containing the menu which will be returned to the game main window.
   */
  public Node createMainMenu() {

    StackPane root = new StackPane();
    root.setPrefSize(1920, 1080);

    ImageView bg = new ImageView("sprites/default/backgrounds/default.png");

    bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
    root.getChildren().add(bg);
    StackPane.setAlignment(bg, Pos.CENTER);

    ImageView logo = new ImageView("ui/MIPS-B.png");
    logo.preserveRatioProperty();
    StackPane.setAlignment(logo, Pos.TOP_CENTER);
    StackPane.setMargin(logo, new Insets(200, 0, 0, 0));
    logo.setPreserveRatio(true);
    root.getChildren().add(logo);
    logo.setVisible(true);

    startGameBtn = ButtonGenerator.generate(false, root, "Start", UIColours.GREEN, 40);
    StackPane.setAlignment(startGameBtn, Pos.CENTER);
    StackPane.setMargin(startGameBtn, new Insets(0, 0, 0, 0));
    startGameBtn.setOnAction(
            e -> {
              audioController.playSound(Sounds.click);
              client.startSinglePlayerGame();
            });

    String[] knownMaps = resourceLoader.getValidMaps();
    numberOfMaps = knownMaps.length;
    for (String map : knownMaps) {
      resourceLoader.loadMap(map);
      this.validMaps.add(resourceLoader.getMap());
      mapImages.add(mapPreview.getMapPreview(map));
    }

    VBox mapSelectionView = new VBox(40);
    Label selectMapLbl = LabelGenerator
            .generate(true, mapSelectionView, "Select a map: ", UIColours.BLACK, 14);
    moveMapsLeftBtn = ButtonGenerator.generate(true, root, "<", UIColours.WHITE, 40);
    moveMapsRightBtn = ButtonGenerator.generate(true, root, ">", UIColours.WHITE, 40);
    moveMapsLeftBtn.setVisible(false);
    if (knownMaps.length <= 1) {
      moveMapsRightBtn.setVisible(false);
    } else {
      moveMapsRightBtn.setVisible(true);
    }
    moveMapsLeftBtn.setOnAction(event -> showPreviousMap());
    moveMapsRightBtn.setOnAction(event -> showNextMap());

    mapView = new ImageView(mapImages.get(0));
    currentMap = validMaps.get(0);
    mapView.setPreserveRatio(true);
    mapView.setFitWidth(700);
    Button generateMapBtn = ButtonGenerator
            .generate(true, root, "Generate Map", UIColours.WHITE, 25);
    Button mapConfirmationBtn = ButtonGenerator
            .generate(true, root, "Continue", UIColours.GREEN, 25);
    mapConfirmationBtn.setOnAction(event -> {
      audioController.playSound(Sounds.click);
      moveItemsToBackTree();
      itemsOnScreen.add(startGameBtn);
      resourceLoader.setMap(currentMap);
      client.setMap(resourceLoader.getMap());
      showItemsOnScreen();
    });

    generateMapBtn.setOnAction(event -> {
      int[][] newMap = MapGenerator.newRandomMap(3, 3);
      while (!MapGenerator.validateMap(newMap)) {
        newMap = MapGenerator.generateNewMap(3, 3);
      }
      Map generatedMap = new Map(newMap);
      validMaps.add(generatedMap);
      Image generatedPreview = mapPreview.getMapPreview(generatedMap);
      mapImages.add(generatedPreview);
      numberOfMaps++;
      mapsIndex = numberOfMaps - 1;
      currentMap = validMaps.get(mapsIndex);
      mapView.setImage(mapImages.get(mapsIndex));
      moveMapsRightBtn.setVisible(false);
      moveMapsLeftBtn.setVisible(true);
    });

    HBox mapSelectionBox = new HBox(30, moveMapsLeftBtn, mapView, moveMapsRightBtn);
    HBox mapSelectionBtns = new HBox(20, generateMapBtn, mapConfirmationBtn);
    mapSelectionView.getChildren().addAll(mapSelectionBox, mapSelectionBtns);
    mapSelectionBtns.setAlignment(Pos.CENTER);
    mapSelectionBox.setAlignment(Pos.CENTER);
    mapSelectionView.setAlignment(Pos.CENTER);
    StackPane.setMargin(mapSelectionView, new Insets(0, 0, 150, 0));
    mapSelectionView.setVisible(false);
    root.getChildren().add(mapSelectionView);

    Button singlePlayerBtn = ButtonGenerator
            .generate(true, root, "Singleplayer", UIColours.WHITE, 40);
    singlePlayerBtn.setOnAction(
            e -> {
              audioController.playSound(Sounds.click);
              moveItemsToBackTree();
              itemsOnScreen.add(mapSelectionView);
              showItemsOnScreen();
            });

    Button multiplayerBtn = ButtonGenerator
            .generate(true, root, "Multiplayer", UIColours.WHITE, 40);
    multiplayerBtn.setOnAction(
            e -> {
              audioController.playSound(Sounds.click);
              moveItemsToBackTree();
              itemsOnScreen.add(nameEntryOptions);
              showItemsOnScreen();
            });

    gameModeOptions = new VBox(10, singlePlayerBtn, multiplayerBtn);
    gameModeOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(gameModeOptions, Pos.CENTER);
    StackPane.setMargin(gameModeOptions, new Insets(0, 0, 0, 0));
    root.getChildren().add(gameModeOptions);
    gameModeOptions.setVisible(false);

    Button playBtn = ButtonGenerator.generate(true, root, "Play", UIColours.GREEN, 35);
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

    client.setRenderingMode(RenderingMode.SMOOTH_SCALING);

    ImageView creditsView = new ImageView("ui/Credits.png");
    Button creditsBtn = ButtonGenerator.generate(false, root, creditsView);
    StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));

    Button joinGameBtn = ButtonGenerator.generate(true, root, "Join a game", UIColours.WHITE, 40);
    joinGameBtn.setPickOnBounds(true);
    joinGameBtn.setOnAction(
            event -> {
              audioController.playSound(Sounds.click);
              moveItemsToBackTree();
              lobbyStatusLbl.setText("Waiting for game to start");
              itemsOnScreen.add(searchingForMultiplayers);
              showItemsOnScreen();
              inLobby = true;
              client.joinMultiplayerLobby();
              playerNumberDiscovery = new Thread(lobbyPlayers);
              playerNumberDiscovery.start();

            });

    Button createGameBtn = ButtonGenerator.generate(true, root, "Create game", UIColours.WHITE, 40);
    createGameBtn.setPickOnBounds(true);
    createGameBtn.setOnAction(
            event -> {
              audioController.playSound(Sounds.click);
              moveItemsToBackTree();
              itemsOnScreen.add(searchingForMultiplayers);
              itemsOnScreen.add(startMGameBtn);
              showItemsOnScreen();
              client.createMultiplayerLobby();
              inLobby = true;
              playerNumberDiscovery = new Thread(lobbyPlayers);
              playerNumberDiscovery.start();
            });

    multiplayerOptions = new VBox(10, createGameBtn, joinGameBtn);
    multiplayerOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(multiplayerOptions, Pos.CENTER);
    StackPane.setMargin(multiplayerOptions, new Insets(0, 0, 0, 0));
    root.getChildren().add(multiplayerOptions);
    multiplayerOptions.setVisible(false);

    searchingForMultiplayers = new VBox(5);

    lobbyStatusLbl = LabelGenerator
            .generate(true, searchingForMultiplayers, "Searching for players", UIColours.WHITE, 20);
    loadingDots = LabelGenerator
            .generate(true, searchingForMultiplayers, " .", UIColours.WHITE, 20);
    playersInLobby = LabelGenerator
            .generate(true, searchingForMultiplayers, "Players in Lobby: 0", UIColours.WHITE, 20);
    searchingForMultiplayers.setAlignment(Pos.CENTER);
    StackPane.setAlignment(searchingForMultiplayers, Pos.CENTER);
    root.getChildren().add(searchingForMultiplayers);
    searchingForMultiplayers.setVisible(false);

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

    Button nameEntryBtn = ButtonGenerator.generate(true, root, "Continue", UIColours.GREEN, 30);
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
    StackPane.setMargin(nameEntryOptions, new Insets(50, 250, 0, 250));
    nameEntryOptions.setPrefWidth(300);
    nameEntryOptions.setVisible(false);
    root.getChildren().add(nameEntryOptions);

    Button quitBtn = ButtonGenerator.generate(true, root, "quit", UIColours.QUIT_RED, 30);
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

    Label musicLbl = LabelGenerator.generate(true, soundTabLayout, "Music:", UIColours.BLACK, 16);
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

    Label soundFXLbl = LabelGenerator
            .generate(true, soundTabLayout, "SoundFX:", UIColours.BLACK, 16);
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

    Label volumeLbl = LabelGenerator.generate(true, soundTabLayout, "Volume:", UIColours.BLACK, 16);

    JFXSlider volumeSlider = new JFXSlider(0, 1, 0.5);

    volumeSlider.valueProperty().addListener((ov, old_val, new_val) -> {
      audioController.setSoundVolume(new_val.doubleValue());
      audioController.setMusicVolume(new_val.doubleValue());
    });
    volumeSlider.setMaxWidth(200);
    volumeSlider.setMaxWidth(200);
    StackPane.setAlignment(volumeSlider, Pos.BOTTOM_CENTER);
    StackPane.setMargin(volumeSlider, new Insets(0, 0, 155, 220));

    StackPane.setAlignment(volumeLbl, Pos.BOTTOM_CENTER);
    StackPane.setMargin(volumeLbl, new Insets(0, 200, 150, 0));

    soundTabLayout.getChildren()
            .addAll(musicToggle, soundFXToggle, volumeSlider);
    soundTab.setContent(soundTabLayout);

    //Creates the tab for graphics
    Tab graphicsTab = new Tab();
    graphicsTab.setText("Graphics");
    StackPane graphicsTabLayout = new StackPane();

    //Adding resolution label and combo box
    Label resolutionLbl = LabelGenerator
            .generate(true, graphicsTabLayout, "Resolution: ", UIColours.BLACK, 14);

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
    Label scalingLbl = LabelGenerator
            .generate(true, graphicsTabLayout, "Resolution Scaling: ", UIColours.BLACK, 14);

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
            .addAll(resolutionCombo, scalingCombo);
    graphicsTab.setContent(graphicsTabLayout);

    //Creates the tab for controls
    Tab controlsTab = new Tab();
    controlsTab.setText("Controls");

    StackPane controlsLayout = new StackPane();

    VBox keyLbls = new VBox(45);

    upLbl = LabelGenerator
            .generate(true, keyLbls, "UP KEY: " + Settings.getKey(InputKey.UP).getName(),
                    UIColours.BLACK, 14);
    leftLbl = LabelGenerator
            .generate(true, keyLbls, "LEFT KEY: " + Settings.getKey(InputKey.LEFT).getName(),
                    UIColours.BLACK, 14);
    rightLbl = LabelGenerator
            .generate(true, keyLbls, "RIGHT KEY: " + Settings.getKey(InputKey.RIGHT).getName(),
                    UIColours.BLACK, 14);
    downLbl = LabelGenerator
            .generate(true, keyLbls, "DOWN KEY: " + Settings.getKey(InputKey.DOWN).getName(),
                    UIColours.BLACK, 14);
    useLbl = LabelGenerator
            .generate(true, keyLbls, "USE KEY: " + Settings.getKey(InputKey.USE).getName(),
                    UIColours.BLACK, 14);
    keyLbls.setAlignment(Pos.CENTER);

    StackPane.setAlignment(keyLbls, Pos.CENTER);
    StackPane.setMargin(keyLbls, new Insets(0, 200, 75, 0));

    keyToggleStatus = new Label("");
    keyToggleStatus.setStyle(" -fx-font-size: 12pt ; -fx-text-fill: white");
    StackPane.setAlignment(keyToggleStatus, Pos.BOTTOM_CENTER);

    ToggleButton upToggle = new ToggleButton(defaultToggleText);
    ToggleButton leftToggle = new ToggleButton(defaultToggleText);
    ToggleButton rightToggle = new ToggleButton(defaultToggleText);
    ToggleButton downToggle = new ToggleButton(defaultToggleText);
    ToggleButton useToggle = new ToggleButton(defaultToggleText);
    keyToggleList.add(leftToggle);
    keyToggleList.add(rightToggle);
    keyToggleList.add(upToggle);
    keyToggleList.add(downToggle);
    keyToggleList.add(useToggle);
    initialiseToggleActions();
    ToggleGroup toggleGroup = new ToggleGroup();

    upToggle.setUserData(InputKey.UP);
    leftToggle.setUserData(InputKey.LEFT);
    rightToggle.setUserData(InputKey.RIGHT);
    downToggle.setUserData(InputKey.DOWN);
    useToggle.setUserData(InputKey.USE);

    upToggle.setToggleGroup(toggleGroup);
    leftToggle.setToggleGroup(toggleGroup);
    rightToggle.setToggleGroup(toggleGroup);
    downToggle.setToggleGroup(toggleGroup);
    useToggle.setToggleGroup(toggleGroup);

    VBox keyToggles = new VBox(45, upToggle, leftToggle, rightToggle, downToggle, useToggle);
    keyToggles.setAlignment(Pos.CENTER);

    StackPane.setAlignment(keyToggles, Pos.CENTER);
    StackPane.setMargin(keyToggles, new Insets(0, 0, 75, 450));

    controlsLayout.getChildren().addAll(keyLbls, keyToggles, keyToggleStatus);
    controlsTab.setContent(controlsLayout);

    //Adds the tabs to the tab pane
    settingsTabs.getTabs().addAll(soundTab, graphicsTab, controlsTab);

    //Calculates the width of the tabs
    double tabWidth = settingsTabs.getMaxWidth() / settingsTabs.getTabs().size();
    settingsTabs.setTabMinWidth(tabWidth - 5);
    settingsTabs.setTabMaxWidth(tabWidth - 5);

    SingleSelectionModel<Tab> selectionModel = settingsTabs.getSelectionModel();
    selectionModel.select(0);

    root.getChildren().addAll(settingsTabs);

    ImageView settingsView = new ImageView("ui/settings.png");
    settingsBtn = ButtonGenerator.generate(true, root, settingsView);
    StackPane.setAlignment(settingsBtn, Pos.TOP_LEFT);
    StackPane.setMargin(settingsBtn, new Insets(50, 0, 0, 50));
    settingsView.setFitHeight(50);
    settingsView.setFitWidth(50);
    settingsBtn.setOnAction(
            event -> {
              audioController.playSound(Sounds.click);
              if (!viewSettings) {
                viewSettings = true;
                hideItemsOnScreen();

                backBtn.setVisible(false);
                settingsTabs.setVisible(true);
                settingsBtn.setVisible(true);

              } else {
                viewSettings = false;
                settingsTabs.setVisible(false);
                showItemsOnScreen();
                if (!isHome) {
                  backBtn.setVisible(true);
                }
              }
            });

    startMGameBtn = ButtonGenerator.generate(false, root, "Start", UIColours.GREEN, 30);
    StackPane.setAlignment(startMGameBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(startMGameBtn, new Insets(0, 0, 200, 0));
    startMGameBtn.setOnAction(
            e -> {
              audioController.playSound(Sounds.click);
              client.startMultiplayerGame();
            });

    ImageView instructionsGif = new ImageView("ui/preview.gif");
    instructionsGif.setPreserveRatio(true);
    instructionsGif.setFitWidth(500);
    instructionsGif.setVisible(false);
    StackPane.setAlignment(instructionsGif, Pos.TOP_CENTER);
    StackPane.setMargin(instructionsGif, new Insets(100, 0, 0, 0));
    root.getChildren().add(instructionsGif);

    Label instructionLbl = new Label("Use your keyboard mastery to capture MIPs man "
            + "and collect as many points as possible whilst you control him. "
            + "Beware: If you are captured by a ghoul, you will become one and have to capture"
            + " the new MIPs man all over again. Good luck! ");
    instructionLbl.setWrapText(true);
    instructionLbl.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(instructionLbl, Pos.CENTER);
    StackPane.setMargin(instructionLbl, new Insets(200, 200, 0, 200));
    root.getChildren().add(instructionLbl);
    instructionLbl.setVisible(false);

    Button instructions = ButtonGenerator
            .generate(true, root, "Instructions", UIColours.YELLOW, 30);
    StackPane.setAlignment(instructions, Pos.BOTTOM_CENTER);
    StackPane.setMargin(instructions, new Insets(0, 0, 100, 0));
    instructions.setOnAction(event -> {
      audioController.playSound(Sounds.click);
      moveItemsToBackTree();
      isInstructions = true;
      backBtn.setVisible(true);
      itemsOnScreen.add(instructionLbl);
      itemsOnScreen.add(instructionsGif);
      showItemsOnScreen();
    });


    backBtn = ButtonGenerator.generate(false, root, "back", UIColours.RED, 30);
    StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(backBtn, new Insets(0, 0, 100, 0));
    backBtn.setOnAction(
            event -> {
              audioController.playSound(Sounds.click);
              if (isInstructions) {
                instructionLbl.setVisible(false);
                instructionsGif.setVisible(false);
                backBtn.setVisible(false);
                itemsOnScreen.clear();
                ArrayList<Node> toShow = backTree.pop();
                itemsOnScreen.addAll(toShow);
                showItemsOnScreen();
                isInstructions = false;

              }

              if (inLobby) {
                playerNumberDiscovery.interrupt();
                client.leaveLobby();
//            lobbyPlayers.interrupt();

                inLobby = false;

              }
              if (!backTree.isEmpty()) {
                hideItemsOnScreen();
                itemsOnScreen.clear();
                ArrayList<Node> toShow = backTree.pop();
                itemsOnScreen.addAll(toShow);
                showItemsOnScreen();
                if (backTree.isEmpty()) {
                  backBtn.setVisible(false);
                  isHome = true;
                }
              }
            });

    backTree.empty();
    itemsOnScreen.add(playBtn);
    itemsOnScreen.add(instructions);
    itemsOnScreen.add(logo);
    itemsOnScreen.add(quitBtn);
    itemsOnScreen.add(settingsBtn);

    imageViews =
            Arrays.asList(
                    logo,
                    settingsView
            );

    for (int i = 0; i < imageViews.size(); i++) {
      originalViewWidths.add(imageViews.get(i).getBoundsInLocal().getWidth());
      minimumViewWidths.add(originalViewWidths.get(i) * 0.4);
    }

    return root;
  }

  /**
   * Hides remapping toggles when they are no longer needed
   *
   * @param toShow the toggle we want to keep showing
   */
  private void hideInactiveToggles(ToggleButton toShow) {
    for (ToggleButton t : keyToggleList) {
      if (!t.equals(toShow)) {
        t.setVisible(false);
      }
    }
  }

  /**
   * Shows the toggles in their original state
   */
  private void resetToggles() {
    for (ToggleButton t : keyToggleList) {
      t.setText(defaultToggleText);
      t.setVisible(true);
    }
  }

  /**
   * Initialises the behaviour for the toggle buttons
   */
  private void initialiseToggleActions() {
    for (ToggleButton t : keyToggleList) {
      t.setOnAction(event -> {
        if (t.isSelected()) {
          t.setText(remapToggleText);
          toRemap = (InputKey) t.getUserData();
          hideInactiveToggles(t);
          keyToggleStatus.setText(remapReady);
          primaryStage.getScene().setOnKeyPressed(keyRemapper);
          new Thread(() -> {
            while (true) {
              if (keyRemapper.getActiveKey() == null) {
                try {
                  Thread.sleep(50);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                continue;
              }

              proposedChange = keyRemapper.getActiveKey();
              break;
            }
          }).start();

        } else {
          if (proposedChange == null) {
            keyToggleStatus.setText(remapCancelled);
          } else if (keyRemapper.checkForDublicates(proposedChange) && proposedChange != null) {
            keyToggleStatus.setText("Sorry, key already in use.");
          } else {
            keyToggleStatus.setText(remapComplete);
            Settings.setKey(toRemap, proposedChange);
            keyRemapper.reset();
          }
          resetToggles();
          updateToggleLabels();
          primaryStage.getScene().setOnKeyPressed(null);
          toRemap = null;
          proposedChange = null;
        }
      });
    }
  }

  /**
   * Resets the toggle labels to their default state with the current control values.
   */
  private void updateToggleLabels() {
    System.out.println("Current UP KEY: " + Settings.getKey(InputKey.UP).getName());
    upLbl.setText("UP KEY: " + Settings.getKey(InputKey.UP).getName());
    leftLbl.setText("LEFT KEY: " + Settings.getKey(InputKey.LEFT).getName());
    rightLbl.setText("RIGHT KEY: " + Settings.getKey(InputKey.RIGHT).getName());
    downLbl.setText("DOWN KEY: " + Settings.getKey(InputKey.DOWN).getName());
    useLbl.setText("USE ITEM KEY: " + Settings.getKey(InputKey.USE).getName());
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
