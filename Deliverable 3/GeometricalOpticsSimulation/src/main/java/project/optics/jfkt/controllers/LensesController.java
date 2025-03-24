package project.optics.jfkt.controllers;


import project.optics.jfkt.models.LensesModel;
import project.optics.jfkt.views.LensView;

public class LensesController {
    private LensesModel model;
    private LensView view;

    public LensesController(LensesModel model, LensView view){
        this.model = model;
        this.view=view;
        updateView();
    }

    public void setParameters(int numRays, double objectDistance, double objectHeight, double magnification, double focalLength) {
        model.setNumRays(numRays);
        model.setObjectDistance(objectDistance);
        model.setObjectHeight(objectHeight);
        model.setMagnification(magnification);
        model.setFocalLength(focalLength);
        updateView();
    }
    public void addExtraLens(double position) {
        model.addExtraLens(position);
        updateView();
    }

    private void updateView() {
        view.updateView(
                model.getNumRays(),
                model.getObjectDistance(),
                model.getObjectHeight(),
                model.getMagnification(),
                model.getFocalLength(),
                model.getExtraLenses()
        );
    }
}
