package com.lordsofmidnight.ui;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTabPane;
import com.lordsofmidnight.audio.AudioController;
import com.lordsofmidnight.audio.Sounds;
import com.lordsofmidnight.gamestate.maps.Map;
import com.lordsofmidnight.gamestate.maps.MapGenerationHandler;
import com.lordsofmidnight.gamestate.maps.MapPreview;
import com.lordsofmidnight.main.Client;
import com.lordsofmidnight.server.NetworkUtility;
import com.lordsofmidnight.utils.KeyRemapping;
import com.lordsofmidnight.renderer.ResourceLoader;
import com.lordsofmidnight.utils.ResourceSaver;
import com.lordsofmidnight.utils.Settings;
import com.lordsofmidnight.utils.enums.InputKey;
import com.lordsofmidnight.utils.enums.RenderingMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author Adam Kona
 * Class which handles the creation and functionality of components in the
 * main menu.
 */
public class MenuController {

  private AudioController audioController;
  private Client client;

  private boolean viewSettings = false;
  private Stage primaryStage;
  private Stack<ArrayList<Node>> backTree = new Stack<>();
  private ArrayList<Node> itemsOnScreen = new ArrayList<>();
  private ResourceLoader resourceLoader;

  private ImageView bg;

  private Button startGameBtn;
  private Button backBtn;
  private Button startMGameBtn;
  private Button settingsBtn;
  private Button quitBtn;
  private Button setNameBtn;
  private ImageView logo;
  private VBox homeOptions;

  private Label lobbyStatusLbl;
  private Label loadingDots;
  private Label playersInLobby;
  private int numberOfPlayers;

  private TextField nameEntry;
  private VBox nameEntryView;
  private Label nameEntryStatus;

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

  private Text upKey;
  private Text leftKey;
  private  Text rightKey;
  private Text downKey;
  private Text useKey;

  private MapPreview mapPreview = new MapPreview(1920, 1080);
  private ImageView mapView;
  private int mapsIndex = 0;
  private int numberOfMaps;
  private ArrayList<Image> mapImages = new ArrayList<>();
  private Button moveMapsLeftBtn;
  private Button moveMapsRightBtn;
  private Map currentMap;
  private ArrayList<Map> validMaps = new ArrayList<>();
  private ArrayList<String> mapNames = new ArrayList<>();
  private Text mapNameBody;
  private Map generatedBuffer;
  private Image previewBuffer;

  private ImageView themePreview;
  private int themeIndex = 0;
  private int numberofThemes;
  private String[] themeNames;
  private Image[] themeImages;
  private Button moveThemesLeftBtn;
  private Button moveThemesRightBtn;
  private Label themeName;
  private String currentTheme;
  private boolean isHome = true;
  private boolean isMultiplayer = false;
  private boolean inLobby;
  private Thread playerNumberDiscovery;
  private MulticastSocket socket;
  private boolean gameFound = false;

  private MapGenerationHandler mapGenerationHandler;

  private ResourceSaver resourceSaver = new ResourceSaver("src/main/resources/");

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
    this.mapGenerationHandler = new MapGenerationHandler();

    Image icon = new Image("icon.png", false);
    primaryStage.getIcons().add(icon);
  }

  /**
   * Runnable to listen to the Server Lobby which is constantly pinging the number of players in the
   * lobby.
   */
  private Runnable lobbyPlayers = (() -> {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        Thread.sleep(1000);
        System.out.println("Listening for number of players...");
        socket = new MulticastSocket(NetworkUtility.CLIENT_M_PORT);
        socket.setSoTimeout(NetworkUtility.LOBBY_TIMEOUT);
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
        gameFound = true;
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
          numberOfPlayers = players;
          System.out.println("Updating player count");
          Platform.runLater(() -> {
            lobbyStatusLbl.setText("Waiting for game to start");
            playersInLobby.setVisible(true);
            playersInLobby.setText("Players in lobby: " + players);
          });
        }
        socket.close();
      } catch (SocketTimeoutException e) {
        System.out.println("Server stopped sending lobby updates.");
        if (gameFound) {
          client.leaveLobby();
          Platform.runLater(() -> {
            lobbyStatusLbl.setText("Host left the game");
            loadingDots.setVisible(false);
            playersInLobby.setVisible(false);
          });
        }

        Thread.currentThread().interrupt();
      } catch (InterruptedException e1) {
        System.out.println("Lobby players thread was interrupted. ");
      } catch (IOException e2) {
        e2.printStackTrace();
      }
    }
    if (!socket.isClosed() && socket != null) {
      socket.close();
    }
    numberOfPlayers = 0;

    System.out.println("Lobby players thread fully ended");
  });

  /**
   * Kills the thread which listens for the number of players in a lobby.
   */
  public void endPlayerDiscovery() {
    gameFound = false;
    this.playerNumberDiscovery.interrupt();
  }

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

  /**
   * Handles showing the next map in the menu
   */
  private void showNextMap() {
    mapsIndex++;
    if (mapsIndex >= (numberOfMaps - 1)) {
      mapsIndex = numberOfMaps - 1;
      moveMapsRightBtn.setVisible(false);
    }
    mapNameBody.setText(mapNames.get(mapsIndex));
    moveMapsLeftBtn.setVisible(true);
    mapView.setImage(mapImages.get(mapsIndex));
    currentMap = validMaps.get(mapsIndex);
  }

  /**
   * Handles showing the previous map in the menu
   */
  private void showPreviousMap() {
    mapsIndex--;
    if (mapsIndex <= 0) {
      mapsIndex = 0;
      moveMapsLeftBtn.setVisible(false);
    }
    mapNameBody.setText(mapNames.get(mapsIndex));
    moveMapsRightBtn.setVisible(true);
    mapView.setImage(mapImages.get(mapsIndex));
    currentMap = validMaps.get(mapsIndex);
  }

  /**
   * Handles showing the next theme in the menu
   */
  private void showNextTheme() {
    themeIndex++;
    if (themeIndex >= (numberofThemes - 1)) {
      themeIndex = numberofThemes - 1;
      moveThemesRightBtn.setVisible(false);
    }
    themeName.setText(
        themeNames[themeIndex].substring(0, 1).toUpperCase() + themeNames[themeIndex].substring(1));
    currentTheme = themeNames[themeIndex];
    moveThemesLeftBtn.setVisible(true);
    themePreview.setImage(themeImages[themeIndex]);
  }

  /**
   * Handles showing the previous theme in the menu
   */
  private void showPreviousTheme() {
    themeIndex--;
    if (themeIndex <= 0) {
      themeIndex = 0;
      moveThemesLeftBtn.setVisible(false);
    }
    themeName.setText(
        themeNames[themeIndex].substring(0, 1).toUpperCase() + themeNames[themeIndex].substring(1));
    moveThemesRightBtn.setVisible(true);
    currentTheme = themeNames[themeIndex];
    themePreview.setImage(themeImages[themeIndex]);
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
      t.setStyle("-fx-text-fill: " + UIColours.YELLOW + ";");
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

    upKey.setText(Settings.getKey(InputKey.UP).getName());
    leftKey.setText(Settings.getKey(InputKey.LEFT).getName());
    rightKey.setText(Settings.getKey(InputKey.RIGHT).getName());
    downKey.setText(Settings.getKey(InputKey.DOWN).getName());
    useKey.setText(Settings.getKey(InputKey.USE).getName());
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

  /**
   * Resets the main menu to its default state.
   */
  public void reset() {
    audioController.playMusic(Sounds.MENULOOP);
    hideItemsOnScreen();
    itemsOnScreen.clear();
    backTree.clear();
    itemsOnScreen.add(logo);
    itemsOnScreen.add(quitBtn);
    itemsOnScreen.add(settingsBtn);
    itemsOnScreen.add(homeOptions);
    isHome = true;
    isMultiplayer = false;
    inLobby = false;
    backBtn.setVisible(false);
    showItemsOnScreen();
    numberOfPlayers = 0;
    mapGenerationHandler.start();
  }

  /**
   * Updates the lobby label letting the user know that a game was not found.
   */
  public void gameNotFound() {
    Platform.runLater(() -> {
      lobbyStatusLbl.setText("Game Not Found");
      loadingDots.setVisible(false);
    });

  }

  /**
   * Creates all the menu items and defines their functionality.
   *
   * @return The node containing the menu which will be returned to the game window.
   */
  public Node createMainMenu() {

    StackPane root = new StackPane();
    root.setPrefSize(1920, 1080);

    bg = new ImageView(resourceLoader.getBackground());
    bg.fitWidthProperty().bind(this.primaryStage.widthProperty());
    bg.fitHeightProperty().bind(this.primaryStage.heightProperty());

    root.getChildren().add(bg);
    StackPane.setAlignment(bg, Pos.CENTER);

    logo = new ImageView("ui/MIPS-B.png");
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
          audioController.playSound(Sounds.CLICK);
          client.startSinglePlayerGame();
          mapGenerationHandler.stop();
        });

    String[] knownMaps = resourceLoader.getValidMaps();
    numberOfMaps = knownMaps.length;
    for (String map : knownMaps) {
      resourceLoader.loadMap(map);
      this.validMaps.add(resourceLoader.getMap());
      mapNames.add(map);
      mapImages.add(mapPreview.getMapPreview(map));
    }

    VBox mapSelectionView = new VBox(20);

    mapNameBody = TextGenerator.generate(mapNames.get(0), UIColours.YELLOW, 14);

    TextFlow mapNameFlow = new TextFlow(mapNameBody);
    Label selectMapLbl = LabelGenerator
        .generate(true, mapSelectionView, "Select a map", UIColours.WHITE, 14);

    Label mapNameLbl = new Label(null, mapNameFlow);
    mapSelectionView.getChildren().add(mapNameLbl);
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
    moveMapsLeftBtn.setFocusTraversable(false);
    moveMapsRightBtn.setFocusTraversable(false);

    mapView = new ImageView(mapImages.get(0));
    currentMap = validMaps.get(0);
    mapView.setPreserveRatio(true);
    mapView.setFitWidth(700);
    Button generateMapBtn = ButtonGenerator
        .generate(true, root, "Generate Map", UIColours.WHITE, 20);
    generateMapBtn.setFocusTraversable(false);
    Button mapConfirmationBtn = ButtonGenerator
        .generate(true, root, "Continue", UIColours.GREEN, 20);
    mapConfirmationBtn.setFocusTraversable(false);
    mapConfirmationBtn.setOnAction(event -> {
      audioController.playSound(Sounds.CLICK);
      moveItemsToBackTree();
      resourceLoader.setMap(currentMap);
      client.setMap(resourceLoader.getMap());
      if(isMultiplayer){
        lobbyStatusLbl.setText("Creating Game");
        itemsOnScreen.add(searchingForMultiplayers);
        itemsOnScreen.add(startMGameBtn);
        showItemsOnScreen();
        client.createMultiplayerLobby();
        inLobby = true;
        playerNumberDiscovery = new Thread(lobbyPlayers);
        playerNumberDiscovery.start();
      }else{
        itemsOnScreen.add(startGameBtn);
      }
      showItemsOnScreen();
    });

    HBox mapSelectionBtns = new HBox(20, generateMapBtn, mapConfirmationBtn);

    HBox mapSizeBtns = new HBox(20);
    mapSizeBtns.setVisible(false);
    StackPane.setAlignment(mapSizeBtns, Pos.CENTER);
    Label mapNameTxt = LabelGenerator.generate(true, "Generated Map", UIColours.WHITE, 15);

    ImageView generatedMapPreview = new ImageView();
    generatedMapPreview.setPreserveRatio(true);
    generatedMapPreview.setFitWidth(700);
    root.getChildren().add(generatedMapPreview);
    StackPane.setAlignment(generatedMapPreview, Pos.CENTER);

    TextField mapNameEntry = new TextField();
    mapNameEntry.setPromptText("Select a name for the map...");
    mapNameEntry.setStyle(
        "-fx-text-inner-color: white; "
            + "-fx-prompt-text-fill: white; "
            + "-fx-background-color: transparent;");
    mapNameEntry.setAlignment(Pos.CENTER);

    Line line = new Line(0, 100, 500, 100);
    line.setStroke(Color.WHITE);
    VBox mapField = new VBox(mapNameEntry, line);
    mapField.setAlignment(Pos.CENTER);

    Button saveMapBtn = ButtonGenerator.generate(true, root, "Save", UIColours.GREEN, 25);
    saveMapBtn.setOnAction(
        event -> {
          if(mapNameEntry.getText().equals("")){
            mapNameTxt.setText("Please enter a name");
          }else if(mapNames.contains(mapNameEntry.getText())){
            mapNameTxt.setText("Map Name Already Exists");
          }else{
            try{
              resourceSaver.saveMap(generatedBuffer, mapNameEntry.getText());
            }catch(IOException e){
              e.printStackTrace();
            }

            validMaps.add(generatedBuffer);
            generatedBuffer = null;
            mapImages.add(previewBuffer);
            previewBuffer = null;
            numberOfMaps++;
            mapsIndex = numberOfMaps - 1;
            currentMap = validMaps.get(mapsIndex);
            mapView.setImage(mapImages.get(mapsIndex));
            moveMapsRightBtn.setVisible(false);
            moveMapsLeftBtn.setVisible(true);
            mapNameBody.setText(mapNameEntry.getText());
            mapNames.add(mapNameEntry.getText());
            backBtn.fire();
            backBtn.fire();
          }

        });

    VBox mapNameOptions = new VBox(10,mapNameTxt,generatedMapPreview ,mapField, saveMapBtn);
    mapNameOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(mapNameOptions, Pos.CENTER);
    StackPane.setMargin(mapNameOptions, new Insets(0, 0, 170, 0));
    mapNameOptions.setPrefWidth(300);
    mapNameOptions.setVisible(false);
    root.getChildren().add(mapNameOptions);

    Button smallMapBtn = ButtonGenerator.generate(true, mapSizeBtns, "Small", UIColours.GREEN, 35);
    smallMapBtn.setOnAction(event -> {
      mapNameTxt.setText("Generated Map");
      generatedBuffer = mapGenerationHandler.getSmallMap();
      previewBuffer = mapPreview.getMapPreview(generatedBuffer);
      generatedMapPreview.setImage(previewBuffer);
      moveItemsToBackTree();
      itemsOnScreen.add(mapNameOptions);
      showItemsOnScreen();

    });
    Button bigMapBtn = ButtonGenerator.generate(true, mapSizeBtns, "Big", UIColours.RED, 35);
    bigMapBtn.setOnAction(event -> {
      mapNameTxt.setText("Generated Map");
      generatedBuffer = mapGenerationHandler.getBigMap();
      previewBuffer = mapPreview.getMapPreview(generatedBuffer);
      generatedMapPreview.setImage(previewBuffer);
      moveItemsToBackTree();
      itemsOnScreen.add(mapNameOptions);
      showItemsOnScreen();

    });
    mapSizeBtns.setAlignment(Pos.CENTER);
    root.getChildren().addAll(mapSizeBtns);

    generateMapBtn.setOnAction(event -> {
      moveItemsToBackTree();
      itemsOnScreen.add(mapSizeBtns);
      showItemsOnScreen();
    });

    HBox mapSelectionBox = new HBox(30, moveMapsLeftBtn, mapView, moveMapsRightBtn);

    mapSelectionView.getChildren().addAll(mapSelectionBox, mapSelectionBtns);
    mapSelectionBtns.setAlignment(Pos.CENTER);
    mapSelectionBox.setAlignment(Pos.CENTER);
    mapSelectionView.setAlignment(Pos.CENTER);
    StackPane.setMargin(mapSelectionView, new Insets(0, 0, 150, 0));
    mapSelectionView.setVisible(false);
    root.getChildren().add(mapSelectionView);

    Button singlePlayerBtn = ButtonGenerator
        .generate(true, root, "Singleplayer", UIColours.WHITE, 40);
    singlePlayerBtn.setFocusTraversable(false);
    singlePlayerBtn.setOnAction(
        e -> {
          isMultiplayer = false;
          audioController.playSound(Sounds.CLICK);
          moveItemsToBackTree();
          itemsOnScreen.add(mapSelectionView);
          showItemsOnScreen();
        });

    Button multiplayerBtn = ButtonGenerator
        .generate(true, root, "Multiplayer", UIColours.WHITE, 40);
    multiplayerBtn.setFocusTraversable(false);
    multiplayerBtn.setOnAction(
        e -> {
          isMultiplayer = true;
          audioController.playSound(Sounds.CLICK);
          moveItemsToBackTree();
          itemsOnScreen.add(multiplayerOptions);
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
    playBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.CLICK);
          isHome = false;
          backBtn.setVisible(true);
          moveItemsToBackTree();
          itemsOnScreen.add(gameModeOptions);
          showItemsOnScreen();
        });

    ImageView creditsView = new ImageView("ui/Credits.png");
    Button creditsBtn = ButtonGenerator.generate(false, root, creditsView);
    StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(creditsBtn, new Insets(0, 0, 50, 0));

    Button joinGameBtn = ButtonGenerator.generate(true, root, "Join a game", UIColours.WHITE, 40);
    joinGameBtn.setPickOnBounds(true);
    joinGameBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);
          moveItemsToBackTree();
          lobbyStatusLbl.setText("Searching for game");
          itemsOnScreen.add(searchingForMultiplayers);
          showItemsOnScreen();
          inLobby = true;
          numberOfPlayers = 0;
          client.joinMultiplayerLobby();
          playerNumberDiscovery = new Thread(lobbyPlayers);
          playerNumberDiscovery.start();

        });

    Button createGameBtn = ButtonGenerator.generate(true, root, "Create game", UIColours.WHITE, 40);
    createGameBtn.setPickOnBounds(true);
    createGameBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);
          moveItemsToBackTree();
          numberOfPlayers = 0;
          itemsOnScreen.add(mapSelectionView);
          showItemsOnScreen();
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
        .generate(false, searchingForMultiplayers, "Players in Lobby: 0", UIColours.WHITE, 20);
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
              new FileInputStream(new File(
                  "src/main/resources/ui/PressStart2P.ttf")),
              16);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    nameEntry.setPromptText("Please enter your name...");
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

    Button nameEntryBtn = ButtonGenerator.generate(true, root, "Save", UIColours.GREEN, 30);
    nameEntryBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);
          if (nameEntry.getText().equals("")) {
            nameEntryStatus.setVisible(true);
          } else {
            hideItemsOnScreen();
            itemsOnScreen.clear();
            this.client.setName(nameEntry.getText());
            backBtn.fire();
            showItemsOnScreen();
          }

        });

    Label namePrompt = LabelGenerator
        .generate(true, root, "What's your name?", UIColours.WHITE, 20);
    nameEntryStatus = LabelGenerator
        .generate(true, root, "Enter something man", UIColours.WHITE, 20);
    nameEntryStatus.setVisible(false);
    nameEntryOptions = new VBox(40, nameAndLine, nameEntryBtn);
    nameEntryOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(nameEntryOptions, Pos.CENTER);
    StackPane.setMargin(nameEntryOptions, new Insets(50, 250, 0, 250));
    nameEntryOptions.setPrefWidth(300);
    nameEntryOptions.setVisible(true);
    root.getChildren().add(nameEntryOptions);

    nameEntryView = new VBox(80, namePrompt, nameEntryOptions, nameEntryStatus);
    nameEntryView.setAlignment(Pos.CENTER);
    StackPane.setAlignment(nameEntryView, Pos.CENTER);
    root.getChildren().add(nameEntryView);
    nameEntryView.setVisible(false);

    quitBtn = ButtonGenerator.generate(true, root, "quit", UIColours.QUIT_RED, 25);
    quitBtn.setFocusTraversable(false);
    StackPane.setAlignment(quitBtn, Pos.TOP_LEFT);
    StackPane.setMargin(quitBtn, new Insets(50, 0, 0, 50));
    quitBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);
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

    Label musicLbl = LabelGenerator.generate(true, soundTabLayout, "Music:", UIColours.WHITE, 16);

    JFXSlider musicVolumeSlider = new JFXSlider(0, 1, Settings.getMusicVolume());
    musicVolumeSlider.valueProperty().addListener((ov, old_val, new_val) -> audioController.setMusicVolume(new_val.doubleValue()));

    musicVolumeSlider.setMaxWidth(200);

    StackPane.setAlignment(musicLbl, Pos.TOP_CENTER);
    StackPane.setMargin(musicLbl, new Insets(150, 200, 0, 0));

    StackPane.setAlignment(musicVolumeSlider, Pos.TOP_CENTER);
    StackPane.setMargin(musicVolumeSlider, new Insets(150, 0, 0, 200));

    Label soundFXLbl = LabelGenerator
        .generate(true, soundTabLayout, "SoundFX:", UIColours.WHITE, 16);
    JFXSlider soundFXSlider = new JFXSlider(0, 1, Settings.getSoundVolume());
    soundFXSlider.valueProperty().addListener((ov, old_val, new_val) -> Settings.setSoundVolume(new_val.doubleValue()));

    soundFXSlider.setMaxWidth(200);
    StackPane.setAlignment(soundFXLbl, Pos.CENTER);
    StackPane.setMargin(soundFXLbl, new Insets(0, 200, 0, 0));

    StackPane.setAlignment(soundFXSlider, Pos.CENTER);
    StackPane.setMargin(soundFXSlider, new Insets(0, 0, 0, 200));

    soundTabLayout.getChildren()
        .addAll(musicVolumeSlider, soundFXSlider);
    soundTab.setContent(soundTabLayout);

    //Creates the tab for graphics
    Tab graphicsTab = new Tab();
    graphicsTab.setText("Graphics");
    StackPane graphicsTabLayout = new StackPane();

    //Adding resolution label and combo box
    Label resolutionLbl = LabelGenerator
        .generate(true, graphicsTabLayout, "Resolution: ", UIColours.WHITE, 14);

    JFXComboBox<String> resolutionCombo = new JFXComboBox<>();
    resolutionCombo.getItems().add("1366x768");
    resolutionCombo.getItems().add("1920x1080");
    resolutionCombo.getItems().add("2560x1440");
    resolutionCombo.setEditable(false);
    resolutionCombo.setPromptText("Select a resolution...");
    resolutionCombo.setOnAction(event -> {
      System.out.println(resolutionCombo.getValue());
      audioController.playSound(Sounds.CLICK);
      switch (resolutionCombo.getValue()) {
        case "1366x768":
          Settings.setxResolution(1366);
          Settings.setyResolution(768);
          break;
        case "1920x1080":
          Settings.setxResolution(1920);
          Settings.setyResolution(1080);
          break;
        case "2560x1440":
          Settings.setxResolution(2560);
          Settings.setyResolution(1440);
          break;
        default:
          System.out.println("FAILED");
      }
      client.updateResolution();
    });

    resolutionCombo.setMinWidth(330);

    switch (Settings.getxResolution()) {
      case 1366:
        resolutionCombo.getSelectionModel().select(0);
        break;
      case 1920:
        resolutionCombo.getSelectionModel().select(1);
        break;
      case 2560:
        resolutionCombo.getSelectionModel().select(2);
        break;
      default:
        break;
    }

    // Provide our own ListCells for the ComboBox
    alignComboText(resolutionCombo);

    StackPane.setAlignment(resolutionLbl, Pos.CENTER);
    StackPane.setAlignment(resolutionCombo, Pos.CENTER);

    StackPane.setMargin(resolutionLbl, new Insets(0, 270, 200, 0));
    StackPane.setMargin(resolutionCombo, new Insets(0, 0, 200, 330));

    //Adding resolution label and combo box
    Label scalingLbl = LabelGenerator
        .generate(true, graphicsTabLayout, "Resolution Scaling: ", UIColours.WHITE, 14);

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
          Settings.setRenderingMode(RenderingMode.NO_SCALING);
          break;
        case "Standard":
          Settings.setRenderingMode(RenderingMode.STANDARD_SCALING);
          break;
        case "Smooth":
          Settings.setRenderingMode(RenderingMode.SMOOTH_SCALING);
          break;
        case "Integer":
          Settings.setRenderingMode(RenderingMode.INTEGER_SCALING);
          break;
        default:
          System.out.println("Setting rendering mode failed.");
      }
    });

    scalingCombo.setMinWidth(330);

    switch (Settings.getRenderingMode().getName()) {
      case "no_scaling":
        scalingCombo.getSelectionModel().select(0);
        break;
      case "integer_scaling":
        scalingCombo.getSelectionModel().select(2);
        break;
      case "smooth_scaling":
        scalingCombo.getSelectionModel().select(3);
        break;
      case "standard_scaling":
        scalingCombo.getSelectionModel().select(1);
      default:
        break;
    }

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

    upKey = TextGenerator.generate(Settings.getKey(InputKey.UP).getName(), UIColours.GREEN, 14);
    TextFlow upFlow = new TextFlow(TextGenerator.generate("UP KEY: ", UIColours.WHITE, 14), upKey);
    Label upLbl = new Label(null, upFlow);

    leftKey = TextGenerator.generate(Settings.getKey(InputKey.LEFT).getName(), UIColours.GREEN, 14);
    TextFlow leftFlow = new TextFlow(TextGenerator.generate("LEFT KEY: ", UIColours.WHITE,14), leftKey);
    Label leftLbl = new Label(null, leftFlow);

    rightKey = TextGenerator.generate(Settings.getKey(InputKey.RIGHT).getName(), UIColours.GREEN, 14);
    TextFlow rightFlow = new TextFlow(TextGenerator.generate("RIGHT KEY: ", UIColours.WHITE,14), rightKey);
    Label rightLbl = new Label(null, rightFlow);

    downKey = TextGenerator.generate(Settings.getKey(InputKey.DOWN).getName(), UIColours.GREEN, 14);
    TextFlow downFlow = new TextFlow(TextGenerator.generate("DOWN KEY: ", UIColours.WHITE,14), downKey);
    Label downLbl = new Label(null, downFlow);

    useKey = TextGenerator.generate(Settings.getKey(InputKey.USE).getName(), UIColours.GREEN, 14);
    TextFlow useFlow = new TextFlow(TextGenerator.generate("USE KEY: ", UIColours.WHITE,14), useKey);
    Label useLbl = new Label(null, useFlow);

    keyLbls.getChildren().addAll(upLbl, leftLbl, rightLbl, downLbl, useLbl);
    keyLbls.setAlignment(Pos.CENTER);

    StackPane.setAlignment(keyLbls, Pos.CENTER);
    StackPane.setMargin(keyLbls, new Insets(0, 200, 75, 0));

    keyToggleStatus = new Label("");
    keyToggleStatus.setStyle(" -fx-font-size: 12pt ; -fx-text-fill: white");
    StackPane.setAlignment(keyToggleStatus, Pos.BOTTOM_CENTER);
    StackPane.setMargin(keyToggleStatus,new Insets(0,0,50,0));

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

    VBox keyToggles = new VBox(35, upToggle, leftToggle, rightToggle, downToggle, useToggle);
    keyToggles.setAlignment(Pos.CENTER);

    StackPane.setAlignment(keyToggles, Pos.CENTER);
    StackPane.setMargin(keyToggles, new Insets(0, 0, 75, 450));

    controlsLayout.getChildren().addAll(keyLbls, keyToggles, keyToggleStatus);
    controlsTab.setContent(controlsLayout);

    Tab themeTab = new Tab();
    themeTab.setText("Themes");

    StackPane themesTabLayout = new StackPane();

    HashMap<String, Image> availableThemes = resourceLoader.getThemes();
    themeNames = Arrays
        .copyOf(availableThemes.keySet().toArray(), availableThemes.keySet().toArray().length,
            String[].class);
    themeImages = Arrays
        .copyOf(availableThemes.values().toArray(), availableThemes.values().toArray().length,
            Image[].class);
    numberofThemes = availableThemes.size();

    VBox themesContainer = new VBox(30);
    HBox themeBtns = new HBox(20);
    themesContainer.setAlignment(Pos.CENTER);
    themePreview = new ImageView(themeImages[0]);
    themePreview.setPreserveRatio(true);
    themePreview.setFitWidth(600);

    currentTheme = themeNames[themeIndex];
    String initialName =
        themeNames[themeIndex].substring(0, 1).toUpperCase() + themeNames[themeIndex].substring(1);
    themeName = LabelGenerator.generate(true, themesContainer, initialName, UIColours.WHITE, 25);

    moveThemesLeftBtn = ButtonGenerator.generate(false, themeBtns, "<", UIColours.WHITE, 30);
    themeBtns.getChildren().add(themePreview);
    moveThemesRightBtn = ButtonGenerator.generate(true, themeBtns, ">", UIColours.WHITE, 30);
    themesContainer.getChildren().add(themeBtns);
    themeBtns.setAlignment(Pos.CENTER);

    moveThemesRightBtn.setOnAction(event -> showNextTheme());
    moveThemesLeftBtn.setOnAction(event -> showPreviousTheme());
    moveThemesRightBtn.setFocusTraversable(false);
    moveThemesLeftBtn.setFocusTraversable(false);

    Button selectThemeBtn = ButtonGenerator
        .generate(true, themesContainer, "Select", UIColours.GREEN, 30);
    selectThemeBtn.setOnAction(event -> {
      client.updateTheme(currentTheme);
      bg.setImage(resourceLoader.getBackground());
      for (int i = 0; i < validMaps.size(); i++) {
        mapImages.add(i, mapPreview.getMapPreview(validMaps.get(i)));
      }
      mapView.setImage(mapImages.get(mapsIndex));
    });

    themesContainer.setVisible(true);
    themesTabLayout.getChildren().add(themesContainer);

    StackPane.setAlignment(themesContainer, Pos.CENTER);
    themeTab.setContent(themesTabLayout);

    //Adds the tabs to the tab pane
    settingsTabs.getTabs().addAll(soundTab, graphicsTab, controlsTab, themeTab);

    //Calculates the width of the tabs
    double tabWidth = settingsTabs.getMaxWidth() / settingsTabs.getTabs().size();
    settingsTabs.setTabMinWidth(tabWidth - 5);
    settingsTabs.setTabMaxWidth(tabWidth - 5);

    SingleSelectionModel<Tab> selectionModel = settingsTabs.getSelectionModel();
    selectionModel.select(0);

    root.getChildren().addAll(settingsTabs);

    Button defaultBtn = ButtonGenerator.generate(false, root, "Restore Default Settings", UIColours.YELLOW, 20);
    StackPane.setAlignment(defaultBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(defaultBtn, new Insets(0,0,50,0));
    defaultBtn.setOnAction(event -> {
      Settings.restoreDefaultSettings(this.client);
      bg.setImage(resourceLoader.getBackground());
      updateToggleLabels();
      soundFXSlider.setValue(0.5);
      musicVolumeSlider.setValue(0.5);


    });


    ImageView settingsView = new ImageView("ui/settings.png");
    settingsBtn = ButtonGenerator.generate(true, root, settingsView);
    StackPane.setAlignment(settingsBtn, Pos.TOP_RIGHT);
    StackPane.setMargin(settingsBtn, new Insets(50, 50, 0, 0));
    settingsView.setFitHeight(50);
    settingsView.setFitWidth(50);
    settingsBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);
          if (!viewSettings) {
            viewSettings = true;
            hideItemsOnScreen();

            backBtn.setVisible(false);
            settingsTabs.setVisible(true);
            defaultBtn.setVisible(true);
            settingsBtn.setVisible(true);

          } else {
            viewSettings = false;
            settingsTabs.setVisible(false);
            defaultBtn.setVisible(false);
            showItemsOnScreen();
            if (!isHome) {
              backBtn.setVisible(true);
            }
            Settings.saveSettings();
          }
        });

    startMGameBtn = ButtonGenerator.generate(false, root, "Start", UIColours.GREEN, 30);
    StackPane.setAlignment(startMGameBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(startMGameBtn, new Insets(0, 0, 200, 0));
    startMGameBtn.setOnAction(
        e -> {
          audioController.playSound(Sounds.CLICK);
          client.startMultiplayerGame();
          mapGenerationHandler.stop();
        });

    ImageView instructionsGif = new ImageView("ui/preview.gif");
    instructionsGif.setPreserveRatio(true);
    instructionsGif.setFitWidth(500);
    instructionsGif.setVisible(false);
    StackPane.setAlignment(instructionsGif, Pos.TOP_CENTER);
    StackPane.setMargin(instructionsGif, new Insets(100, 0, 0, 0));
    root.getChildren().add(instructionsGif);

    Label instructionLbl = LabelGenerator.generate(false, root, "Use your keyboard mastery to capture MIPs man "
        + "and collect as many points as possible whilst you control him. "
        + "Beware: If you are captured by a ghoul, you will become one and have to capture"
        + " the new MIPs man all over again. Good luck! ", UIColours.WHITE, 15);
    instructionLbl.setWrapText(true);
    instructionLbl.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(instructionLbl, Pos.CENTER);
    StackPane.setMargin(instructionLbl, new Insets(200, 200, 0, 200));

    Button instructions = ButtonGenerator
        .generate(true, root, "Instructions", UIColours.WHITE, 30);
    instructions.setOnAction(event -> {
      isHome = false;
      audioController.playSound(Sounds.CLICK);
      moveItemsToBackTree();
      settingsBtn.setVisible(false);
      backBtn.setVisible(true);
      itemsOnScreen.add(instructionLbl);
      itemsOnScreen.add(instructionsGif);
      showItemsOnScreen();
    });

    creditsBtn = ButtonGenerator.generate(true, root, "Credits", UIColours.WHITE, 30);
    StackPane.setAlignment(creditsBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(creditsBtn, new Insets(0,0,50,0));

    Text musicHeader = TextGenerator.generate("Game Music:", UIColours.YELLOW, 20);
    Text musicBody = TextGenerator
        .generate("           Trinnox - Fast Flow\n\n", UIColours.WHITE, 20);
    Text bgMusicHeader = TextGenerator.generate("Background Music:", UIColours.YELLOW, 20);
    Text bgMusicBody = TextGenerator
        .generate("     Patrick de Arteaga - Chiptronical\n\n", UIColours.WHITE, 20);

    Text soundFXHeader = TextGenerator.generate("SoundFX:", UIColours.YELLOW, 20);
    Text soundFXBody = TextGenerator.generate("           SubspaceAudio \n\n", UIColours.WHITE, 20);
    Text developersHeader = TextGenerator.generate("Developers:", UIColours.YELLOW, 20);
    Text developersBody = TextGenerator.generate(" \n\nAdam Kona\n\nAlex Banks\n\nJames Weir\n\nLewis Ackroyd\n\nMatty Jones\n\nTim Cheung", UIColours.WHITE, 20);

    TextFlow textFlow = new TextFlow(musicHeader, musicBody, bgMusicHeader, bgMusicBody,
        soundFXHeader, soundFXBody, developersHeader, developersBody);
    textFlow.setTextAlignment(TextAlignment.CENTER);

    Label creditsLbl = new Label(null, textFlow);
    creditsLbl.setVisible(false);
    root.getChildren().add(creditsLbl);
    creditsLbl.setWrapText(true);
    creditsLbl.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(creditsLbl, Pos.CENTER);
    StackPane.setMargin(creditsLbl, new Insets(0,0,0,0));

    creditsBtn.setOnAction(event -> {
      isHome = false;
      audioController.playSound(Sounds.CLICK);
      moveItemsToBackTree();
      itemsOnScreen.add(creditsLbl);
      backBtn.setVisible(true);
      showItemsOnScreen();
    });

    setNameBtn = ButtonGenerator.generate(true, root, "Set Name", UIColours.YELLOW, 30);
    setNameBtn.setOnAction(event -> {
      isHome = false;
      moveItemsToBackTree();
      itemsOnScreen.add(nameEntryView);
      showItemsOnScreen();
    });

    homeOptions = new VBox(20, playBtn, instructions, setNameBtn, creditsBtn);
    root.getChildren().add(homeOptions);
    homeOptions.setAlignment(Pos.CENTER);
    StackPane.setAlignment(homeOptions, Pos.CENTER);
    StackPane.setMargin(homeOptions, new Insets(160,0,0,0));

    backBtn = ButtonGenerator.generate(false, root, "back", UIColours.RED, 30);
    backBtn.setFocusTraversable(false);
    StackPane.setAlignment(backBtn, Pos.BOTTOM_CENTER);
    StackPane.setMargin(backBtn, new Insets(0, 0, 100, 0));
    backBtn.setOnAction(
        event -> {
          audioController.playSound(Sounds.CLICK);

          if (inLobby && gameFound) {
            playerNumberDiscovery.interrupt();
            client.leaveLobby();
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
    reset();

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

}
