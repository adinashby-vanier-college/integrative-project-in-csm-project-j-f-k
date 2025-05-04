package project.optics.jfkt.views;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import project.optics.jfkt.controllers.LensesController;
import project.optics.jfkt.controllers.ThemeController;
import project.optics.jfkt.models.GeneralSetting;
import project.optics.jfkt.models.LensMath;
import project.optics.jfkt.models.LensesModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LensView extends BaseView {
    private final List<Text> parameterLabels = new ArrayList<>();
    private LensesController controller;
    private VBox paramVBox;
    private static final double DEFAULT_SCALE = 50.0;
    private double scale = DEFAULT_SCALE;
    private double centerY;
    private double centerX;
    private int lensCounter = 1;
    private Slider rayLengthSlider;
    private List<Color[]> multiLensColors = new ArrayList<>();


    private double dragStartX = 0;
    private double dragStartY = 0;
    private double offsetX = 0;
    private double offsetY = 0;

    private static final double RAY_STAGE_DURATION = 1.5;
    private Timeline animationTimeline;
    private double lastImageX;
    private double lastImageY;


    private final List<Line> animatedRays = new ArrayList<>();
    private boolean loopEnabled = false; // controlled by 6th button

    // Buttons for animation control (set in createBottom)
    private final List<Duration> keyframeTimes = new ArrayList<>();
    private int currentKeyframeIndex = 0;

    // State tracking
    private int lastNumRays;
    private double lastObjectDistance;
    private double lastObjectHeight;
    private double lastMagnification;
    private double lastFocalLength;
    private List<LensesModel.Lens> lastExtraLenses = new ArrayList<>();
    // Track the dynamically added lens parameter fields
    public final List<TextField[]> extraLensFields = new ArrayList<>();


    // UI Components
    private TextField objectDistanceField;
    private TextField objectHeightField;
    private TextField focalLengthField;
    private TextField magnificationField;
    private TextField numRaysField;
    private Button applyButton;
    private Text parametersHeader;
    private CheckBox showLabelsCheckBox;


    private Text rayLengthLabel;
    private List<Label> lensLabels = new ArrayList<>();
    private List<Text> focalLabels = new ArrayList<>();

    public LensView() {
        super("Lenses");

        Platform.runLater(() -> {
            ImageView concaveView = new ImageView(new Image(getClass().getResource("/images/concave.png").toExternalForm()));
            concaveView.fitWidthProperty().bind(getOptionbutton1().widthProperty().subtract(10));
            concaveView.fitHeightProperty().bind(getOptionbutton1().heightProperty().subtract(10));
            concaveView.setPreserveRatio(true);
            getOptionbutton1().setGraphic(concaveView);

            ImageView convexView = new ImageView(new Image(getClass().getResource("/images/convexe.png").toExternalForm()));
            convexView.fitWidthProperty().bind(getOptionbutton2().widthProperty().subtract(10));
            convexView.fitHeightProperty().bind(getOptionbutton2().heightProperty().subtract(10));
            convexView.setPreserveRatio(true);
            getOptionbutton2().setGraphic(convexView);
        });

        getSlowbutton().setVisible(false);
        getFastbutton().setVisible(false);

        initializeView();
        setupZoomControls();
        updateFonts(ThemeController.getCurrentFont());

        // Add font change listener
        ThemeController.addFontChangeListener(this::updateFonts);

        this.controller = new LensesController(new LensesModel(3, 8.0, 2.0, -0.5, 4.0), this);
        showDefaultLensSystem();
    }
    private void updateFonts(String font) {
        String fontStyle = "-fx-font-family: '" + font + "';";
        if (paramVBox != null) {
            paramVBox.lookupAll(".parameter-label").forEach(node -> {
                if (node instanceof Text) {
                    ((Text)node).setStyle(fontStyle);
                }
            });
        }
        if (parametersHeader != null) parametersHeader.setStyle(fontStyle);
        for (Text label : parameterLabels) {
            label.setStyle(fontStyle);
        }
        if (rayLengthLabel != null) rayLengthLabel.setStyle(fontStyle);
        for (Label label : lensLabels) {
            label.setStyle(fontStyle);
        }
        for (Text label : focalLabels) {
            label.setStyle(fontStyle);
        }
    }

    private void initializeView() {
        Pane animPane = getAnimpane();

        animPane.setOnMousePressed(e -> {
            dragStartX = e.getSceneX();
            dragStartY = e.getSceneY();
        });

        animPane.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - dragStartX;
            double deltaY = e.getSceneY() - dragStartY;

            offsetX += deltaX;
            offsetY += deltaY;

            dragStartX = e.getSceneX();
            dragStartY = e.getSceneY();

            // Re-render the entire scene — this interrupts animation
            updateView(lastNumRays, lastObjectDistance, lastObjectHeight,
                    lastMagnification, lastFocalLength, lastExtraLenses);
        });

        animPane.getChildren().removeIf(node -> {
            // Keep only the zoom controls (HBox)
            return !(node instanceof HBox);
        });

        centerY = animPane.getPrefHeight() / 2;
        centerX = animPane.getPrefWidth() / 2;
    }

    private void showDefaultLensSystem() {
        updateView(0, 8.0, 2.0, -0.5, 4.0, new ArrayList<>());
    }

    @Override
    protected Pane createCenter() {
        Pane baseCenter = (Pane) super.createCenter();

        // Find the original VBox from BaseView
        paramVBox = findParametersVBox(baseCenter);
        if (paramVBox != null) {
            paramVBox.getChildren().clear();

            // === Static Fields ===
            parametersHeader = new Text(GeneralSetting.getString("text.parameters"));
            parametersHeader.setFont(new Font(40));
            parametersHeader.setTextAlignment(TextAlignment.CENTER);
            parametersHeader.setUnderline(true);
            parametersHeader.getStyleClass().add("lensview-parameters-header");
            VBox.setMargin(parametersHeader, new Insets(0, 0, 20, 0));

            HBox objectDistanceHBox = createParameterHBox(GeneralSetting.getString("text.objectDistance"), "8.0");
            HBox objectHeightHBox = createParameterHBox(GeneralSetting.getString("text.objectHeight"), "2.0");
            HBox focalLengthHBox = createParameterHBox(GeneralSetting.getString("text.focalLength"), "4.0");
            HBox magnificationHBox = createParameterHBox(GeneralSetting.getString("text.magnification"), "-0.5");
            HBox numExtraRaysHBox = createParameterHBox(GeneralSetting.getString("text.rays"), "0");

            objectDistanceField = (TextField) objectDistanceHBox.getChildren().get(1);
            objectHeightField = (TextField) objectHeightHBox.getChildren().get(1);
            focalLengthField = (TextField) focalLengthHBox.getChildren().get(1);
            magnificationField = (TextField) magnificationHBox.getChildren().get(1);
            numRaysField = (TextField) numExtraRaysHBox.getChildren().get(1);

            // === Ray Length Slider ===
            rayLengthLabel = new Text(GeneralSetting.getString("text.rayLength"));
            rayLengthLabel.setFont(new Font(20));
            rayLengthLabel.getStyleClass().add("lensview-ray-length-label");
            rayLengthSlider = new Slider(0, 100, 100);
            rayLengthSlider.setShowTickLabels(true);
            rayLengthSlider.setShowTickMarks(true);
            rayLengthSlider.setMajorTickUnit(25);
            rayLengthSlider.setBlockIncrement(5);
            rayLengthSlider.setPrefWidth(250);
            HBox rayLengthBox = new HBox(15, rayLengthLabel, rayLengthSlider);
            rayLengthBox.setAlignment(Pos.CENTER);
            VBox.setMargin(rayLengthBox, new Insets(10, 0, 0, 0));

            // === Apply Button ===
            applyButton = new Button(GeneralSetting.getString("button.apply"));
            applyButton.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 40;");
            HBox buttonHBox = new HBox(applyButton);
            buttonHBox.setAlignment(Pos.CENTER);
            VBox.setMargin(buttonHBox, new Insets(20, 0, 0, 0));

            //Checkbox
            showLabelsCheckBox = new CheckBox(GeneralSetting.getString("label.show"));
            showLabelsCheckBox.setSelected(true); // default ON

            // === Rebuild scrollable VBox ===
            VBox scrollContent = new VBox(10);
            scrollContent.setPadding(new Insets(10));
            scrollContent.setPrefWidth(400);
            scrollContent.getChildren().addAll(
                    parametersHeader,
                    objectDistanceHBox,
                    objectHeightHBox,
                    focalLengthHBox,
                    numExtraRaysHBox,
                    rayLengthBox,
                    showLabelsCheckBox
            );

            this.paramVBox = scrollContent;

            ScrollPane scrollPane = new ScrollPane(scrollContent);
            scrollPane.setPrefSize(420, 600);
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: transparent;");

            VBox paramVBoxContainer = new VBox(scrollPane, buttonHBox);
            paramVBoxContainer.setPrefSize(420, 720);
            paramVBoxContainer.setPadding(new Insets(10));
            paramVBoxContainer.setSpacing(10);

            // === Setup SplitPane ===
            Pane animpane = getAnimpane(); // from BaseView

            SplitPane splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.HORIZONTAL);
            splitPane.getStyleClass().add("content-container");
            paramVBoxContainer.setMinWidth(300);
            paramVBoxContainer.setPrefWidth(300);

            animpane.setMinWidth(800);
            animpane.setPrefWidth(1500);
            animpane.setMaxWidth(Double.MAX_VALUE);

            splitPane.getItems().addAll(paramVBoxContainer, animpane);
            splitPane.setDividerPositions(0.15); // 22% param pane width

            baseCenter.getChildren().clear();
            baseCenter.getChildren().add(splitPane);

            return baseCenter;
        }

        return baseCenter;
    }

    private HBox createParameterHBox(String labelText, String defaultValue) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 10, 5, 10));

        Text label = new Text(labelText);
        label.getStyleClass().add("lensview-ray-length-label");

        TextField textField = new TextField(defaultValue);
        textField.setFont(new Font(18));
        textField.setAlignment(Pos.CENTER);
        textField.setPrefWidth(100);
        textField.setPrefHeight(30);

        hbox.getChildren().addAll(label, textField);
        return hbox;
    }

    private void setupZoomControls() {
        Pane animPane = getAnimpane();
        for (Node node : animPane.getChildren()) {
            if (node instanceof HBox) {
                HBox zoomBox = (HBox) node;
                for (Node btn : zoomBox.getChildren()) {
                    if (btn instanceof Button) {
                        Button button = (Button) btn;
                        if (button.getGraphic() != null) {
                            String imageUrl = ((ImageView)button.getGraphic()).getImage().getUrl();
                            if (imageUrl.contains("Add")) {
                                button.setOnAction(e -> zoom(1.2));
                            } else if (imageUrl.contains("Reduce")) {
                                button.setOnAction(e -> zoom(1/1.2));
                            }
                        }
                    }
                }
            }
        }
    }

    private void zoom(double factor) {
        scale *= factor;
        updateView(lastNumRays, lastObjectDistance, lastObjectHeight,
                lastMagnification, lastFocalLength, lastExtraLenses);
    }

    private VBox findParametersVBox(Pane baseCenter) {
        for (Node node : baseCenter.getChildren()) {
            if (node instanceof VBox) {
                return (VBox) node;
            }
        }
        return null;
    }

    public void updateView(int numRays, double objectDistance, double objectHeight,
                           double magnification, double focalLength,
                           List<LensesModel.Lens> extraLenses) {

        //System.out.println("CenterY: "+centerY);

        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        Pane animPane = getAnimpane();
        animPane.getChildren().removeIf(node -> {
            // Keep only the zoom controls (HBox)
            return !(node instanceof HBox);
        });

        // Store current state
        this.lastNumRays = numRays;
        this.lastObjectDistance = objectDistance;
        this.lastObjectHeight = objectHeight;
        this.lastMagnification = magnification;
        this.lastFocalLength = focalLength;
        this.lastExtraLenses = new ArrayList<>(extraLenses);

        // Apply scaling
        double scaledFL = focalLength * scale;
        double scaledObjDist = objectDistance * scale;
        double scaledObjHeight = objectHeight * scale;

        // Draw components
        drawOpticalAxis(animPane);
        drawMainLens(animPane);
        drawFocalPoints(adjustedCenterX, adjustedCenterY, scaledFL, animPane,1);

        // Step 1: Draw initial object and rays to main lens
        double objX = adjustedCenterX - (objectDistance * scale);
        //double objTopY = centerY - (objectHeight * scale);

        drawObject(objX, adjustedCenterY, objectHeight * scale, animPane);
// Always draw the 3 principal rays
        drawAllPrincipalRays(objX, adjustedCenterY - scaledObjHeight, adjustedCenterX, adjustedCenterY, scaledFL, animPane);

// Draw extra rays if requested
        if (numRays != 0) {
            boolean isConverging = scaledFL > 0;
            drawAdditionalRays(objX, adjustedCenterY - scaledObjHeight, adjustedCenterX, adjustedCenterY, scaledFL, numRays, isConverging, animPane);

        }

// Step 2: Compute image from main lens
        double u = adjustedCenterX - objX;
        double f = focalLength * scale;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = adjustedCenterX + v;
        magnification = -v / u;
        double imageHeight = objectHeight * magnification * scale;
        double imageTopY = adjustedCenterY - imageHeight;

// Optional: draw final image for main lens
        if (Math.abs(imageX) < 5000 && Math.abs(imageHeight) < 5000) {
            if (f > 0 && u > f) {
                drawImageArrow(imageX, adjustedCenterY, imageHeight, animPane);
            } else {
                drawVirtualImageArrow(imageX, adjustedCenterY, imageHeight, animPane);
            }
            if (showLabelsCheckBox == null || showLabelsCheckBox.isSelected()) {
                String labelText = "Image\nM=" + String.format("%.2f", magnification);
                Text label = new Text(imageX + 10, adjustedCenterY - imageHeight / 2, labelText);
                label.getStyleClass().add("lensview-ray-length-label");
                animPane.getChildren().add(label);
            }
        } else {
            //System.out.println("Skipping drawing image arrow because out of bounds");
        }


// Step 3: Draw extra lenses and rays step by step
        drawMultiLensSystem(imageX, imageTopY, adjustedCenterY, extraLenses, animPane);

// Step 4: Draw the lens lines
        drawExtraLensLines(extraLenses,animPane);
        int index = 2;
        for (LensesModel.Lens lens : extraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double f2 = lens.getFocalLength() * scale;
            drawFocalPoints(lensX, adjustedCenterY, f2, animPane,index++);
        }

        setupZoomControls(); // Re-add zoom controls
    }

    private void drawOpticalAxis(Pane pane) {
        double adjustedCenterY = centerY + offsetY;
        Line axis = new Line(0, adjustedCenterY, pane.getPrefWidth(), adjustedCenterY);

        pane.getChildren().add(axis);
    }

    private void drawMainLens(Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        // Calculate object and image Y positions
        double objTopY = adjustedCenterY - lastObjectHeight * scale;
        double objBottomY = adjustedCenterY;

        double u = lastObjectDistance * scale;
        double f = lastFocalLength * scale;

        double imageDistance = 1 / ((1 / f) - (1 / u));
        double magnification = -imageDistance / u;
        double imageHeight = lastObjectHeight * magnification * scale;
        double imageTopY = adjustedCenterY - imageHeight;

        // Check ray deviation — especially for diverging red ray
        double redDeviation = 0;
        if (f < 0) {
            double virtualFocalX = adjustedCenterX - Math.abs(f);
            redDeviation = Math.abs((objTopY - adjustedCenterY) / (adjustedCenterX - virtualFocalX) * 150); // predict ray height offset 150px ahead
        }

        // Use max height from object, image, or diverging ray
        double maxDeviation = Math.max(Math.abs(adjustedCenterY - objTopY), Math.abs(adjustedCenterY - imageTopY));
        maxDeviation = Math.max(maxDeviation, redDeviation);

        double lensHalfHeight = maxDeviation;

        Line lens = new Line(adjustedCenterX, adjustedCenterY - lensHalfHeight,
                adjustedCenterX, adjustedCenterY + lensHalfHeight);

        if (showLabelsCheckBox == null || showLabelsCheckBox.isSelected()) {
            Text lensLabel = new Text(adjustedCenterX - 30, adjustedCenterY - lensHalfHeight - 10, "Main Lens");
            lensLabel.setFill(Color.BLACK);
            lensLabel.getStyleClass().add("lensview-ray-length-label");
            pane.getChildren().add(lensLabel);
        }


        lens.setStrokeWidth(3);
        pane.getChildren().add(lens);
    }

    private void drawFocalPoints(double lensX, double centerY, double focalLengthPx, Pane pane, int lensIndex) {
        // Near focal point
        Circle nearFocal = new Circle(lensX - focalLengthPx, centerY, 5);
        nearFocal.setFill(Color.DARKBLUE);
        Text nearLabel = new Text(lensX - focalLengthPx - 30, centerY - 10, "F" + lensIndex);

        // Far focal point
        Circle farFocal = new Circle(lensX + focalLengthPx, centerY, 5);
        farFocal.setFill(Color.DARKBLUE);
        Text farLabel = new Text(lensX + focalLengthPx + 10, centerY - 10, "F'" + lensIndex);

        nearLabel.getStyleClass().add("lensview-ray-length-label");
        farLabel.getStyleClass().add("lensview-ray-length-label");

        pane.getChildren().addAll(nearFocal, farFocal);
        if (showLabelsCheckBox == null || showLabelsCheckBox.isSelected()) {
            pane.getChildren().addAll(nearLabel, farLabel);
        }
    }

    private void drawObject(double x, double baseY, double heightPx, Pane pane) {
        // Object line
        Line objLine = new Line(x, baseY, x, baseY - heightPx);

        objLine.setStrokeWidth(2);

        // Arrow head
        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );


        // Label
        Text label = new Text(x - 30, baseY - heightPx/2, GeneralSetting.getString("text.object"));
        label.getStyleClass().add("lensview-ray-length-label");

        pane.getChildren().addAll(objLine, arrowHead);
        if (showLabelsCheckBox == null || showLabelsCheckBox.isSelected()) {
            pane.getChildren().add(label);
        }
    }

    private void drawAllPrincipalRays(double objX, double objTopY,
                                      double lensX, double lensY,
                                      double focalLengthPx, Pane pane) {

        double u = lensX - objX;
        double f = focalLengthPx;
        boolean isConverging = f > 0;

        double v = LensMath.calculateImageDistance(u, f);
        double m = LensMath.calculateMagnification(v, u);
        double imageX = LensMath.calculateImageX(lensX, v);
        double imageY = LensMath.calculateImageY(lensY, objTopY, m);

        double extension = getRayExtension(); // scaled
        boolean isVirtual = (isConverging && u < f) || (!isConverging);

        // === RED RAY: Parallel to axis → refracted through/away from focal point ===
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted;
        ray1.setStroke(Color.RED);

        if (isVirtual) {
            double dx = lensX - imageX;
            double dy = objTopY - imageY;
            double slope = dy / dx;
            ray1Refracted = new Line(lensX, objTopY, lensX + extension, objTopY + slope * extension);

            drawVirtualBacktrace(pane, lensX, objTopY, imageX, imageY, extension, Color.RED);
        } else {
            double dx = imageX - lensX;
            double dy = imageY - objTopY;
            double slope = dy / dx;
            ray1Refracted = new Line(lensX, objTopY,
                    imageX + extension, imageY + slope * extension);
        }
        ray1Refracted.setStroke(Color.RED);
        pane.getChildren().addAll(ray1, ray1Refracted);

        // === BLUE RAY: Through center of lens ===
        Line ray2 = new Line(objX, objTopY, lensX, lensY);
        ray2.setStroke(Color.BLUE);
        pane.getChildren().add(ray2);

        Line ray2Refracted = new Line(lensX, lensY, imageX, imageY);
        ray2Refracted.setStroke(Color.BLUE);

        double dx2 = imageX - lensX;
        double dy2 = imageY - lensY;

        if (isVirtual && u < f) {
            drawVirtualBacktrace(pane, lensX, lensY, imageX, imageY, extension, Color.BLUE);
            ray2Refracted.setEndX(lensX + extension);
            ray2Refracted.setEndY(lensY + (dy2 / dx2) * extension);
        } else {
            ray2Refracted.setEndX(imageX + extension);
            ray2Refracted.setEndY(imageY + (dy2 / dx2) * extension);
        }
        pane.getChildren().add(ray2Refracted);

        // === GREEN RAY: Through near focal → refracted parallel ===
        double nearFocalX = lensX - f;

        double targetY = lensY + (objTopY - lensY) * f / (objX - nearFocalX);
        Line ray3 = new Line(objX, objTopY, lensX, targetY);
        ray3.setStroke(Color.GREEN);

        Line ray3Refracted;
        if (isVirtual) {
            ray3Refracted = new Line(lensX, targetY, lensX + extension, targetY);
            drawVirtualBacktrace(pane, lensX, targetY, imageX, imageY, extension, Color.GREEN);
        } else {
            double dx3 = imageX - lensX;
            double dy3 = imageY - targetY;
            double slope = dy3 / dx3;
            ray3Refracted = new Line(lensX, targetY,
                    imageX + extension, imageY + slope * extension);
        }
        ray3Refracted.setStroke(Color.GREEN);
        pane.getChildren().addAll(ray3, ray3Refracted);
    }

    private void drawVirtualBacktrace(Pane pane, double startX, double startY,
                                      double imageX, double imageY,
                                      double extension, Color color) {
        double dx = imageX - startX;
        double dy = imageY - startY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 1e-5) {
            double unitX = dx / dist;
            double unitY = dy / dist;

            double endX = imageX + unitX * extension;
            double endY = imageY + unitY * extension;

            Line backtrace = new Line(startX, startY, endX, endY);
            backtrace.setStroke(color);
            backtrace.setStrokeWidth(1);
            backtrace.getStrokeDashArray().addAll(6.0, 6.0);
            pane.getChildren().add(backtrace);
        }
    }

    private void drawImageArrow(double x, double baseY, double heightPx, Pane pane) {
        if (Double.isNaN(x) || Double.isNaN(baseY) || Math.abs(x) > 5000) {
            //System.out.println("Skipping real image arrow (out of bounds)");
            return;
        }

        double topY = baseY - heightPx;  // Correct regardless of sign of height

        Line imageLine = new Line(x, baseY, x, topY);
        imageLine.setStrokeWidth(2);
        imageLine.setStroke(Color.BLACK);

        Polygon arrowHead;

        if (heightPx >= 0) {
            // Arrow pointing up
            arrowHead = new Polygon(
                    x, topY,
                    x - 5, topY + 10,
                    x + 5, topY + 10
            );
        } else {
            // Arrow pointing down
            arrowHead = new Polygon(
                    x, topY,
                    x - 5, topY - 10,
                    x + 5, topY - 10
            );
        }

        arrowHead.setFill(Color.BLACK);
        pane.getChildren().addAll(imageLine, arrowHead);
    }

    private void drawVirtualImageArrow(double x, double baseY, double heightPx, Pane pane) {
        if (Double.isNaN(x) || Double.isNaN(baseY) || Math.abs(x) > 5000 || Math.abs(heightPx) > 5000) {
            //System.out.println("Skipping virtual image arrow (out of bounds)");
            return;
        }

        double topY = baseY - heightPx;

        Line imageLine = new Line(x, baseY, x, topY);
        imageLine.setStroke(Color.PURPLE);
        imageLine.setStrokeWidth(2);
        imageLine.getStrokeDashArray().addAll(5d, 5d);

        Polygon arrowHead = new Polygon(
                x, topY,
                x - 5, topY + 10,
                x + 5, topY + 10
        );
        arrowHead.setFill(Color.PURPLE);
        arrowHead.setOpacity(0.6);

        pane.getChildren().addAll(imageLine, arrowHead);
    }


    private void drawAdditionalRays(double objX, double objTopY,
                                    double lensX, double lensY,
                                    double focalLengthPx, int numExtraRays,
                                    boolean isConverging, Pane pane) {

        double u = lensX - objX;
        if (u == 0) return;

        double v = 1 / (1 / focalLengthPx - 1 / u);
        double imageX = lensX + v;
        double imageHeight = -(v / u) * (lensY - objTopY);
        double imageY = lensY - imageHeight;

        double objectHeight = lensY - objTopY;
        double spacing = objectHeight / (numExtraRays + 1);
        double extension = getRayExtension();

        for (int i = 1; i <= numExtraRays; i++) {
            double yOffset = spacing * i;
            double rayStartY = objTopY + yOffset;

            Line incidentRay = new Line(objX, rayStartY, lensX, rayStartY);
            incidentRay.setStroke(Color.ORANGE);
            incidentRay.setStrokeWidth(1);

            if (isConverging) {
                double imageYOffset = imageY + yOffset * (imageHeight / objectHeight);
                double slope = (imageYOffset - rayStartY) / (imageX - lensX);
                Line refractedRay = new Line(lensX, rayStartY,
                        imageX + extension, imageYOffset + slope * extension);
                refractedRay.setStroke(Color.ORANGE);
                refractedRay.setStrokeWidth(1);
                pane.getChildren().addAll(incidentRay, refractedRay);
            } else {
                double virtualFocalX = lensX - Math.abs(focalLengthPx);
                double slope = (rayStartY - lensY) / (lensX - virtualFocalX);

                Line refractedRay = new Line(lensX, rayStartY,
                        lensX + extension, rayStartY + slope * extension);
                refractedRay.setStroke(Color.ORANGE);
                refractedRay.setStrokeWidth(1);

                double dx = lensX - imageX;
                double dy = rayStartY - imageY;
                double dist = Math.sqrt(dx * dx + dy * dy);
                double unitX = dx / dist;
                double unitY = dy / dist;

                Line virtualBack = new Line(
                        lensX, rayStartY,
                        imageX - unitX * extension, imageY - unitY * extension
                );

                virtualBack.setStroke(Color.ORANGE);
                virtualBack.setStrokeWidth(1);
                virtualBack.getStrokeDashArray().addAll(5d, 5d);

                pane.getChildren().addAll(incidentRay, refractedRay, virtualBack);
            }
        }
    }

    //Multi Lens System
    private void drawMultiLensSystem(double objX, double objTopY, double centerY,
                                     List<LensesModel.Lens> lenses, Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY;

        double currObjX = objX;
        double currObjTopY = objTopY;
        double currBaseY = adjustedCenterY;
        //System.out.println("CurrBaseY: "+currBaseY);
        double currObjHeight = currBaseY - currObjTopY;
        int imageCounter = 2;

        for (LensesModel.Lens lens : lenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double focalLengthPx = lens.getFocalLength() * scale;
            boolean isConverging = lens.isConverging();

            double u = lensX - currObjX;
            if (u == 0) continue;

            double v = LensMath.calculateImageDistance(u, focalLengthPx);
            double m = LensMath.calculateMagnification(v, u);
            double imageX = LensMath.calculateImageX(lensX, v);
            double imageHeight = currObjHeight * m;
            double imageTopY = currBaseY - imageHeight;

            // === Draw the rays using consistent vertical baseline ===
            drawAllPrincipalRays(currObjX, currObjTopY, lensX, currBaseY, focalLengthPx, pane);

            // Draw extra lens
            drawExtraLens(pane, lensX, currBaseY, currObjTopY, imageTopY, isConverging);

            // === Draw image arrow at correct position ===
            if (Math.abs(imageX) < 5000 && Math.abs(imageHeight) < 5000) {
                if (isConverging && u > focalLengthPx) {
                    drawImageArrow(imageX, currBaseY, imageHeight, pane);
                } else {
                    drawVirtualImageArrow(imageX, currBaseY, imageHeight, pane);
                }
            }
            // Label the image
            String info = String.format("Image %d\nM=%.2f", imageCounter++, m);
            Text label = new Text(imageX + 10, currBaseY - imageHeight / 2, info);
            label.getStyleClass().add("lensview-ray-length-label");
            pane.getChildren().add(label);

            //For next lens
            currObjX = imageX;
            currObjTopY = currBaseY - imageHeight;
            currObjHeight = imageHeight;
        }
    }

    private void drawExtraLensLines(List<LensesModel.Lens> extraLenses, Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        int lensIndex = 2;
        for (LensesModel.Lens lens : extraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double focalLengthPx = lens.getFocalLength() * scale;

            // Only draw focal points, since the lens body is now drawn in drawMultiLensSystem()
            drawFocalPoints(lensX, adjustedCenterY, focalLengthPx, pane,lensIndex++);
        }
    }


    private void drawExtraLens(Pane pane, double lensX, double lensY,
                               double objTopY, double imageTopY, boolean isConverging) {

        double objDeviation = Math.abs(lensY - objTopY);
        double imageDeviation = Math.abs(lensY - imageTopY);

        double redDeviation = 0;
        if (!isConverging) {
            double virtualFocalX = lensX - 50; // 1 unit focal in px (approx)
            redDeviation = Math.abs((objTopY - lensY) / (lensX - virtualFocalX) * 150);
        }

        double maxDeviation = Math.max(objDeviation, imageDeviation);
        maxDeviation = Math.max(maxDeviation, redDeviation);

        double minAllowed = 30;
        double maxAllowed = 300;
        maxDeviation = Math.max(minAllowed, Math.min(maxDeviation, maxAllowed));

        Line lensLine = new Line(lensX, lensY - maxDeviation, lensX, lensY + maxDeviation);
        lensLine.setStrokeWidth(3);
        lensLine.setStroke(isConverging ? Color.BLUE : Color.RED);
        if (!isConverging) lensLine.getStrokeDashArray().addAll(5d, 5d);

        Text label = new Text(lensX - 25, lensY - maxDeviation - 10,
                isConverging ? GeneralSetting.getString("label.converging") : GeneralSetting.getString("label.diverging"));
        label.setFill(isConverging ? Color.BLUE : Color.RED);
        label.getStyleClass().add("lensview-ray-length-label");

        pane.getChildren().addAll(lensLine);
        if (showLabelsCheckBox == null || showLabelsCheckBox.isSelected()) {
            pane.getChildren().add(label);
        }
    }


    // Adds converging lens parameters with default values
    public void addConvergingLensParams(double defaultPosition, double defaultFocalLength) {
        addExtraLensParamFields(defaultPosition, defaultFocalLength);
    }

    // Adds diverging lens parameters with default values
    public void addDivergingLensParams(double defaultPosition, double defaultFocalLength) {
        addExtraLensParamFields(defaultPosition, defaultFocalLength);
    }

    // General method to add parameter fields
    private void addExtraLensParamFields(double position, double focalLength) {
        if (paramVBox != null) {
            // Wrapper VBox for each lens entry
            VBox lensGroup = new VBox(5);
            lensGroup.setPadding(new Insets(10));
            lensGroup.getStyleClass().add("lens-background");
            lensGroup.setStyle("-fx-border-color: grey; -fx-border-width: 1;");


            String labelText = (focalLength > 0 ? GeneralSetting.getString("label.converging") : GeneralSetting.getString("label.diverging")) + GeneralSetting.getString("label.lens#") + lensCounter++;

            Label lensLabel = new Label(labelText);
            lensLabel.getStyleClass().add("lensview-ray-length-label");

            HBox lensPositionBox = createParameterHBox(GeneralSetting.getString("label.position"), String.valueOf(position));
            HBox lensFocalLengthBox = createParameterHBox(GeneralSetting.getString("text.focalLength"), String.valueOf(focalLength));

            TextField positionField = (TextField) lensPositionBox.getChildren().get(1);
            TextField focalField = (TextField) lensFocalLengthBox.getChildren().get(1);
            extraLensFields.add(new TextField[]{positionField, focalField});

            Button removeBtn = new Button("X");
            removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            removeBtn.setOnAction(e -> {
                paramVBox.getChildren().remove(lensGroup);
                extraLensFields.removeIf(pair -> pair[0] == positionField && pair[1] == focalField);
                resetLensLabels();
            });

            HBox header = new HBox(lensLabel, removeBtn);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setSpacing(10);

            lensGroup.getChildren().addAll(header, lensPositionBox, lensFocalLengthBox);

            // Add before Apply button (last element)
            paramVBox.getChildren().add(paramVBox.getChildren().size() - 1, lensGroup);
        }
    }

    private void resetLensLabels() {
        lensCounter = 1;
        for (Node node : paramVBox.getChildren()) {
            if (node instanceof VBox lensGroup) {
                for (Node inner : lensGroup.getChildren()) {
                    if (inner instanceof HBox header) {
                        for (Node labelNode : header.getChildren()) {
                            if (labelNode instanceof Label label) {
                                if (label.getText().contains("Len")) {
                                    boolean isConverging = label.getText().contains("Con");
                                    label.setText((isConverging ? GeneralSetting.getString("label.converging") : GeneralSetting.getString("label.diverging")) + GeneralSetting.getString("label.lens#") + lensCounter++);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Animation
    public void startAnimation() {
        StopAnimation(); // Reset any running animation

        //System.out.println("Starting animation... Loop mode: " + (loopEnabled ? "ON" : "OFF"));

        animationTimeline = new Timeline();
        animationTimeline.setCycleCount(loopEnabled ? Timeline.INDEFINITE : 1);
        keyframeTimes.clear();
        currentKeyframeIndex = 0;

        Pane animPane = getAnimpane();

        // === Clear everything except zoom controls ===
        animPane.getChildren().removeIf(node -> !(node instanceof HBox));
        animatedRays.clear();

        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        // === Redraw static components ===
        drawOpticalAxis(animPane);
        drawMainLens(animPane);
        drawFocalPoints(adjustedCenterX, adjustedCenterY, lastFocalLength * scale, animPane, 1);

        double objX = adjustedCenterX - lastObjectDistance * scale;
        double objTopY = adjustedCenterY - lastObjectHeight * scale;

        drawObject(objX, adjustedCenterY, lastObjectHeight * scale, animPane);

        // === Phase 1 animation ===
        double totalDuration = animateLensStage(
                objX, objTopY,
                adjustedCenterX, adjustedCenterY,
                lastFocalLength * scale,
                lastNumRays,
                0.0,
                animationTimeline
        );

        // === Animate extra lenses ===
        for (int i = 0; i < lastExtraLenses.size(); i++) {
            LensesModel.Lens lens = lastExtraLenses.get(i);
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double lensF = lens.getFocalLength() * scale;

            // Compute and draw the lens body
            boolean isConverging = lensF > 0;
            double u = lensX - lastImageX;
            double v = LensMath.calculateImageDistance(u, lensF);
            double m = LensMath.calculateMagnification(v, u);
            double imageHeight = m * (adjustedCenterY - lastImageY);
            double imageTopY = adjustedCenterY - imageHeight;

            drawExtraLens(animPane, lensX, adjustedCenterY, lastImageY, imageTopY, isConverging);


            // Draw focal points
            drawFocalPoints(lensX, adjustedCenterY, lensF, animPane, i + 2);

            // Animate rays through this lens
            totalDuration += animateLensStage(
                    lastImageX, lastImageY,
                    lensX, adjustedCenterY,
                    lensF,
                    lastNumRays,
                    totalDuration,
                    animationTimeline
            );
        }

        animationTimeline.play();
    }

    public void StopAnimation() {
        //System.out.println("Stop");
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }

    public void stopAnimation() {
        //System.out.println("Pause");
        if (animationTimeline != null) {
            animationTimeline.pause();
        }
    }

    public void stepForwardAnimation() {
        if (animationTimeline != null && !keyframeTimes.isEmpty()) {
            animationTimeline.pause(); // Pause current animation
            currentKeyframeIndex = Math.min(currentKeyframeIndex + 1, keyframeTimes.size() - 1);
            animationTimeline.jumpTo(keyframeTimes.get(currentKeyframeIndex));
            //System.out.println("Jumped to frame: " + currentKeyframeIndex);
        }
    }

    public void stepBackAnimation() {
        if (animationTimeline != null && !keyframeTimes.isEmpty()) {
            animationTimeline.pause(); // Pause current animation
            currentKeyframeIndex = Math.max(currentKeyframeIndex - 1, 0);
            animationTimeline.jumpTo(keyframeTimes.get(currentKeyframeIndex));
            //System.out.println("Jumped to frame: " + currentKeyframeIndex);
        }
    }

    public void toggleLoopMode() {
        loopEnabled = !loopEnabled;
        //System.out.println("Loop mode is now " + (loopEnabled ? "ON" : "OFF"));
    }

    private double animateLensStage(double objX, double objTopY, double lensX, double lensY,
                                    double focalLengthPx, int numExtraRays, double offsetSeconds,
                                    Timeline timeline) {

        double u = lensX - objX;
        if (u == 0) return 0;

        double f = focalLengthPx;
        double v = LensMath.calculateImageDistance(u, f);
        double m = LensMath.calculateMagnification(v, u);
        double imageX = LensMath.calculateImageX(lensX, v);
        double imageY = LensMath.calculateImageY(lensY, objTopY, m);

        boolean isConverging = f > 0;
        boolean isVirtual = (isConverging && u < f) || (!isConverging);

        double rayExtension = getRayExtension();
        Pane animPane = getAnimpane();

        double nearFocalX = lensX - f;
        double targetY = lensY + (objTopY - lensY) * f / (objX - nearFocalX);

        Line ray1 = new Line(objX, objTopY, objX, objTopY);
        Line ray2 = new Line(objX, objTopY, objX, objTopY);
        Line ray3 = new Line(objX, objTopY, objX, objTopY);

        ray1.setStroke(Color.RED);
        ray2.setStroke(Color.BLUE);
        ray3.setStroke(Color.GREEN);
        animPane.getChildren().addAll(ray1, ray2, ray3);

        double speed = 1.0;

        timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray1.endXProperty(), lensX)),
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray2.endXProperty(), lensX),
                        new KeyValue(ray2.endYProperty(), lensY)),
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray3.endXProperty(), lensX),
                        new KeyValue(ray3.endYProperty(), targetY))
        );

        Line ray1Refracted = new Line(lensX, objTopY, lensX, objTopY);
        Line ray2Refracted = new Line(lensX, lensY, lensX, lensY);
        Line ray3Refracted = new Line(lensX, targetY, lensX, targetY);

        ray1Refracted.setStroke(Color.RED);
        ray2Refracted.setStroke(Color.BLUE);
        ray3Refracted.setStroke(Color.GREEN);
        animPane.getChildren().addAll(ray1Refracted, ray2Refracted, ray3Refracted);

        double slope1, slope2, slope3;

        if (isVirtual) {
            slope1 = (objTopY - imageY) / (lensX - imageX);
            slope2 = (lensY - imageY) / (lensX - imageX);
            slope3 = (targetY - imageY) / (lensX - imageX);

            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.2),
                            new KeyValue(ray1Refracted.endXProperty(), lensX + rayExtension),
                            new KeyValue(ray1Refracted.endYProperty(), objTopY + slope1 * rayExtension)),
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.4),
                            new KeyValue(ray2Refracted.endXProperty(), lensX + rayExtension),
                            new KeyValue(ray2Refracted.endYProperty(), lensY + slope2 * rayExtension)),
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.6),
                            new KeyValue(ray3Refracted.endXProperty(), lensX + rayExtension),
                            new KeyValue(ray3Refracted.endYProperty(), targetY + slope3 * rayExtension))
            );

            animateBacktrace(ray1Refracted, lensX, objTopY, imageX, imageY, Color.RED, animPane, timeline, offsetSeconds + speed + 0.8);
            animateBacktrace(ray2Refracted, lensX, lensY, imageX, imageY, Color.BLUE, animPane, timeline, offsetSeconds + speed + 1.0);
            animateBacktrace(ray3Refracted, lensX, targetY, imageX, imageY, Color.GREEN, animPane, timeline, offsetSeconds + speed + 1.2);

        } else {
            slope1 = (imageY - objTopY) / (imageX - lensX);
            slope2 = (imageY - lensY) / (imageX - lensX);
            slope3 = (imageY - targetY) / (imageX - lensX);

            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.2),
                            new KeyValue(ray1Refracted.endXProperty(), imageX),
                            new KeyValue(ray1Refracted.endYProperty(), imageY)),
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.4),
                            new KeyValue(ray2Refracted.endXProperty(), imageX),
                            new KeyValue(ray2Refracted.endYProperty(), imageY)),
                    new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.6),
                            new KeyValue(ray3Refracted.endXProperty(), imageX),
                            new KeyValue(ray3Refracted.endYProperty(), imageY))
            );

            if (getRayLengthFactor() > 0) {
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.seconds(offsetSeconds + speed + 0.8),
                                new KeyValue(ray1Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray1Refracted.endYProperty(), imageY + slope1 * rayExtension)),
                        new KeyFrame(Duration.seconds(offsetSeconds + speed + 1.0),
                                new KeyValue(ray2Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray2Refracted.endYProperty(), imageY + slope2 * rayExtension)),
                        new KeyFrame(Duration.seconds(offsetSeconds + speed + 1.2),
                                new KeyValue(ray3Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray3Refracted.endYProperty(), imageY + slope3 * rayExtension))
                );
            }
        }

        Line imageArrow = new Line(imageX, lensY, imageX, imageY);
        imageArrow.setStroke(Color.BLACK);
        imageArrow.setStrokeWidth(2);
        Polygon arrowHead = new Polygon(imageX, imageY, imageX - 5, imageY + 10, imageX + 5, imageY + 10);
        arrowHead.setFill(Color.BLACK);
        animPane.getChildren().addAll(imageArrow, arrowHead);

        lastImageX = imageX;
        lastImageY = imageY;

        return speed + 1.5;
    }

    private void animateBacktrace(Line baseRay, double startX, double startY, double imageX, double imageY,
                                  Color color, Pane animPane, Timeline timeline, double delay) {
        double dx = imageX - startX;
        double dy = imageY - startY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1e-4) return;

        double unitX = dx / dist;
        double unitY = dy / dist;
        double extension = getRayExtension();

        Line backtrace = new Line(startX, startY, startX, startY);
        backtrace.setStroke(color);
        backtrace.setStrokeWidth(1.5);
        backtrace.getStrokeDashArray().setAll(6.0, 6.0);
        animPane.getChildren().add(backtrace);

        // First: animate from start to image position
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(delay),
                        new KeyValue(backtrace.endXProperty(), imageX),
                        new KeyValue(backtrace.endYProperty(), imageY))
        );

        // Then: extend it further past the image position
        double extX = imageX + unitX * extension;
        double extY = imageY + unitY * extension;

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(delay + 0.3),
                        new KeyValue(backtrace.endXProperty(), extX),
                        new KeyValue(backtrace.endYProperty(), extY))
        );
    }

    //Test cases


    public void resetDragOffset() {
        offsetX = 0;
        offsetY = 0;
    }

    public void resetLensCounter() {
        lensCounter = 1;
    }

    public void addLensColors(Color c1, Color c2, Color c3) {
        multiLensColors.add(new Color[]{c1, c2, c3});
    }

    public void clearLensColors() {
        multiLensColors.clear();
    }

    public List<Color[]> getLensColors() {
        return multiLensColors;
    }


    // UI Access Methods
    public Button getApplyButton() {
        return applyButton;
    }

    public Button getConvergingButton() {
        return getOptionbutton1();
    }

    public Button getDivergingButton() {
        return getOptionbutton2();
    }

    public String getObjectDistanceField() {
        return objectDistanceField.getText();
    }

    public String getObjectHeightField() {
        return objectHeightField.getText();
    }

    public String getFocalLengthField() {
        return focalLengthField.getText();
    }

    public String getMagnificationField() {
        return magnificationField.getText();
    }

    public String getNumExtraRaysField() {
        return numRaysField.getText();
    }

    public double getRayLengthFactor() {
        return rayLengthSlider != null ? rayLengthSlider.getValue() / 100.0 : 1.0;
    }

    private double getRayExtensionBeyondImage(double baseLength) {
        return baseLength * getRayLengthFactor();  // getRayLengthFactor() from 0 to 1
    }

    private double getRayExtension() {
        return getRayLengthFactor() * 3.0 * scale;
    }


    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(GeneralSetting.getString("title.inputErr"));
        alert.setHeaderText(GeneralSetting.getString("text.invalidPar"));
        alert.setContentText(message);
        alert.showAndWait();
    }
}