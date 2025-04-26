package project.optics.jfkt.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
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

    public LensView() {
        super("Lenses");
        initializeView();
        setupZoomControls();
        runRayIntersectionTest();


        this.controller = new LensesController(new LensesModel(3, 8.0, 2.0, -0.5, 4.0), this);
        showDefaultLensSystem();
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
            Text header = new Text("Parameters:");
            header.setFont(new Font(40));
            header.setTextAlignment(TextAlignment.CENTER);
            header.setUnderline(true);
            VBox.setMargin(header, new Insets(0, 0, 20, 0));

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
            Text rayLengthLabel = new Text("Ray Length:");
            rayLengthLabel.setFont(new Font(20));
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

            // === Rebuild the actual scrollable VBox ===
            VBox scrollContent = new VBox(10);
            scrollContent.setPadding(new Insets(10));
            scrollContent.setPrefWidth(400);
            scrollContent.getChildren().addAll(
                    header,
                    objectDistanceHBox,
                    objectHeightHBox,
                    focalLengthHBox,
                    numExtraRaysHBox,
                    rayLengthBox
            );

            // Replace your paramVBox reference with the one inside the ScrollPane
            this.paramVBox = scrollContent;

            // === Wrap the scrollable part in a ScrollPane ===
            ScrollPane scrollPane = new ScrollPane(scrollContent);
            scrollPane.setPrefSize(420, 600); // Leave room for Apply button
            scrollPane.setFitToWidth(true);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setStyle("-fx-background-color: transparent;");

            // === Final VBox to contain scroll + Apply button ===
            VBox paramVBoxContainer = new VBox(scrollPane, buttonHBox);
            paramVBoxContainer.setPrefSize(420, 720);
            paramVBoxContainer.setPadding(new Insets(10));
            paramVBoxContainer.setSpacing(10);

            // === Replace old VBox from BaseView with the new scrollable container ===
            baseCenter.getChildren().removeIf(node -> node instanceof VBox);
            baseCenter.getChildren().add(paramVBoxContainer);
        }

        return baseCenter;
    }


    private HBox createParameterHBox(String labelText, String defaultValue) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 10, 5, 10));

        Text label = new Text(labelText);
        label.setFont(new Font(20));
        label.getStyleClass().add("parameter-label");

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
        Line lens = new Line(adjustedCenterX, adjustedCenterY - 150, adjustedCenterX, adjustedCenterY + 150);

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
        nearLabel.getStyleClass().add("focal-label");
        farLabel.getStyleClass().add("focal-label");

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
        label.getStyleClass().add("lens-label");

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
        double imageHeight = -(imageDistance / u) * (lensY - objTopY);
        double imageY = lensY - imageHeight;

        double extension = getRayExtensionBeyondImage(150); // control how far ray extends past image

        // === Ray 1: Parallel to axis → refracted through or away from focal point
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted;

        if (isConverging) {
            double dx = imageX - lensX;
            double dy = imageY - objTopY;
            double slope = dy / dx;

            ray1Refracted = new Line(lensX, objTopY,
                    imageX + extension, imageY + slope * extension);

        } else {
            double virtualFocalX = lensX - Math.abs(f);
            double slope = (objTopY - lensY) / (lensX - virtualFocalX);

            ray1Refracted = new Line(lensX, objTopY,
                    lensX + extension, objTopY + slope * extension);

            // Create virtual backtrace safely
            double dx = lensX - imageX;
            double dy = objTopY - imageY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 1e-5 && dist < 5000) {  // only if the virtual distance is reasonable
                double unitX = dx / dist;
                double unitY = dy / dist;

                double backExtension = Math.min(extension, 200); // limit back extension
                Line virtualBack = new Line(
                        lensX, objTopY,
                        lensX - unitX * backExtension, objTopY - unitY * backExtension
                );
                virtualBack.setStroke(Color.ORANGE); // Make it lighter to distinguish
                virtualBack.getStrokeDashArray().addAll(5d, 5d);
                pane.getChildren().add(virtualBack);
            }
        }

        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);
        pane.getChildren().addAll(ray1, ray1Refracted);

        // === Ray 2: Through center
        Line ray2 = new Line(objX, objTopY, imageX + extension,
                imageY + (imageY - lensY != 0 ? (imageY - lensY) * extension / (imageX - lensX) : 0));
        ray2.setStroke(Color.BLUE);
        pane.getChildren().add(ray2);

        // === Ray 3: Toward focal point → refracted parallel
        Line ray3;
        Line ray3Refracted;

        if (isConverging) {
            double nearFocalX = lensX - f;
            double targetY = lensY + (objTopY - lensY) * f / (objX - nearFocalX);

            ray3 = new Line(objX, objTopY, lensX, targetY);

            double dx = imageX - lensX;
            double dy = imageY - targetY;
            double slope = dy / dx;

            ray3Refracted = new Line(lensX, targetY,
                    imageX + extension, imageY + slope * extension);

        } else {
            double farFocalX = lensX + Math.abs(f);
            double slope = (lensY - objTopY) / (farFocalX - objX);
            double intersectY = objTopY + slope * (lensX - objX);

            ray3 = new Line(objX, objTopY, lensX, intersectY);
            ray3Refracted = new Line(lensX, intersectY,
                    lensX + extension, intersectY);

            // Optional: you could apply a similar fix here if needed
        }

        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);
        pane.getChildren().addAll(ray3, ray3Refracted);
    }


    private void drawImageArrow(double x, double baseY, double heightPx, Pane pane) {
        if (Double.isNaN(x) || Double.isNaN(baseY) || Math.abs(x) > 5000) {
            System.out.println("Skipping real image arrow (out of bounds)");
            return;
        }

        Line imageLine = new Line(x, baseY, x, baseY - heightPx);
        imageLine.setStrokeWidth(2);

        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );

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

        double extension = getRayExtensionBeyondImage(150); // <-- get based on Ray Length Slider!

        // Ray 1: Parallel -> refracts through image
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, imageX + extension, imageTopY + (imageTopY - objTopY) * extension / (imageX - lensX));
        ray1.setStroke(color1);
        ray1Refracted.setStroke(color1);

        // Ray 2: Through center
        Line ray2 = new Line(objX, objTopY, imageX + extension, imageTopY + (imageTopY - centerY) * extension / (imageX - lensX));
        ray2.setStroke(color2);

        // Ray 3: Aimed at near focal point -> emerges parallel
        double nearFocalX = lensX - f;
        double intersectY = centerY + (objTopY - centerY) * f / (objX - nearFocalX);
        Line ray3 = new Line(objX, objTopY, lensX, intersectY);
        Line ray3Refracted = new Line(lensX, intersectY, imageX + extension, imageTopY + (imageTopY - intersectY) * extension / (imageX - lensX));
        ray3.setStroke(color3);
        ray3Refracted.setStroke(color3);

        pane.getChildren().addAll(ray1, ray1Refracted, ray2, ray3, ray3Refracted);
    }



    private void drawExtraLensLines(List<LensesModel.Lens> extraLenses, Pane pane) {
        double adjustedCenterX = centerX + offsetX;
        double adjustedCenterY = centerY + offsetY;

        for (LensesModel.Lens lens : extraLenses) {
            double lensX = adjustedCenterX + lens.getPosition() * scale;

            Line lensLine = new Line(lensX, adjustedCenterY - 100, lensX, adjustedCenterY + 100);
            lensLine.setStroke(lens.isConverging() ? Color.BLUE : Color.RED);
            lensLine.setStrokeWidth(3);
            if (!lens.isConverging()) {
                lensLine.getStrokeDashArray().addAll(5d, 5d);
            }

            Text label = new Text(lensX - 20, adjustedCenterY - 110,
                    lens.isConverging() ? "Converging" : "Diverging");
            label.setFont(new Font(14));
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
            lensGroup.setStyle("-fx-border-color: grey; -fx-border-width: 1; -fx-background-color: #f5f5f5;");

            String labelText = (focalLength > 0 ? "Converging" : "Diverging") + " Lens #" + lensCounter++;

            Label lensLabel = new Label(labelText);
            lensLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

            HBox lensPositionBox = createParameterHBox("Position", String.valueOf(position));
            HBox lensFocalLengthBox = createParameterHBox("Focal Length", String.valueOf(focalLength));

            TextField positionField = (TextField) lensPositionBox.getChildren().get(1);
            TextField focalField = (TextField) lensFocalLengthBox.getChildren().get(1);
            extraLensFields.add(new TextField[]{positionField, focalField});

            // "X" remove button
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
        animatedRays.clear();
        animationTimeline = new Timeline();
        keyframeTimes.clear();
        currentKeyframeIndex = 0;

        animPane.getChildren().removeIf(node ->
                node instanceof Line || node instanceof Polygon || node instanceof Text || node instanceof Circle);

        drawOpticalAxis(animPane);
        drawMainLens(animPane);
        drawFocalPoints(adjustedCenterX, adjustedCenterY, lastFocalLength * scale, animPane);
        drawExtraLensLines(lastExtraLenses, animPane);

        double objX = adjustedCenterX - lastObjectDistance * scale;
        double objTopY = adjustedCenterY - lastObjectHeight * scale;
        drawObject(objX, adjustedCenterY, lastObjectHeight * scale, animPane);

        double offset = 0;
        animateLensStage(objX, objTopY, adjustedCenterX, adjustedCenterY, lastFocalLength * scale, lastNumRays, offset);

        double u = adjustedCenterX - objX;
        double f = lastFocalLength * scale;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = adjustedCenterX + v;
        double imageY = adjustedCenterY - (v / u) * (adjustedCenterY - objTopY);
        double currObjX = imageX;
        double currObjTopY = imageY;

        offset += RAY_STAGE_DURATION;

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
            offset += RAY_STAGE_DURATION;
        }

        animationTimeline.play();
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
        double imageHeight = -v / u * (lensY - objTopY);
        double imageY = lensY - imageHeight;
        double rayExtension = getRayLengthFactor() * 150;
        boolean isConverging = f > 0;

        Pane animPane = getAnimpane();

        // === Ray 1: Parallel to axis
        Line ray1 = new Line(objX, objTopY, objX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, lensX, objTopY);
        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);

        if (!isConverging) {
            // Virtual backtrace for diverging lens
            double dx = lensX - imageX;
            double dy = objTopY - imageY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double unitX = dx / dist;
            double unitY = dy / dist;

            if (Math.abs(unitX) > 1e-6) {  // Only add if unitX isn't almost 0
                Line virtualRay = new Line(
                        lensX, objTopY,
                        imageX - unitX * rayExtension, imageY - unitY * rayExtension
                );
                virtualRay.setStroke(Color.RED);
                virtualRay.getStrokeDashArray().addAll(5d, 5d);
                animPane.getChildren().add(virtualRay);
            }

        }

        // === Ray 2: Through center
        Line ray2 = new Line(objX, objTopY, objX, objTopY);
        ray2.setStroke(Color.BLUE);

        // === Ray 3: Toward focal point (diverging) or through focal (converging)
        Line ray3 = new Line(objX, objTopY, objX, objTopY);
        Line ray3Refracted = new Line(lensX, objTopY, lensX, objTopY);
        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);

        if (!isConverging) {
            double dx = lensX - imageX;
            double dy = objTopY - imageY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double unitX = dx / dist;
            double unitY = dy / dist;

            double virtualIntersectY = objTopY + (lensX - objX) * (unitY / unitX);
            Line virtualF = new Line(objX, objTopY, lensX, virtualIntersectY);
            virtualF.setStroke(Color.GREEN);

            Line virtualRay = new Line(
                    lensX, virtualIntersectY,
                    imageX - unitX * rayExtension, imageY - unitY * rayExtension
            );
            virtualRay.setStroke(Color.GREEN);
            virtualRay.getStrokeDashArray().addAll(5d, 5d);

            animPane.getChildren().addAll(virtualF, virtualRay);
        }

        // === Add rays to pane
        animPane.getChildren().addAll(ray1, ray1Refracted, ray2, ray3, ray3Refracted);

        // === Animation KeyFrames ===

        // Ray 1
        Duration t1 = Duration.seconds(offsetSeconds + 0.3);
        animationTimeline.getKeyFrames().add(new KeyFrame(t1,
                new KeyValue(ray1.endXProperty(), lensX)));
        keyframeTimes.add(t1);

        Duration t2 = Duration.seconds(offsetSeconds + 0.6);
        animationTimeline.getKeyFrames().add(new KeyFrame(t2,
                new KeyValue(ray1Refracted.endXProperty(), imageX),
                new KeyValue(ray1Refracted.endYProperty(), imageY)));
        keyframeTimes.add(t2);

        // Ray 2
        Duration t3 = Duration.seconds(offsetSeconds + 0.8);
        animationTimeline.getKeyFrames().add(new KeyFrame(t3,
                new KeyValue(ray2.endXProperty(), imageX),
                new KeyValue(ray2.endYProperty(), imageY)));
        keyframeTimes.add(t3);

        // Ray 3
        double nearFocalX = lensX - f;
        double targetY = isConverging
                ? lensY + (objTopY - lensY) * f / (objX - nearFocalX)
                : lensY;

        Duration t4 = Duration.seconds(offsetSeconds + 0.5);
        animationTimeline.getKeyFrames().add(new KeyFrame(t4,
                new KeyValue(ray3.endXProperty(), lensX),
                new KeyValue(ray3.endYProperty(), targetY)));
        keyframeTimes.add(t4);

        Duration t5 = Duration.seconds(offsetSeconds + 0.9);
        animationTimeline.getKeyFrames().add(new KeyFrame(t5,
                new KeyValue(ray3Refracted.endXProperty(), imageX),
                new KeyValue(ray3Refracted.endYProperty(), imageY)));
        keyframeTimes.add(t5);

        // === Image Arrow Animation ===
        Line arrow = new Line(imageX, lensY, imageX, lensY);
        arrow.setStroke(Color.PURPLE);
        arrow.setStrokeWidth(2);
        animPane.getChildren().add(arrow);

        Duration t6 = Duration.seconds(offsetSeconds + 1.2);
        animationTimeline.getKeyFrames().add(new KeyFrame(t6,
                new KeyValue(arrow.endYProperty(), imageY)));
        keyframeTimes.add(t6);
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
        double u = lensX - objX;
        if (u == 0) return null;

        double v = 1 / ((1 / focalLength) - (1 / u));
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