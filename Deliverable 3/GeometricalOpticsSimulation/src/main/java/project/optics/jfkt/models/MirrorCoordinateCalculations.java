package project.optics.jfkt.models;

import java.util.ArrayList;


public class MirrorCoordinateCalculations {
    //straight to Mirror
    private ArrayList<CoordinateModel> firstCoordinateSet  = new ArrayList<>();
    //straight to center
    private ArrayList<CoordinateModel> secondCoordinateSet = new ArrayList<>();
    //straight to focal
    private ArrayList<CoordinateModel> thirdCoordinateSet = new ArrayList<>();

    private double imageDistance;
    private  double imageHeight;


    public MirrorCoordinateCalculations(double focalLength, double scale, double centerX, double centerY, double objectDistance, double objectHeight, boolean isConcave){
        double x;
        double y;
        CoordinateModel coordinate;
        if (!isConcave) {
            //Convex
            focalLength = -focalLength;
            imageDistance = 1/(1/focalLength - 1/objectDistance);
            double magnification = -imageDistance/objectDistance;
            imageHeight = objectHeight*magnification;
            double xImageCoordinate = centerX - imageDistance*scale;
            double yImageCoordinate = centerY - (imageHeight*scale);
            double radius = Math.abs(2*focalLength)*scale;
            double squareTerm = (Math.pow(radius,2) - Math.pow(objectHeight*scale, 2));
            double squareTerm2 = (Math.pow(radius,2) - Math.pow(imageHeight*scale, 2));

            //top of object coordinate
            x = centerX - objectDistance* scale;
            y = centerY - objectHeight * scale;
            coordinate = new CoordinateModel(x,y);
            firstCoordinateSet.add(coordinate);
            secondCoordinateSet.add(coordinate);
            thirdCoordinateSet.add(coordinate);

            //ray going through center
            x = centerX;
            y = centerY;
            coordinate = new CoordinateModel(centerX, centerY);
            secondCoordinateSet.add(coordinate);

            //ray going through Focal
            x= centerX + (radius - (Math.sqrt(squareTerm2)));
            y= yImageCoordinate;
            coordinate = new CoordinateModel(x,y);
            thirdCoordinateSet.add(coordinate);

            //ray going straight to mirror
            x = centerX + (radius -(Math.sqrt(squareTerm)));
            y = centerY - objectHeight * scale;
            if (!Double.isNaN(x)){
                coordinate = new CoordinateModel(x,y);
                firstCoordinateSet.add(coordinate);

            } else {
                x= centerX*2;
                coordinate = new CoordinateModel(x,y);
                firstCoordinateSet.add(coordinate);
                firstCoordinateSet.add(coordinate);

            }
            //ray going to image height and distance
            x = xImageCoordinate;
            y = yImageCoordinate;
            coordinate = new CoordinateModel(x,y);
            if (firstCoordinateSet.size()<3) {
                firstCoordinateSet.add(coordinate);
            }
            secondCoordinateSet.add(coordinate);
            thirdCoordinateSet.add(coordinate);

        } else {

            focalLength = Math.abs(focalLength);
            imageDistance = 1/(1/focalLength - 1/objectDistance);
            double magnification = -imageDistance/objectDistance;
            imageHeight = objectHeight*(magnification);
            double xImageCoordinate = centerX - imageDistance*scale;
            double yImageCoordinate = centerY - (imageHeight*scale);
            double radius = Math.abs(2*focalLength)*scale;
            double squareTerm = (Math.pow(radius,2) - Math.pow(objectHeight*scale, 2));
            double squareTerm2 = (Math.pow(radius,2) - Math.pow(imageHeight*scale, 2));

            //top of object coordinate
            x = centerX - objectDistance* scale;
            y = centerY - objectHeight * scale;
            coordinate = new CoordinateModel(x,y);
            firstCoordinateSet.add(coordinate);
            secondCoordinateSet.add(coordinate);
            thirdCoordinateSet.add(coordinate);

            //ray going through center
            x = centerX;
            y = centerY;
            coordinate = new CoordinateModel(x, y);
            secondCoordinateSet.add(coordinate);
            if (objectDistance == focalLength){
                double deltaY = centerY - (centerY-objectHeight*scale);
                double deltaX = (centerX + focalLength*scale) - (centerX - objectDistance*scale);
                double slope = -deltaX/deltaY;
                y = centerY * 2;
                x = y/slope;
                coordinate = new CoordinateModel(x, y);
                secondCoordinateSet.add(coordinate);
            }



            //ray going through Focal
            x = centerX - (radius - Math.sqrt(squareTerm2));
            y = yImageCoordinate;
            if (!Double.isNaN(x)) {
                coordinate = new CoordinateModel(x, y);
                thirdCoordinateSet.add(coordinate);
            } else if (focalLength == objectDistance){
                double deltaY = centerY - (centerY-objectHeight*scale);
                double deltaX = (centerX + focalLength*scale) - (centerX - objectDistance*scale);
                double slope = deltaY/deltaX;
                x = centerX;
                y = centerY - (centerX - (centerX - objectDistance*scale))*slope;
                coordinate = new CoordinateModel(x, y);
                thirdCoordinateSet.add(coordinate);
                slope = -1/slope;
                y = centerY * 2;
                x = y/slope;
                coordinate = new CoordinateModel(x, y);
                thirdCoordinateSet.add(coordinate);
            } else{
                double deltaY = centerY - (centerY-objectHeight*scale);
                double deltaX = (centerX + focalLength*scale) - (centerX - objectDistance*scale);
                double slope = deltaY/deltaX;
                System.out.println("THis is the slope: " + slope);
                x = centerX;
                y = centerY - (centerX - (centerX - objectDistance*scale))*slope;
                coordinate = new CoordinateModel(x , y);
                thirdCoordinateSet.add(coordinate);
            }

            //ray going straight to mirror
            if (objectHeight <= Math.abs(2*focalLength)) {
                x = centerX - (radius - (Math.sqrt(squareTerm)));
                y = centerY - objectHeight * scale;
                coordinate = new CoordinateModel(x,y);
                firstCoordinateSet.add(coordinate);
                if (objectDistance == focalLength){
                    double deltaY = centerY - (centerY-objectHeight*scale);
                    double deltaX = (centerX + focalLength*scale) - (centerX - objectDistance*scale);
                    double slope = -deltaX/deltaY;
                    y = centerY * 2;
                    x = y/slope;
                    coordinate = new CoordinateModel(x,y);
                    firstCoordinateSet.add(coordinate);
                }
            } else {
                x = centerX*2;
                y = centerY - objectHeight*scale;
                coordinate = new CoordinateModel(x,y);
                firstCoordinateSet.add(coordinate);
                firstCoordinateSet.add(coordinate);
            }
            //ray going to image height and distance
            x = xImageCoordinate;
            y = yImageCoordinate;
            coordinate = new CoordinateModel(x,y);
            if (firstCoordinateSet.size()<3){
                firstCoordinateSet.add(coordinate);
            }
            if (secondCoordinateSet.size()<3) {
                secondCoordinateSet.add(coordinate);
            }
            if (thirdCoordinateSet.size()<3) {
                thirdCoordinateSet.add(coordinate);
            }
        }
    }

    public ArrayList<CoordinateModel> getFirstCoordinateSet() {
        return firstCoordinateSet;
    }

    public ArrayList<CoordinateModel> getSecondCoordinateSet() {
        return secondCoordinateSet;
    }

    public ArrayList<CoordinateModel> getThirdCoordinateSet() {
        return thirdCoordinateSet;
    }

    public double getImageDistance() {
        return imageDistance;
    }

    public double getImageHeight() {
        return imageHeight;
    }
}
