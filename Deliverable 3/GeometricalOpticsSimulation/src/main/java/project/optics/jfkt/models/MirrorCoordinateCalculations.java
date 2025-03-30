package project.optics.jfkt.models;

import java.util.ArrayList;

public class MirrorCoordinateCalculations {
    private ArrayList<Double> xCoordinates = new ArrayList<>();
    private ArrayList<Double> yCoordinates = new ArrayList<>();

    public MirrorCoordinateCalculations(double focalLength, double scale, double centerX, double centerY, double objectDistance, double objectHeight, boolean isConcave){
        if (!isConcave) {
            focalLength = -Math.abs(focalLength);
            double imageDistance = 1/(1/focalLength - 1/objectDistance);
            double magnification = -imageDistance/objectDistance;
            double imageHeight = objectHeight*magnification;
            System.out.println("magnification :" + magnification +", imageheight: " + imageHeight+", imagedistance: " + imageDistance);
            double xImageCoordinate = centerX + imageDistance*scale;
            double yImageCoordinate = centerY - (imageHeight*scale);
            double radius = Math.abs(2*focalLength)*scale;
            double squareTerm = (Math.pow(radius,2) - Math.pow(objectHeight*scale, 2));

            //top of object coordinate
            xCoordinates.add(centerX - objectDistance* scale);
            yCoordinates.add(centerY - objectHeight * scale);
            //ray going through center
            xCoordinates.add(centerX);
            yCoordinates.add(centerY);
            //ray going through Focal
            xCoordinates.add(centerX + radius/2);
            yCoordinates.add(centerY);
            //ray going straight to mirror
            xCoordinates.add(centerX + (radius -(Math.sqrt(squareTerm))));
            yCoordinates.add(centerY - objectHeight * scale);
            //ray going to image height and distance
            xCoordinates.add(xImageCoordinate);
            yCoordinates.add(yImageCoordinate);
        } else {
            double imageDistance = 1/(1/focalLength - 1/objectDistance);
            double magnification = -imageDistance/objectDistance;
            double imageHeight = objectHeight*(magnification);
            double xImageCoordinate = centerX + imageDistance*scale;
            double yImageCoordinate = centerY - (imageHeight*scale);
            double radius = Math.abs(2*focalLength)*scale;
            double squareTerm = (Math.pow(radius,2) - Math.pow(objectHeight*scale, 2));

            //top of object coordinate
            xCoordinates.add(centerX - objectDistance* scale);
            yCoordinates.add(centerY - objectHeight * scale);
            //ray going through center
            xCoordinates.add(centerX);
            yCoordinates.add(centerY);
            //ray going through Focal
            xCoordinates.add(centerX - radius/2);
            yCoordinates.add(centerY);
            //ray going straight to mirror
            xCoordinates.add(centerX - (radius -(Math.sqrt(squareTerm))));
            yCoordinates.add(centerY - objectHeight * scale);
            //ray going to image height and distance
            xCoordinates.add(xImageCoordinate);
            yCoordinates.add(yImageCoordinate);

        }
    }

    public ArrayList<Double> getxCoordinates() {
        return xCoordinates;
    }

    public ArrayList<Double> getyCoordinates() {
        return yCoordinates;
    }
}
