    package project.optics.jfkt.views;
    import javafx.application.Platform;
    import javafx.geometry.Insets;
    import javafx.geometry.Pos;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.Region;
    import javafx.scene.layout.VBox;
    import javafx.scene.text.Font;
    import javafx.scene.text.FontWeight;
    import javafx.stage.Stage;
    import project.optics.jfkt.controllers.MainController;
    import project.optics.jfkt.controllers.ThemeController;

    import java.io.IOException;

    public class MainView extends BorderPane{
    private Stage primaryStage;
        private MainController mainController;
        private Label title;

        public MainView(Stage primaryStage) {
            this.primaryStage = primaryStage;
            mainController = new MainController(primaryStage);

            // Initialize with current theme and font
            this.getStyleClass().add(ThemeController.getCurrentTheme());
            applyCurrentFont();

            // Add font change listener
            ThemeController.addFontChangeListener(font -> {
                Platform.runLater(() -> {
                    applyCurrentFont();
                    updateTitleFont();
                });
            });

            this.setCenter(createContent());
            this.setTop(createMenu());
        }

        private void applyCurrentFont() {
            this.setStyle("-fx-font-family: '" + ThemeController.getCurrentFont() + "';");
        }

        private void updateTitleFont() {
            // This ensures the title keeps its size and weight while changing font family
            title.setStyle("-fx-font-family: '" + ThemeController.getCurrentFont() + "';" +
                    "-fx-font-size: 35px; -fx-font-weight: bold;");
        }

        private MenuBar createMenu() {
            MenuBar menuBar = new MenuBar();
            Menu fileMenu = new Menu("File");
            MenuItem quit = new MenuItem("Quit");
            MenuItem aboutUs = new MenuItem("About Us");
            MenuItem help = new MenuItem("Help");
            Menu settings = new Menu("Settings");

            MenuItem theme = new MenuItem("Theme");
            MenuItem animation = new MenuItem("Animation");
            MenuItem general = new MenuItem("General");

            quit.setOnAction(e -> mainController.onQuitButtonPressed());
            aboutUs.setOnAction(e -> mainController.onAboutUsPressed());
            help.setOnAction(e -> mainController.onHelpPressed());

            theme.setOnAction(e->mainController.onThemeButtonPressed());
            animation.setOnAction(e->mainController.onAnimationButtonPressed());
            general.setOnAction(event -> mainController.onGeneralSettingsButtonPressed());

            fileMenu.getItems().addAll(quit,aboutUs, help);

            settings.getItems().addAll(theme, animation, general);

            menuBar.getMenus().addAll(fileMenu,settings );

            return menuBar;
        }


        private Region createContent() {
            VBox container = new VBox(30);

            title = new Label("Geometric Optics Simulation");
            title.getStyleClass().add("main-title");
            updateTitleFont(); // Initialize the font with current settings
            title.setPadding(new Insets(0, 0, 80, 0));

            double imageWidth = 300;
            double imageHeight = 300;


            ImageView educationImageView = new ImageView(new Image(this.getClass().getResource("/images/education3.jpg").toExternalForm()));
            educationImageView.setFitWidth(imageWidth);
            educationImageView.setFitHeight(imageHeight);
            educationImageView.setPreserveRatio(true);

            ImageView refractionImageView = new ImageView(new Image(this.getClass().getResource("/images/refraction.jpg").toExternalForm()));
            refractionImageView.setFitWidth(imageWidth);
            refractionImageView.setFitHeight(imageHeight);
            refractionImageView.setPreserveRatio(true);

            ImageView thinLensesImageView = new ImageView(new Image(this.getClass().getResource("/images/thinlenses.png").toExternalForm()));
            thinLensesImageView.setFitWidth(imageWidth);
            thinLensesImageView.setFitHeight(221);
            thinLensesImageView.setPreserveRatio(false);

            ImageView mirrorImageView = new ImageView(new Image(this.getClass().getResource("/images/mirror.png").toExternalForm()));
            mirrorImageView.setFitWidth(imageWidth);
            mirrorImageView.setFitHeight(imageHeight);
            mirrorImageView.setPreserveRatio(true);


            Button educationGameButton = new Button("Education Game");
            educationGameButton.setPrefSize(200, 20);
            educationGameButton.setOnAction(event -> mainController.onEducationButtonPressed());

            Button refractionButton = new Button("Refraction");
            refractionButton.setPrefSize(200, 20);
            refractionButton.setOnAction(event -> mainController.onRefractionButtonPressed());

            Button thinLensesButton = new Button("Thin Lenses");
            thinLensesButton.setPrefSize(200, 20);
            thinLensesButton.setOnAction(e-> {
                try {
                    mainController.onThinLensesPressed();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });


            Button mirrorButton = new Button("Mirror");
            mirrorButton.setPrefSize(200, 20);
            mirrorButton.setOnAction(e-> mainController.onMirrorPressed());



            VBox educationGroup = new VBox(10, educationImageView, educationGameButton);
            VBox refractionGroup = new VBox(10, refractionImageView, refractionButton);
            VBox thinLensesGroup = new VBox(10, thinLensesImageView, thinLensesButton);
            VBox mirrorGroup = new VBox(10, mirrorImageView, mirrorButton);


            for (VBox group : new VBox[]{educationGroup, refractionGroup, thinLensesGroup, mirrorGroup}) {
                group.setAlignment(Pos.CENTER);
            }


            HBox contentBox = new HBox(30, educationGroup, refractionGroup, thinLensesGroup, mirrorGroup);
            contentBox.setAlignment(Pos.CENTER);

            container.getChildren().addAll(title,contentBox);
            container.setAlignment(Pos.CENTER);

            return container;
        }

    }
