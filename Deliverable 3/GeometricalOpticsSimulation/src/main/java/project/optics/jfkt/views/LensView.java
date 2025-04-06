package project.optics.jfkt.views;

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
        paramVBox = findParametersVBox(baseCenter);

        if (paramVBox != null) {
            paramVBox.getChildren().clear();

            // Create header
            Text header = new Text("Parameters:");
            header.setFont(new Font(40));
            header.setTextAlignment(TextAlignment.CENTER);
            header.setUnderline(true);
            header.getStyleClass().add("header-text");
            VBox.setMargin(header, new Insets(0, 0, 20, 0));

            // Create parameter HBoxes
            HBox objectDistanceHBox = createParameterHBox("Object Distance", "8.0");
            HBox objectHeightHBox = createParameterHBox("Object Height", "2.0");
            HBox focalLengthHBox = createParameterHBox("Focal Length", "4.0");
            HBox magnificationHBox = createParameterHBox("Magnification", "-0.5");
            HBox numRaysHBox = createParameterHBox("Number of Rays", "3");

            // Create Apply button
            applyButton = new Button("Apply");
            applyButton.setStyle("-fx-font-size: 16; -fx-pref-width: 120; -fx-pref-height: 40;");
            HBox buttonHBox = new HBox(applyButton);
            buttonHBox.setAlignment(Pos.CENTER);
            VBox.setMargin(buttonHBox, new Insets(20, 0, 0, 0));

            // Create Ray Length slider
            Text rayLengthLabel = new Text("Ray Length:");
            rayLengthLabel.setFont(new Font(20));

            Slider rayLengthSlider = new Slider(0, 100, 100); // default to 100
            rayLengthSlider.setShowTickLabels(true);
            rayLengthSlider.setShowTickMarks(true);
            rayLengthSlider.setMajorTickUnit(25);
            rayLengthSlider.setMinorTickCount(5);
            rayLengthSlider.setBlockIncrement(5);
            rayLengthSlider.setPrefWidth(250);

            // HBox for the label + slider
            HBox rayLengthBox = new HBox(15, rayLengthLabel, rayLengthSlider);
            rayLengthBox.setAlignment(Pos.CENTER);
            VBox.setMargin(rayLengthBox, new Insets(10, 0, 0, 0));


            // Add all components to VBox
            paramVBox.getChildren().addAll(
                    header,
                    objectDistanceHBox,
                    objectHeightHBox,
                    focalLengthHBox,
                    magnificationHBox,
                    numRaysHBox,
                    rayLengthBox,
                    buttonHBox
            );


            // Store references to text fields
            objectDistanceField = (TextField) objectDistanceHBox.getChildren().get(1);
            objectHeightField = (TextField) objectHeightHBox.getChildren().get(1);
            focalLengthField = (TextField) focalLengthHBox.getChildren().get(1);
            magnificationField = (TextField) magnificationHBox.getChildren().get(1);
            numRaysField = (TextField) numRaysHBox.getChildren().get(1);
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

        double objX = centerX - scaledObjDist;
        drawObject(objX, centerY, scaledObjHeight, animPane);
        drawAllPrincipalRays(objX, centerY - scaledObjHeight, centerX, centerY, scaledFL, animPane);

        if (numRays > 3) {
            drawAdditionalRays(objX, centerY - scaledObjHeight, centerX, centerY, scaledFL, numRays - 3, animPane);
        }

        drawMultiLensSystem(objX, centerY - scaledObjHeight, centerY, extraLenses, animPane);
        drawExtraLensLines(lastExtraLenses, animPane);

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

    private void drawExtraLenses(List<LensesModel.Lens> extraLenses, Pane pane) {
        for (LensesModel.Lens lens : extraLenses) {
            double lensX = centerX + (lens.getPosition() * scale);
            Line lensLine = new Line(lensX, centerY - 100, lensX, centerY + 100);
            lensLine.setStroke(lens.isConverging() ? Color.BLUE : Color.RED);
            lensLine.setStrokeWidth(3);

            if (!lens.isConverging()) {
                lensLine.getStrokeDashArray().addAll(10d, 5d);
            }

            Text lensLabel = new Text(lensX - 30, centerY - 110, lens.isConverging() ? "Converging" : "Diverging");
            lensLabel.setFont(new Font(14));
            lensLabel.setFill(lens.isConverging() ? Color.BLUE : Color.RED);

            pane.getChildren().addAll(lensLine, lensLabel);
        }
    }

    //Multi Lens System
    private void drawMultiLensSystem(double objX, double objTopY, double objBaseY,
                                     List<LensesModel.Lens> lenses, Pane pane) {

        double currObjX = objX;
        double currObjTopY = objTopY;
        double currObjBaseY = objBaseY;

        double currObjHeight = currObjBaseY - currObjTopY;

        for (LensesModel.Lens lens : lenses) {
            double lensX = centerX + (lens.getPosition() * scale);
            double f = lens.getFocalLength() * scale;
            double u = lensX - currObjX;

            if (u == 0) continue; // avoid division by zero

            double v = 1 / ((1 / f) - (1 / u));
            double imageX = lensX + v;
            double magnification = -v / u;
            double imageHeight = currObjHeight * magnification;
            double imageTopY = centerY - imageHeight;
            double imageBaseY = centerY;

            //  Correct rays based on image and lens
            draw3RaysBetween(currObjX, currObjTopY, lensX, f, imageX, imageTopY, pane);

            // draw arrow for intermediate image (optional)
            drawVirtualImageArrow(imageX, imageBaseY, imageHeight, pane);

            // update object for next lens
            currObjX = imageX;
            currObjTopY = imageTopY;
            currObjBaseY = imageBaseY;
            currObjHeight = imageHeight;
        }

        // Final image arrow
        drawImageArrow(currObjX, currObjBaseY, currObjTopY - currObjBaseY, pane);
    }

    private void draw3RaysBetween(double objX, double objTopY, double lensX, double f,
                                  double imageX, double imageTopY, Pane pane) {

        // Ray 1: Parallel → through image
        Line ray1 = new Line(objX, objTopY, lensX, objTopY);
        Line ray1Refracted = new Line(lensX, objTopY, imageX, imageTopY);
        ray1.setStroke(Color.RED);
        ray1Refracted.setStroke(Color.RED);
        pane.getChildren().addAll(ray1, ray1Refracted);

        // Ray 2: Through center
        Line ray2 = new Line(objX, objTopY, imageX, imageTopY);
        ray2.setStroke(Color.BLUE);
        pane.getChildren().add(ray2);

        // Ray 3: Aimed at F → refracted parallel
        double nearFocalX = lensX - f;
        double intersectY = centerY + (objTopY - centerY) * f / (objX - nearFocalX);

        Line ray3 = new Line(objX, objTopY, lensX, intersectY);
        Line ray3Refracted = new Line(lensX, intersectY, imageX, imageTopY);
        ray3.setStroke(Color.GREEN);
        ray3Refracted.setStroke(Color.GREEN);
        pane.getChildren().addAll(ray3, ray3Refracted);
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

    public String getNumRaysField() {
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