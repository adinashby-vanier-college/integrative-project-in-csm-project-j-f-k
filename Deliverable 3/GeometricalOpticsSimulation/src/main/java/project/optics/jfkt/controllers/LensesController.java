package project.optics.jfkt.controllers;


import javafx.scene.control.Alert;
//import project.optics.jfkt.models.Lens;
import javafx.scene.control.TextField;
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
        System.out.println("LensesController initialized");
    }

    private void initialize() {
        setupViewListeners();
        updateView();
    }

    private void setupViewListeners() {
        view.getApplyButton().setOnAction(e -> applyParameters());

        view.getConvergingButton().setOnAction(e -> {
            view.addConvergingLensParams(10.0, 4.0);
        });

        view.getDivergingButton().setOnAction(e -> {
            view.addDivergingLensParams(10.0, -4.0);
        });
    }


    //Applying
    public void applyParameters() {
        try {
            // Existing main parameters
            double objectDistance = parseDouble(view.getObjectDistanceField(), "Object Distance");
            double objectHeight = parseDouble(view.getObjectHeightField(), "Object Height");
            double focalLength = parseDouble(view.getFocalLengthField(), "Focal Length");
            double magnification = parseDouble(view.getMagnificationField(), "Magnification");
            int numRays = parseInt(view.getNumRaysField(), "Number of Rays");

            // Validation
            if (objectDistance <= 0 || objectHeight <= 0 || numRays < 1 || numRays > 20) {
                throw new IllegalArgumentException("Check parameter values (positive and rays between 1-20).");
            }

            // Set main parameters in model
            model.setObjectDistance(objectDistance);
            model.setObjectHeight(objectHeight);
            model.setFocalLength(focalLength);
            model.setMagnification(magnification);
            model.setNumRays(numRays);

            // Handle extra lens parameters dynamically
            model.clearExtraLenses();
            for (TextField[] fields : view.extraLensFields) {
                double position = parseDouble(fields[0].getText(), "Lens Position");
                double lensFocalLength = parseDouble(fields[1].getText(), "Lens Focal Length");
                model.addExtraLens(position, lensFocalLength);
            }

            updateView();

        } catch (NumberFormatException e) {
            view.showError("Invalid input: " + e.getMessage());
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