package project.optics.jfkt.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import project.optics.jfkt.models.LensesModel;

import java.util.ArrayList;
import java.util.List;
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

    private final List<Line> animatedRays = new ArrayList<>();
    private boolean loopEnabled = false; // controlled by 6th button

    // Buttons for animation control (set in createBottom)
    private Button stepBackButton;
    private Button stepForwardButton;
    private Button sixthButton;

    public Button getStepBackButton() { return stepBackButton; }
    public Button getStepForwardButton() { return stepForwardButton; }
    public Button getSixthButton() { return sixthButton; }

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

    private Text rayLengthLabel;
    private List<Label> lensLabels = new ArrayList<>();
    private List<Text> focalLabels = new ArrayList<>();

    public LensView() {
        super("Lenses");
        initializeView();
        setupZoomControls();
        runRayIntersectionTest();
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
        updateView(3, 8.0, 2.0, -0.5, 4.0, new ArrayList<>());
    }

    @Override
    protected Pane createCenter() {
        Pane baseCenter = (Pane) super.createCenter();

        // Find the original VBox from BaseView
        paramVBox = findParametersVBox(baseCenter);
        if (paramVBox != null) {
            paramVBox.getChildren().clear();

            // === Static Fields ===
            parametersHeader = new Text("Parameters:");
            parametersHeader.setFont(new Font(40));
            parametersHeader.setTextAlignment(TextAlignment.CENTER);
            parametersHeader.setUnderline(true);
            parametersHeader.getStyleClass().add("lensview-parameters-header");
            VBox.setMargin(parametersHeader, new Insets(0, 0, 20, 0));

            HBox objectDistanceHBox = createParameterHBox("Object Distance", "8.0");
            HBox objectHeightHBox = createParameterHBox("Object Height", "2.0");
            HBox focalLengthHBox = createParameterHBox("Focal Length", "4.0");
            HBox magnificationHBox = createParameterHBox("Magnification", "-0.5");
            HBox numExtraRaysHBox = createParameterHBox("Number of Extra Rays", "0");

            objectDistanceField = (TextField) objectDistanceHBox.getChildren().get(1);
            objectHeightField = (TextField) objectHeightHBox.getChildren().get(1);
            focalLengthField = (TextField) focalLengthHBox.getChildren().get(1);
            magnificationField = (TextField) magnificationHBox.getChildren().get(1);
            numRaysField = (TextField) numExtraRaysHBox.getChildren().get(1);

            // === Ray Length Slider ===
            rayLengthLabel = new Text("Ray Length:");
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
            applyButton = new Button("Apply");
            applyButton.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 40;");
            HBox buttonHBox = new HBox(applyButton);
            buttonHBox.setAlignment(Pos.CENTER);
            VBox.setMargin(buttonHBox, new Insets(20, 0, 0, 0));

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
                    rayLengthBox
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
            paramVBoxContainer.setPrefWidth(420);

            animpane.setMinWidth(800);
            animpane.setPrefWidth(1500);
            animpane.setMaxWidth(Double.MAX_VALUE);

            splitPane.getItems().addAll(paramVBoxContainer, animpane);
            splitPane.setDividerPositions(0.22); // 22% param pane width

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
        drawFocalPoints(adjustedCenterX, adjustedCenterY, scaledFL, animPane);

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
        } else {
            System.out.println("Skipping drawing image arrow because out of bounds");
        }


// Step 3: Draw extra lenses and rays step by step
        drawMultiLensSystem(imageX, imageTopY, adjustedCenterY, extraLenses, animPane);

// Step 4: Draw the lens lines
        drawExtraLensLines(extraLenses,animPane);
        for (LensesModel.Lens lens : extraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double f2 = lens.getFocalLength() * scale;
            drawFocalPoints(lensX, adjustedCenterY, f2, animPane);
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

        lens.setStrokeWidth(3);
        pane.getChildren().add(lens);
    }

    private void drawFocalPoints(double lensX, double centerY, double focalLengthPx, Pane pane) {
        // Near focal point (F)
        Circle nearFocal = new Circle(lensX - focalLengthPx, centerY, 5);
        nearFocal.setFill(Color.DARKBLUE);

        // Far focal point (F')
        Circle farFocal = new Circle(lensX + focalLengthPx, centerY, 5);
        farFocal.setFill(Color.DARKBLUE);

        // Labels
        Text nearLabel = new Text(lensX - focalLengthPx - 20, centerY - 10, "F");
        Text farLabel = new Text(lensX + focalLengthPx + 10, centerY - 10, "F'");
        nearLabel.getStyleClass().add("lensview-ray-length-label");
        farLabel.getStyleClass().add("lensview-ray-length-label");

        pane.getChildren().addAll(nearFocal, farFocal, nearLabel, farLabel);
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
        Text label = new Text(x - 30, baseY - heightPx/2, "Object");
        label.getStyleClass().add("lensview-ray-length-label");

        pane.getChildren().addAll(objLine, arrowHead, label);
    }

    private void drawAllPrincipalRays(double objX, double objTopY,
                                      double lensX, double lensY,
                                      double focalLengthPx, Pane pane) {

        double u = lensX - objX;
        double f = focalLengthPx;
        boolean isConverging = f > 0;

        double imageDistance = 1 / ((1 / f) - (1 / u));
        double imageX = lensX + imageDistance;
        double magnification = -imageDistance / u;
        double imageHeight = magnification * (lensY - objTopY);
        double imageY = lensY - imageHeight;

        double extension = getRayExtensionBeyondImage(150);  // base * slider % (0 to 1)
        boolean isVirtual = (isConverging && u < f) || (!isConverging);

        // === RED RAY: Parallel to axis → refracted through/away from focal point ===
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted;
        ray1.setStroke(Color.RED);

        if (isVirtual) {
            ray1Refracted = new Line(lensX, objTopY, lensX + extension, objTopY);
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

    private void drawImageArrow(double x, double centerY, double heightPx, Pane pane) {
        if (Double.isNaN(x) || Double.isNaN(centerY) || Math.abs(x) > 5000) {
            System.out.println("Skipping real image arrow (out of bounds)");
            return;
        }

        double topY = centerY - heightPx;  // Correct vertical position

        Line imageLine = new Line(x, centerY, x, topY);
        imageLine.setStrokeWidth(2);
        imageLine.setStroke(Color.BLACK);

        Polygon arrowHead = new Polygon(
                x, topY,
                x - 5, topY + 10,
                x + 5, topY + 10
        );
        arrowHead.setFill(Color.BLACK);

        pane.getChildren().addAll(imageLine, arrowHead);
    }



    private void drawVirtualImageArrow(double x, double baseY, double heightPx, Pane pane) {
        if (Double.isNaN(x) || Double.isNaN(baseY) || Math.abs(x) > 5000 || Math.abs(heightPx) > 5000) {
            System.out.println("Skipping virtual image arrow (out of bounds)");
            return;
        }

        Line imageLine = new Line(x, baseY, x, baseY - heightPx);
        imageLine.setStroke(Color.PURPLE); // <-- Make it purple
        imageLine.setStrokeWidth(2);
        imageLine.getStrokeDashArray().addAll(5d, 5d);

        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );
        arrowHead.setFill(Color.PURPLE); // <-- Make arrow head purple too
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
        double extension = getRayExtensionBeyondImage(150);

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
    private void drawMultiLensSystem(double currObjX, double currObjTopY, double currBaseY,
                                     List<LensesModel.Lens> lenses, Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;
        double currObjHeight = currBaseY - currObjTopY;

        int lensIndex = 0;
        for (LensesModel.Lens lens : lenses) {
            double lensX = adjustedCenterX + (lens.getPosition() * scale);
            double f = lens.getFocalLength() * scale;
            double u = lensX - currObjX;

            if (u == 0) continue;

            double v = 1 / ((1 / f) - (1 / u));
            double imageX = lensX + v;
            double magnification = -v / u;
            double imageHeight = currObjHeight * magnification;
            double imageTopY = adjustedCenterY - imageHeight;

            if (lensIndex < multiLensColors.size()) {
                Color[] colors = multiLensColors.get(lensIndex);
                draw3RaysBetween(currObjX, currObjTopY, lensX, f, imageX, imageTopY, adjustedCenterY, pane,
                        colors[0], colors[1], colors[2]);
            } else {
                // fallback to black if color missing
                draw3RaysBetween(currObjX, currObjTopY, lensX, f, imageX, imageTopY, adjustedCenterY, pane,
                        Color.BLACK, Color.BLACK, Color.BLACK);
            }

            if (Math.abs(imageX) < 5000 && Math.abs(imageHeight) < 5000) {
                if (f > 0 && u > f) {
                    drawImageArrow(imageX, adjustedCenterY, imageHeight, pane);
                } else {
                    drawVirtualImageArrow(imageX, adjustedCenterY, imageHeight, pane);
                }
            }

            lensIndex++;
            currObjX = imageX;
            currObjTopY = imageTopY;
            currObjHeight = imageHeight;
        }
    }

    private void draw3RaysBetween(double objX, double objTopY, double lensX, double f,
                                  double imageX, double imageTopY, double centerY, Pane pane,
                                  Color color1, Color color2, Color color3) {

        double extension = getRayExtensionBeyondImage(150);
        boolean isConverging = f > 0;

        // Ray 1: Parallel -> refracts through image
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, imageX + extension, imageTopY + (imageTopY - objTopY) * extension / (imageX - lensX));
        ray1.setStroke(color1);
        ray1Refracted.setStroke(color1);

        // Ray 2: Through center (or virtual backtrace if diverging)
        Line ray2 = new Line(objX, objTopY, imageX + extension, imageTopY + (imageTopY - centerY) * extension / (imageX - lensX));
        ray2.setStroke(color2);
        if (!isConverging) {
            ray2.getStrokeDashArray().addAll(5d, 5d); // Dashed for diverging
        }

        // Ray 3: Toward near focal point -> emerges parallel
        double nearFocalX = lensX - f;
        double intersectY = centerY + (objTopY - centerY) * f / (objX - nearFocalX);
        Line ray3 = new Line(objX, objTopY, lensX, intersectY);
        Line ray3Refracted = new Line(lensX, intersectY, imageX + extension, imageTopY + (imageTopY - intersectY) * extension / (imageX - lensX));
        ray3.setStroke(color3);
        ray3Refracted.setStroke(color3);

        if (!isConverging) {
            ray3Refracted.getStrokeDashArray().addAll(5d, 5d); // Dashed if diverging lens
        }

        pane.getChildren().addAll(ray1, ray1Refracted, ray2, ray3, ray3Refracted);
    }


    private void drawExtraLensLines(List<LensesModel.Lens> extraLenses, Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        double zoomFactor = scale / DEFAULT_SCALE;
        double halfLensHeight = 100 * zoomFactor; // 100 is your original extra lens half height

        for (LensesModel.Lens lens : extraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;

            Line lensLine = new Line(
                    lensX,
                    adjustedCenterY - halfLensHeight,
                    lensX,
                    adjustedCenterY + halfLensHeight
            );
            lensLine.setStroke(lens.isConverging() ? Color.BLUE : Color.RED);
            lensLine.setStrokeWidth(3);
            if (!lens.isConverging()) {
                lensLine.getStrokeDashArray().addAll(5d, 5d);
            }

            Text label = new Text(
                    lensX - 20, adjustedCenterY - halfLensHeight - 10,
                    lens.isConverging() ? "Converging" : "Diverging"

            );
            label.getStyleClass().add("lensview-ray-length-label");

            label.setFill(lens.isConverging() ? Color.BLUE : Color.RED);

            pane.getChildren().addAll(lensLine, label);
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


            String labelText = (focalLength > 0 ? "Converging" : "Diverging") + " Lens #" + lensCounter++;

            Label lensLabel = new Label(labelText);
            lensLabel.getStyleClass().add("lensview-ray-length-label");

            HBox lensPositionBox = createParameterHBox("Position", String.valueOf(position));
            HBox lensFocalLengthBox = createParameterHBox("Focal Length", String.valueOf(focalLength));

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
                                if (label.getText().contains("Lens #")) {
                                    boolean isConverging = label.getText().contains("Converging");
                                    label.setText((isConverging ? "Converging" : "Diverging") + " Lens #" + lensCounter++);
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
        stopAnimation();

        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        Pane animPane = getAnimpane();
        animPane.getChildren().removeIf(node ->
                !(node instanceof HBox)); // Keep zoom controls only

        animatedRays.clear();
        animationTimeline = new Timeline();
        keyframeTimes.clear();
        currentKeyframeIndex = 0;

        drawOpticalAxis(animPane);
        drawMainLens(animPane);
        drawFocalPoints(adjustedCenterX, adjustedCenterY, lastFocalLength * scale, animPane);
        drawExtraLensLines(lastExtraLenses, animPane);

        double objX = adjustedCenterX - lastObjectDistance * scale;
        double objTopY = adjustedCenterY - lastObjectHeight * scale;
        drawObject(objX, adjustedCenterY, lastObjectHeight * scale, animPane);

        double offset = 0.0;

        // === Main lens stage
        animateLensStage(objX, objTopY, adjustedCenterX, adjustedCenterY, lastFocalLength * scale, lastNumRays, offset);

        double u = adjustedCenterX - objX;
        double f = lastFocalLength * scale;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = adjustedCenterX + v;
        double imageY = adjustedCenterY - (v / u) * (adjustedCenterY - objTopY);

        double currObjX = imageX;
        double currObjTopY = imageY;

        offset += 2.5; // Wait 2.5 seconds before next lens starts animating

        // === Extra lenses
        for (LensesModel.Lens lens : lastExtraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;
            double f2 = lens.getFocalLength() * scale;
            double u2 = lensX - currObjX;
            if (u2 == 0) continue;

            double v2 = 1 / ((1 / f2) - (1 / u2));
            double nextX = lensX + v2;
            double nextY = adjustedCenterY - (v2 / u2) * (adjustedCenterY - currObjTopY);

            animateLensStage(currObjX, currObjTopY, lensX, adjustedCenterY, f2, lastNumRays, offset);

            currObjX = nextX;
            currObjTopY = nextY;
            offset += 2.5;
        }
    }

    public void stopAnimation() {
        if (animationTimeline != null) {
            animationTimeline.stop();
            animationTimeline = null;
        }
    }

    public void pauseAnimation() {
        if (animationTimeline != null) animationTimeline.pause();
    }

    public void restartAnimation() {
        stopAnimation();
        startAnimation();
    }


    public void stepForwardAnimation() {
        if (animationTimeline != null && !keyframeTimes.isEmpty()) {
            currentKeyframeIndex = Math.min(currentKeyframeIndex + 1, keyframeTimes.size() - 1);
            animationTimeline.jumpTo(keyframeTimes.get(currentKeyframeIndex));
            System.out.println("Jumped to frame: " + currentKeyframeIndex);
        }
    }

    public void stepBackAnimation() {
        if (animationTimeline != null && !keyframeTimes.isEmpty()) {
            currentKeyframeIndex = Math.max(currentKeyframeIndex - 1, 0);
            animationTimeline.jumpTo(keyframeTimes.get(currentKeyframeIndex));
            System.out.println("Jumped to frame: " + currentKeyframeIndex);
        }
    }

    public void toggleLoopMode() {
        loopEnabled = !loopEnabled;
        System.out.println("Loop mode is now " + (loopEnabled ? "ON" : "OFF"));
    }

    private void animateLensStage(double objX, double objTopY, double lensX, double lensY,
                                  double focalLengthPx, int numExtraRays, double offsetSeconds) {
        double u = lensX - objX;
        if (u == 0) return;

        double f = focalLengthPx;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = lensX + v;
        double magnification = -v / u;
        double imageHeight = magnification * (lensY - objTopY);
        double imageY = lensY - imageHeight;
        boolean isConverging = f > 0;
        boolean isVirtual = (isConverging && u < f) || (!isConverging);

        double rayExtension = getRayLengthFactor() * 150;
        Pane animPane = getAnimpane();

        double nearFocalX = lensX - f;
        double targetY = lensY + (objTopY - lensY) * f / (objX - nearFocalX);

        Line ray1 = new Line(objX, objTopY, objX, objTopY); // Red
        Line ray2 = new Line(objX, objTopY, objX, objTopY); // Blue
        Line ray3 = new Line(objX, objTopY, objX, objTopY); // Green

        ray1.setStroke(Color.RED);
        ray2.setStroke(Color.BLUE);
        ray3.setStroke(Color.GREEN);
        animPane.getChildren().addAll(ray1, ray2, ray3);

        double speed = 1.0;
        Timeline phase1 = new Timeline();
        Timeline phase2 = new Timeline();

        phase1.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray1.endXProperty(), lensX)),
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray2.endXProperty(), lensX),
                        new KeyValue(ray2.endYProperty(), lensY)),
                new KeyFrame(Duration.seconds(offsetSeconds + speed),
                        new KeyValue(ray3.endXProperty(), lensX),
                        new KeyValue(ray3.endYProperty(), targetY))
        );

        phase1.setOnFinished(e -> {
            Line ray1Refracted = new Line(lensX, objTopY, lensX, objTopY);
            Line ray2Refracted = new Line(lensX, lensY, lensX, lensY);
            Line ray3Refracted = new Line(lensX, targetY, lensX, targetY);

            ray1Refracted.setStroke(Color.RED);
            ray2Refracted.setStroke(Color.BLUE);
            ray3Refracted.setStroke(Color.GREEN);
            animPane.getChildren().addAll(ray1Refracted, ray2Refracted, ray3Refracted);

            if (isVirtual) {
                animateBacktrace(ray1Refracted, lensX, objTopY, imageX, imageY, Color.RED, animPane, phase2, speed + 0.2);
                animateBacktrace(ray2Refracted, lensX, lensY, imageX, imageY, Color.BLUE, animPane, phase2, speed + 0.4);
                animateBacktrace(ray3Refracted, lensX, targetY, imageX, imageY, Color.GREEN, animPane, phase2, speed + 0.6);
            } else {
                double slope1 = (imageY - objTopY) / (imageX - lensX);
                double slope2 = (imageY - lensY) / (imageX - lensX);
                double slope3 = (imageY - targetY) / (imageX - lensX);

                phase2.getKeyFrames().addAll(
                        new KeyFrame(Duration.seconds(speed + 0.2),
                                new KeyValue(ray1Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray1Refracted.endYProperty(), imageY + slope1 * rayExtension)),
                        new KeyFrame(Duration.seconds(speed + 0.4),
                                new KeyValue(ray2Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray2Refracted.endYProperty(), imageY + slope2 * rayExtension)),
                        new KeyFrame(Duration.seconds(speed + 0.6),
                                new KeyValue(ray3Refracted.endXProperty(), imageX + rayExtension),
                                new KeyValue(ray3Refracted.endYProperty(), imageY + slope3 * rayExtension))
                );
            }

            phase2.play();
        });

        // Image Arrow
        Line imageArrow = new Line(imageX, lensY, imageX, imageY);
        imageArrow.setStroke(Color.BLACK);
        imageArrow.setStrokeWidth(2);
        Polygon arrowHead = new Polygon(imageX, imageY, imageX - 5, imageY + 10, imageX + 5, imageY + 10);
        arrowHead.setFill(Color.BLACK);
        animPane.getChildren().addAll(imageArrow, arrowHead);

        phase1.play();
    }

    private void animateBacktrace(Line baseRay, double startX, double startY, double imageX, double imageY,
                                  Color color, Pane animPane, Timeline timeline, double delay) {
        double dx = imageX - startX;
        double dy = imageY - startY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1e-4) return;

        double unitX = dx / dist;
        double unitY = dy / dist;
        double extension = getRayLengthFactor() * 150;

        Line backtrace = new Line(startX, startY, startX, startY);
        backtrace.setStroke(color);
        backtrace.setStrokeWidth(1.5);
        backtrace.getStrokeDashArray().setAll(6.0, 6.0);
        animPane.getChildren().add(backtrace);

        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(delay),
                        new KeyValue(backtrace.endXProperty(), imageX),
                        new KeyValue(backtrace.endYProperty(), imageY))
        );
    }

    //Test cases
    public void runRayIntersectionTest() {
        System.out.println("Running ray-image intersection tests...");

        int passed = 0;
        int failed = 0;

        passed += runTest("Converging, real image",
                8.0, 2.0, 10.0, 4.0,
                new Point2D(6.0, 4.0));

        passed += runTest("Converging, virtual image",
                9.5, 2.0, 10.0, 4.0,
                new Point2D(20.57, -11.43));

        passed += runTest("Diverging lens",
                8.0, 2.0, 10.0, -4.0,
                new Point2D(11.33, -0.67));

        // CASE 4: Object at focal point (v = ∞)
        Point2D infResult = computeImagePosition(6.0, 2.0, 10.0, 4.0);  // u = f
        if (infResult == null || Double.isInfinite(infResult.getX()) || Double.isNaN(infResult.getX())) {
            System.out.println("Focal point test passed (infinite or null image position)");
            passed++;
        } else {
            System.out.println("Focal point test failed: expected image at infinity/null but got " + infResult);
            failed++;
        }

        System.out.println("good " + passed + " passed");
        System.out.println("bad  " + failed + " failed");
    }


    private int runTest(String label, double objX, double objY, double lensX, double focalLength, Point2D expected) {
        Point2D actual = computeImagePosition(objX, objY, lensX, focalLength);

        if (actual == null || expected == null) {
            if (actual == expected) {
                System.out.println(label + " passed (null as expected)");
                return 1;
            } else {
                System.out.println(label + " failed (expected: " + expected + ", got: " + actual + ")");
                return 0;
            }
        }

        if (isClose(actual, expected, 0.05)) {
            System.out.println(label + " passed");
            return 1;
        } else {
            System.out.println(label + " failed");
            System.out.println("Expected: " + expected);
            System.out.println("Actual  : " + actual);
            return 0;
        }
    }


    private Point2D computeImagePosition(double objX, double objY, double lensX, double focalLength) {
        double u = objX - lensX;
        if (u == 0) return null;

        double v = 1 / ((1 / focalLength) + (1 / u));
        double imageX = lensX + v;
        double magnification = -v / u;
        double imageY = objY * magnification;

        return new Point2D(imageX, imageY);
    }

    private boolean isClose(Point2D a, Point2D b, double tolerance) {
        return Math.abs(a.getX() - b.getX()) < tolerance && Math.abs(a.getY() - b.getY()) < tolerance;
    }

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



    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Parameter Values");
        alert.setContentText(message);
        alert.showAndWait();
    }
}