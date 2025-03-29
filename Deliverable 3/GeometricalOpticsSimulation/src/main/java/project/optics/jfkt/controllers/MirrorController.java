package project.optics.jfkt.controllers;

import project.optics.jfkt.views.MirrorView;

public class MirrorController {
    private MirrorView mirrorView;

    public MirrorController(MirrorView mirrorView){
        this.mirrorView = mirrorView;
    }

    public void onOption1ButtonPressed(){
        mirrorView.setisConcave(false);
        mirrorView.updateView();
    }

    public void onOption2ButtonPressed(){
        mirrorView.setisConcave(true);
        mirrorView.updateView();
    }

    public void onFocalLengthUpdated(double focalLength) {
        mirrorView.setFocalLength(focalLength);
        mirrorView.updateView();
    }
}
