package project.optics.jfkt.controllers;

import project.optics.jfkt.models.MirrorCoordinateCalculations;
import project.optics.jfkt.views.MirrorView;

public class MirrorController {
    private MirrorView mirrorView;

    public MirrorController(MirrorView mirrorView){
        this.mirrorView = mirrorView;
    }

    public void onOption1ButtonPressed(){
        mirrorView.setisConcave(true);
        mirrorView.updateView();
    }

    public void onOption2ButtonPressed(){
        mirrorView.setisConcave(false);
        mirrorView.updateView();
    }

    public void onFocalLengthUpdated(double focalLength) {
        mirrorView.setFocalLength(focalLength);
        mirrorView.updateView();
    }

    public void onObjectDistanceUpdated(double objectDistance) {
        mirrorView.setObjectDistance(objectDistance);
        mirrorView.updateView();
    }

    public void onObjectHeightUpdated(double objectHeight){
        mirrorView.setObjectHeight(objectHeight);
        mirrorView.updateView();
    }

    public void onPlayButtonPressed(){
       //mirrorView.testCoordinate();
       mirrorView.playAnim();
    }
    public void onPauseButtonPressed(){
        mirrorView.pauseAnim();
    }
    public void onRedoButtonPressed(){
        mirrorView.updateView();
    }
    public void onSlowButtonPressed(){
        mirrorView.setVelocity(mirrorView.getDefaultVelocity()/2);
    }
    public void onNormalButtonPressed(){
        mirrorView.setVelocity(mirrorView.getDefaultVelocity());
    }
    public void onFastButtonPressed(){
        mirrorView.setVelocity(mirrorView.getDefaultVelocity()*2);
    }
    public void onZoomIn(){
        mirrorView.setScale(mirrorView.getScale() + 2);
        mirrorView.updateView();
    }
    public void onZoomOut(){
        if (mirrorView.getScale() > 2){
            mirrorView.setScale(mirrorView.getScale() - 2);
            mirrorView.updateView();
        }
    }
}
