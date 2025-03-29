package project.optics.jfkt.controllers;


import javafx.scene.control.Alert;
import project.optics.jfkt.models.Lens;
import project.optics.jfkt.models.LensesModel;
import project.optics.jfkt.views.LensView;

import java.util.List;
import java.util.stream.Collectors;

public class LensesController {
    private final LensesModel model;
    private final LensView view;

    public LensesController(LensesModel model, LensView view) {
        this.model = model;
        this.view = view;
        initialize();
    }

    private void initialize() {
        setupViewListeners();
        updateView();
    }

    private void setupViewListeners() {
        // Connect UI actions to controller methods
        view.getApplyButton().setOnAction(e -> applyParameters());
        view.getConvergingButton().setOnAction(e -> addConvergingLens());
        view.getDivergingButton().setOnAction(e -> addDivergingLens());
    }

    // ========================
    // Main Parameter Control
    // ========================
    public void applyParameters() {
        try {
            // Get text from input fields
            String odText = view.getObjectDistanceField();
            String ohText = view.getObjectHeightField();
            String flText = view.getFocalLengthField();
            String magText = view.getMagnificationField();
            String raysText = view.getNumRaysField();

            // Parse and validate
            double objectDistance = parseDouble(odText, "Object Distance");
            double objectHeight = parseDouble(ohText, "Object Height");
            double focalLength = parseDouble(flText, "Focal Length");
            double magnification = parseDouble(magText, "Magnification");
            int numRays = parseInt(raysText, "Number of Rays");

            // Additional validation
            if (objectDistance <= 0) throw new IllegalArgumentException("Object Distance must be positive");
            if (objectHeight <= 0) throw new IllegalArgumentException("Object Height must be positive");
            if (numRays < 1 || numRays > 20) throw new IllegalArgumentException("Number of Rays must be between 1-20");

            // Update model
            model.setObjectDistance(objectDistance);
            model.setObjectHeight(objectHeight);
            model.setFocalLength(focalLength);
            model.setMagnification(magnification);
            model.setNumRays(numRays);

            updateView();

        } catch (NumberFormatException e) {
            view.showError("Please enter valid numbers in all fields");
        } catch (IllegalArgumentException e) {
            view.showError(e.getMessage());
        }
    }

    // Helper methods for parsing and validation
    private double parseDouble(String text, String fieldName) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) {
            throw new NumberFormatException(fieldName + " cannot be empty");
        }
        return Double.parseDouble(text.trim());
    }

    private int parseInt(String text, String fieldName) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) {
            throw new NumberFormatException(fieldName + " cannot be empty");
        }
        return Integer.parseInt(text.trim());
    }

    // Lens Management
    public void addConvergingLens() {
        double defaultPosition = 10.0; // Units right of main lens
        double defaultFocalLength = 4.0; // Converging lens
        model.addExtraLens(defaultPosition, defaultFocalLength);
        updateView();
    }

    public void addDivergingLens() {
        double defaultPosition = 10.0; // Units right of main lens
        double defaultFocalLength = -4.0; // Diverging lens
        model.addExtraLens(defaultPosition, defaultFocalLength);
        updateView();
    }

    public void removeLens(int index) {
        model.removeExtraLens(index);
        updateView();
    }

    public void clearAllLenses() {
        model.clearExtraLenses();
        updateView();
    }

    // ========================
    // View Updates
    // ========================
    private void updateView() {
        view.updateView(
                model.getNumRays(),
                model.getObjectDistance(),
                model.getObjectHeight(),
                model.getMagnification(),
                model.getFocalLength(),
                model.getExtraLenses() // Passes List<Lens>
        );
    }

}