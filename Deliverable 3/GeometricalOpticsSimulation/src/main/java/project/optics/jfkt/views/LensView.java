package project.optics.jfkt.views;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
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

public class LensView extends BaseView {
    private LensesController controller;
    private VBox paramVBox;
    private static final double DEFAULT_SCALE = 50.0;
    private double scale = DEFAULT_SCALE;
    private double centerY;
    private double centerX;
    private int lensCounter = 1;
    private Slider rayLengthSlider;

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

        this.controller = new LensesController(new LensesModel(3, 8.0, 2.0, -0.5, 4.0), this);
        showDefaultLensSystem();
    }

    private void initializeView() {
        Pane animPane = getAnimpane();
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
        drawFocalPoints(centerX, centerY, scaledFL, animPane);

        // Step 1: Draw initial object and rays to main lens
        double objX = centerX - (objectDistance * scale);
        double objTopY = centerY - (objectHeight * scale);

        drawObject(objX, centerY, objectHeight * scale, animPane);
// Always draw the 3 principal rays
        drawAllPrincipalRays(objX, centerY - scaledObjHeight, centerX, centerY, scaledFL, animPane);

// Draw extra rays if requested
        if (numRays != 0) {
            drawAdditionalRays(objX, centerY - scaledObjHeight, centerX, centerY, scaledFL, numRays, animPane);
        }

// Step 2: Compute image from main lens
        double u = centerX - objX;
        double f = focalLength * scale;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = centerX + v;
        magnification = -v / u;
        double imageHeight = objectHeight * magnification * scale;
        double imageTopY = centerY - imageHeight;

// Optional: draw final image for main lens
        drawVirtualImageArrow(imageX, centerY, imageHeight, animPane);

// Step 3: Draw extra lenses and rays step by step
        drawMultiLensSystem(imageX, imageTopY, centerY, extraLenses, animPane);

// Step 4: Draw the lens lines
        drawExtraLensLines(extraLenses, animPane);
        if (numRays > 3) {
            drawAdditionalRays(objX, centerY - scaledObjHeight, centerX, centerY, scaledFL, numRays - 3, animPane);
        }

        setupZoomControls(); // Re-add zoom controls
    }

    private void drawOpticalAxis(Pane pane) {
        Line axis = new Line(0, centerY, pane.getPrefWidth(), centerY);

        pane.getChildren().add(axis);
    }

    private void drawMainLens(Pane pane) {
        Line lens = new Line(centerX, centerY - 150, centerX, centerY + 150);

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
        double factor = getRayLengthFactor();
        System.out.println("Ray length factor: " + factor);

        double rayExtension = 150 * factor;

        boolean isConverging = f > 0;

        // === Calculate image position ===
        double imageDistance = 1 / ((1 / f) - (1 / u));
        double imageX = lensX + imageDistance;
        double imageHeight = -(imageDistance / u) * (lensY - objTopY);
        double imageY = lensY - imageHeight;

        // === Ray 1: Parallel then refracted ===
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted = new Line();

        if (isConverging) {
            // Refracts through image
            ray1Refracted.setStartX(lensX);
            ray1Refracted.setStartY(objTopY);
            ray1Refracted.setEndX(imageX);
            ray1Refracted.setEndY(imageY);
        } else {
            // Diverging: refracts away from virtual focal point
            double virtualFocalX = lensX - Math.abs(f);
            double slope = (objTopY - lensY) / (lensX - virtualFocalX);

            ray1Refracted.setStartX(lensX);
            ray1Refracted.setStartY(objTopY);
            ray1Refracted.setEndX(lensX + rayExtension);
            ray1Refracted.setEndY(objTopY + slope * rayExtension);

            // Dashed virtual ray back
            Line virtualBack = new Line(lensX, objTopY, lensX - rayExtension, objTopY - slope * rayExtension);
            virtualBack.setStroke(Color.RED);
            virtualBack.getStrokeDashArray().addAll(5d, 5d);
            pane.getChildren().add(virtualBack);
        }

        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);
        pane.getChildren().addAll(ray1, ray1Refracted);

        // === Ray 2: Through center of lens ===
        Line ray2 = new Line(objX, objTopY, imageX, imageY);
        ray2.setStroke(Color.BLUE);
        pane.getChildren().add(ray2);

        // === Ray 3: Aimed at F (or toward virtual F), then refracted ===
        Line ray3 = new Line();
        Line ray3Refracted = new Line();

        if (isConverging) {
            double nearFocalX = lensX - f;
            double targetY = lensY + (objTopY - lensY) * f / (objX - nearFocalX);

            ray3.setStartX(objX);
            ray3.setStartY(objTopY);
            ray3.setEndX(lensX);
            ray3.setEndY(targetY);

            ray3Refracted.setStartX(lensX);
            ray3Refracted.setStartY(targetY);
            ray3Refracted.setEndX(imageX);
            ray3Refracted.setEndY(imageY);
        } else {
            // Diverging: aimed toward far F', emerges parallel
            double farFocalX = lensX + Math.abs(f);
            double slope = (lensY - objTopY) / (farFocalX - objX);
            double intersectY = objTopY + slope * (lensX - objX);

            ray3.setStartX(objX);
            ray3.setStartY(objTopY);
            ray3.setEndX(lensX);
            ray3.setEndY(intersectY);

            ray3Refracted.setStartX(lensX);
            ray3Refracted.setStartY(intersectY);
            ray3Refracted.setEndX(lensX + rayExtension);
            ray3Refracted.setEndY(intersectY);

            // Virtual extension (dashed)
            Line virtualBack = new Line(lensX, intersectY, lensX - rayExtension, intersectY);
            virtualBack.setStroke(Color.GREEN);
            virtualBack.getStrokeDashArray().addAll(5d, 5d);
            pane.getChildren().add(virtualBack);
        }

        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);
        pane.getChildren().addAll(ray3, ray3Refracted);

        // === Image Arrow ===
        if (isConverging && u > f) {
            drawImageArrow(imageX, lensY, imageHeight, pane);
        } else {
            drawVirtualImageArrow(imageX, lensY, imageHeight, pane);
        }
    }




    private void drawImageArrow(double x, double baseY, double heightPx, Pane pane) {
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
        Line imageLine = new Line(x, baseY, x, baseY - heightPx);

        imageLine.setStrokeWidth(2);
        imageLine.getStrokeDashArray().addAll(5d, 5d);

        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );

        arrowHead.setOpacity(0.6);
        pane.getChildren().addAll(imageLine, arrowHead);
    }

    private void drawAdditionalRays(double objX, double objTopY,
                                    double lensX, double lensY,
                                    double focalLengthPx,
                                    int numExtraRays, Pane pane) {
        double imageDistance = 1 / (1/focalLengthPx - 1/(lensX-objX));
        double imageX = lensX + imageDistance;
        double imageHeight = -((lensX-objX)/imageDistance) * (lensY-objTopY);
        double imageY = lensY - imageHeight;

        double objectHeight = lensY - objTopY;
        double spacing = objectHeight / (numExtraRays + 1);

        for (int i = 1; i <= numExtraRays; i++) {
            double yOffset = spacing * i;
            double rayStartY = objTopY + yOffset;

            Line incidentRay = new Line(objX, rayStartY, lensX, rayStartY);
            Line refractedRay = new Line(lensX, rayStartY, imageX, imageY + yOffset*(imageHeight/objectHeight));

            Color rayColor = Color.ORANGE.deriveColor(0, 1, 1, 0.6);
            incidentRay.setStroke(rayColor);
            refractedRay.setStroke(rayColor);
            incidentRay.setStrokeWidth(1);
            refractedRay.setStrokeWidth(1);

            pane.getChildren().addAll(incidentRay, refractedRay);
        }
    }

    //Multi Lens System
    private void drawMultiLensSystem(double currObjX, double currObjTopY, double currBaseY,
                                     List<LensesModel.Lens> lenses, Pane pane) {

        double currObjHeight = currBaseY - currObjTopY;

        for (LensesModel.Lens lens : lenses) {
            double lensX = centerX + (lens.getPosition() * scale);
            double f = lens.getFocalLength() * scale;
            double u = lensX - currObjX;

            if (u == 0) continue;

            double v = 1 / ((1 / f) - (1 / u));
            double imageX = lensX + v;
            double magnification = -v / u;
            double imageHeight = currObjHeight * magnification;
            double imageTopY = centerY - imageHeight;

            draw3RaysBetween(currObjX, currObjTopY, lensX, f, imageX, imageTopY, pane);

            drawVirtualImageArrow(imageX, centerY, imageHeight, pane);

            // Update: this image becomes the object for the next lens
            currObjX = imageX;
            currObjTopY = imageTopY;
            currObjHeight = imageHeight;
        }
    }


    private void draw3RaysBetween(double objX, double objTopY, double lensX, double f,
                                  double imageX, double imageTopY, Pane pane) {
        // Ray 1: Parallel → refracts through image
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, imageX, imageTopY);
        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);

        // Ray 2: Through center
        Line ray2 = new Line(objX, objTopY, imageX, imageTopY);
        ray2.setStroke(Color.BLUE);

        // Ray 3: Aimed at near F → refracts parallel
        double nearFocalX = lensX - f;
        double intersectY = centerY + (objTopY - centerY) * f / (objX - nearFocalX);
        Line ray3 = new Line(objX, objTopY, lensX, intersectY);
        Line ray3Refracted = new Line(lensX, intersectY, imageX, imageTopY);
        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);

        pane.getChildren().addAll(ray1, ray1Refracted, ray2, ray3, ray3Refracted);
    }


    private void drawExtraLensLines(List<LensesModel.Lens> extraLenses, Pane pane) {
        for (LensesModel.Lens lens : extraLenses) {
            double lensX = centerX + lens.getPosition() * scale;

            Line lensLine = new Line(lensX, centerY - 100, lensX, centerY + 100);
            lensLine.setStroke(lens.isConverging() ? Color.BLUE : Color.RED);
            lensLine.setStrokeWidth(3);
            if (!lens.isConverging()) {
                lensLine.getStrokeDashArray().addAll(5d, 5d); // make diverging lenses dashed
            }

            Text label = new Text(lensX - 20, centerY - 110,
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
            });

            HBox header = new HBox(lensLabel, removeBtn);
            header.setAlignment(Pos.CENTER_LEFT);
            header.setSpacing(10);

            lensGroup.getChildren().addAll(header, lensPositionBox, lensFocalLengthBox);

            // Add before Apply button (last element)
            paramVBox.getChildren().add(paramVBox.getChildren().size() - 1, lensGroup);
        }
    }
    //Animation
    public void startAnimation() {
        stopAnimation();

        Pane animPane = getAnimpane();
        animatedRays.clear();
        animationTimeline = new Timeline();
        keyframeTimes.clear();
        currentKeyframeIndex = 0;

        // ❗ Step 1: Clear only lines, polygons, images
        animPane.getChildren().removeIf(node ->
                node instanceof Line || node instanceof Polygon || node instanceof Text || node instanceof Circle);

        // ❗ Step 2: Redraw all static visuals
        drawOpticalAxis(animPane);
        drawMainLens(animPane);
        drawFocalPoints(centerX, centerY, lastFocalLength * scale, animPane);
        drawExtraLensLines(lastExtraLenses, animPane);

        // Redraw object
        double objX = centerX - lastObjectDistance * scale;
        double objTopY = centerY - lastObjectHeight * scale;
        drawObject(objX, centerY, lastObjectHeight * scale, animPane);

        // ❗ Step 3: Animate first lens (main)
        double offset = 0;
        animateLensStage(objX, objTopY, centerX, centerY, lastFocalLength * scale, lastNumRays, offset);

        // ❗ Step 4: Animate remaining lenses from last image
        double u = centerX - objX;
        double f = lastFocalLength * scale;
        double v = 1 / ((1 / f) - (1 / u));
        double imageX = centerX + v;
        double imageY = centerY - (v / u) * (centerY - objTopY);
        double currObjX = imageX;
        double currObjTopY = imageY;

        offset += 1.5;

        for (LensesModel.Lens lens : lastExtraLenses) {
            double lensX = centerX + lens.getPosition() * scale;
            double f2 = lens.getFocalLength() * scale;
            double u2 = lensX - currObjX;
            if (u2 == 0) continue;
            double v2 = 1 / ((1 / f2) - (1 / u2));
            double nextX = lensX + v2;
            double nextY = centerY - (v2 / u2) * (centerY - currObjTopY);

            animateLensStage(currObjX, currObjTopY, lensX, centerY, f2, lastNumRays, offset);

            currObjX = nextX;
            currObjTopY = nextY;
            offset += 1.5;
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

        // === Ray 1: Parallel to axis, refracted through (or from) focal point ===
        Line ray1 = new Line(objX, objTopY, objX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, lensX, objTopY);
        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);

        if (!isConverging) {
            // draw virtual back
            double virtualFocalX = lensX - Math.abs(f);
            double slope = (objTopY - lensY) / (lensX - virtualFocalX);
            Line virtualRay = new Line(lensX, objTopY, lensX - rayExtension, objTopY - slope * rayExtension);
            virtualRay.setStroke(Color.RED);
            virtualRay.getStrokeDashArray().addAll(5d, 5d);
            animPane.getChildren().add(virtualRay);
        }

        // === Ray 2: Through center of lens ===
        Line ray2 = new Line(objX, objTopY, objX, objTopY);
        ray2.setStroke(Color.BLUE);

        // === Ray 3: Aimed at (or from) focal point, emerges parallel ===
        Line ray3 = new Line(objX, objTopY, objX, objTopY);
        Line ray3Refracted = new Line(lensX, objTopY, lensX, objTopY);
        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);

        if (!isConverging) {
            Line virtualF = new Line();
            double virtualFocalX = lensX + Math.abs(f);
            double slope = (lensY - objTopY) / (virtualFocalX - objX);
            double intersectY = objTopY + slope * (lensX - objX);

            virtualF.setStartX(objX);
            virtualF.setStartY(objTopY);
            virtualF.setEndX(lensX);
            virtualF.setEndY(intersectY);
            virtualF.setStroke(Color.GREEN);
            animPane.getChildren().add(virtualF);

            Line virtualRay = new Line(lensX, intersectY, lensX - rayExtension, intersectY);
            virtualRay.setStroke(Color.GREEN);
            virtualRay.getStrokeDashArray().addAll(5d, 5d);
            animPane.getChildren().add(virtualRay);
        }

        // Add rays to pane
        animPane.getChildren().addAll(ray1, ray1Refracted, ray2, ray3, ray3Refracted);

        // === Animate Ray 1 ===
        Duration t1 = Duration.seconds(offsetSeconds + 0.3);
        animationTimeline.getKeyFrames().add(new KeyFrame(t1,
                new KeyValue(ray1.endXProperty(), lensX)));
        keyframeTimes.add(t1);

        Duration t2 = Duration.seconds(offsetSeconds + 0.6);
        animationTimeline.getKeyFrames().add(new KeyFrame(t2,
                new KeyValue(ray1Refracted.endXProperty(), imageX),
                new KeyValue(ray1Refracted.endYProperty(), imageY)));
        keyframeTimes.add(t2);

        // === Animate Ray 2 ===
        Duration t3 = Duration.seconds(offsetSeconds + 0.8);
        animationTimeline.getKeyFrames().add(new KeyFrame(t3,
                new KeyValue(ray2.endXProperty(), imageX),
                new KeyValue(ray2.endYProperty(), imageY)));
        keyframeTimes.add(t3);

        // === Animate Ray 3 ===
        double nearFocalX = lensX - f;
        double targetY = isConverging
                ? lensY + (objTopY - lensY) * f / (objX - nearFocalX)
                : lensY; // stays horizontal if divergent

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

        // === Draw animated image arrow ===
        Line arrow = new Line(imageX, lensY, imageX, lensY);
        arrow.setStroke(Color.PURPLE);
        arrow.setStrokeWidth(2);
        animPane.getChildren().add(arrow);

        Duration t6 = Duration.seconds(offsetSeconds + 1.2);
        animationTimeline.getKeyFrames().add(new KeyFrame(t6,
                new KeyValue(arrow.endYProperty(), imageY)));
        keyframeTimes.add(t6);
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



    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Parameter Values");
        alert.setContentText(message);
        alert.showAndWait();
    }
}