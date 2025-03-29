package project.optics.jfkt.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import project.optics.jfkt.controllers.LensesController;
import project.optics.jfkt.models.Lens;
import project.optics.jfkt.models.LensesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LensView extends BaseView {
    private static final double DEFAULT_SCALE = 50.0;
    private double scale = DEFAULT_SCALE;
    private double centerY;
    private double centerX;

    // State tracking
    private int lastNumRays;
    private double lastObjectDistance;
    private double lastObjectHeight;
    private double lastMagnification;
    private double lastFocalLength;
    private List<LensesModel.Lens> lastExtraLenses = new ArrayList<>();

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
        VBox paramVBox = findParametersVBox(baseCenter);

        if (paramVBox != null) {
            paramVBox.getChildren().clear();

            // Create header
            Text header = new Text("Parameters:");
            header.setFont(new Font(40));
            header.setTextAlignment(TextAlignment.CENTER);
            header.setUnderline(true);
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

            // Add all components to VBox
            paramVBox.getChildren().addAll(
                    header,
                    objectDistanceHBox,
                    objectHeightHBox,
                    focalLengthHBox,
                    magnificationHBox,
                    numRaysHBox,
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

        drawExtraLenses(extraLenses, animPane);
        setupZoomControls(); // Re-add zoom controls
    }

    private void drawOpticalAxis(Pane pane) {
        Line axis = new Line(0, centerY, pane.getPrefWidth(), centerY);
        axis.setStroke(Color.BLACK);
        pane.getChildren().add(axis);
    }

    private void drawMainLens(Pane pane) {
        // Increased height to ±150 (was ±50) to ensure ray intersections
        Line lens = new Line(centerX, centerY - 150, centerX, centerY + 150);
        lens.setStroke(Color.BLUE);
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

        pane.getChildren().addAll(nearFocal, farFocal, nearLabel, farLabel);
    }

    private void drawObject(double x, double baseY, double heightPx, Pane pane) {
        // Object line
        Line objLine = new Line(x, baseY, x, baseY - heightPx);
        objLine.setStroke(Color.GREEN);
        objLine.setStrokeWidth(2);

        // Arrow head
        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );
        arrowHead.setFill(Color.GREEN);

        // Label
        Text label = new Text(x - 30, baseY - heightPx/2, "Object");

        pane.getChildren().addAll(objLine, arrowHead, label);
    }

    private void drawAllPrincipalRays(double objX, double objTopY,
                                      double lensX, double lensY,
                                      double focalLengthPx, Pane pane) {
        // 1. Calculate image position using lens formula: 1/f = 1/v + 1/u
        double u = lensX - objX; // Object distance from lens
        double imageDistance = 1 / (1/focalLengthPx - 1/u);
        double imageX = lensX + imageDistance;
        double imageHeight = -(imageDistance/u) * (lensY - objTopY);
        double imageY = lensY - imageHeight;

        boolean isConverging = focalLengthPx > 0;

        // 2. Ray parallel to optical axis (RED)
        Line parallelRay = new Line(objX, objTopY, lensX, objTopY);
        Line parallelRayRefracted;
        if (isConverging) {
            // Converges through far focal point (F')
            parallelRayRefracted = new Line(lensX, objTopY, imageX, imageY);
        } else {
            // Diverges as if coming from near virtual focus
            double virtualSlope = (objTopY - lensY) / (lensX - (lensX - focalLengthPx));
            parallelRayRefracted = new Line(lensX, objTopY,
                    lensX + Math.abs(focalLengthPx)*2,
                    objTopY + virtualSlope*Math.abs(focalLengthPx)*2);
        }

        // 3. Ray through lens center (BLUE)
        Line centralRay = new Line(objX, objTopY, imageX, imageY);

        // 4. Ray through near focal point (GREEN)
        Line focalRay;
        Line focalRayRefracted;
        if (isConverging) {
            // Passes through near focal point (F) before lens, then parallel
            double nearFocalX = lensX - focalLengthPx;
            double focalRayIntersectY = lensY + (objTopY-lensY)*focalLengthPx/(objX-nearFocalX);
            focalRay = new Line(objX, objTopY, lensX, focalRayIntersectY);
            focalRayRefracted = new Line(lensX, focalRayIntersectY, imageX, imageY);
        } else {
            // Aims toward virtual far focal point (F') before lens, then parallel
            double virtualFarFocalX = lensX + Math.abs(focalLengthPx);
            double virtualSlope = (objTopY - lensY) / (objX - virtualFarFocalX);
            double intersectY = lensY + virtualSlope * (lensX - objX);
            focalRay = new Line(objX, objTopY, lensX, intersectY);
            focalRayRefracted = new Line(lensX, intersectY,
                    lensX + Math.abs(focalLengthPx)*2,
                    intersectY);
        }

        // Style and add all rays
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN};
        parallelRay.setStroke(colors[0]);
        parallelRayRefracted.setStroke(colors[0]);
        centralRay.setStroke(colors[1]);
        focalRay.setStroke(colors[2]);
        focalRayRefracted.setStroke(colors[2]);

        for (Line ray : new Line[]{parallelRay, parallelRayRefracted,
                centralRay, focalRay, focalRayRefracted}) {
            ray.setStrokeWidth(2);
            pane.getChildren().add(ray);
        }

        // Draw image if real image exists (converging lens with object outside focal point)
        if (isConverging && u > focalLengthPx) {
            drawImageArrow(imageX, lensY, imageHeight, pane);
        } else if (!isConverging) {
            // For diverging lens, draw virtual image (dashed)
            drawVirtualImageArrow(imageX, lensY, imageHeight, pane);
        }
    }

    private void drawImageArrow(double x, double baseY, double heightPx, Pane pane) {
        Line imageLine = new Line(x, baseY, x, baseY - heightPx);
        imageLine.setStroke(Color.PURPLE);
        imageLine.setStrokeWidth(2);

        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );
        arrowHead.setFill(Color.PURPLE);
        pane.getChildren().addAll(imageLine, arrowHead);
    }

    private void drawVirtualImageArrow(double x, double baseY, double heightPx, Pane pane) {
        Line imageLine = new Line(x, baseY, x, baseY - heightPx);
        imageLine.setStroke(Color.PURPLE);
        imageLine.setStrokeWidth(2);
        imageLine.getStrokeDashArray().addAll(5d, 5d); // Dashed line for virtual image

        Polygon arrowHead = new Polygon(
                x, baseY - heightPx,
                x - 5, baseY - heightPx + 10,
                x + 5, baseY - heightPx + 10
        );
        arrowHead.setFill(Color.PURPLE);
        arrowHead.setOpacity(0.6); // Semi-transparent
        pane.getChildren().addAll(imageLine, arrowHead);
    }

    private void drawAdditionalRays(double objX, double objTopY,
                                    double lensX, double lensY,
                                    double focalLengthPx,
                                    int numExtraRays, Pane pane) {
        // Calculate image position (same as in drawAllPrincipalRays)
        double imageDistance = 1 / (1/focalLengthPx - 1/(lensX-objX));
        double imageX = lensX + imageDistance;
        double imageHeight = -((lensX-objX)/imageDistance) * (lensY-objTopY);
        double imageY = lensY - imageHeight;

        double objectHeight = lensY - objTopY;
        double spacing = objectHeight / (numExtraRays + 1);

        for (int i = 1; i <= numExtraRays; i++) {
            double yOffset = spacing * i;
            double rayStartY = objTopY + yOffset;

            // 1. Incident ray (object to lens)
            Line incidentRay = new Line(objX, rayStartY, lensX, rayStartY);

            // 2. Refracted ray (lens to image point)
            // Calculate proper refraction using lens formula
            double refractedSlope = (rayStartY - lensY) / (lensX - (lensX - focalLengthPx));
            Line refractedRay = new Line(lensX, rayStartY, imageX, imageY + yOffset*(imageHeight/objectHeight));

            // Style rays (semi-transparent orange)
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
            Line lensLine = new Line(lensX, centerY - 40, lensX, centerY + 40);
            lensLine.setStroke(lens.isConverging() ? Color.BLUE : Color.RED);
            if (!lens.isConverging()) {
                lensLine.getStrokeDashArray().addAll(5d, 5d);
            }
            lensLine.setStrokeWidth(2);
            pane.getChildren().add(lensLine);
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

    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Parameter Values");
        alert.setContentText(message);
        alert.showAndWait();
    }

}