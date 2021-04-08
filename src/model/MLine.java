/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 *
 * @author Gill
 */
public class MLine extends Line {

    String origin;
    String destiny;
    String type;
    Color mainColor;
    Color roverColor;
    double defStroke = 3f;
    double roverStroke = 5f;

    MLine(double d, double d0, double d1, double d2) {
        super(d, d0, d1, d2);
    }

    public void configLine(Color mainc, Color rovc, double ds, double rs) {
        this.mainColor = mainc;
        this.setStroke(mainc);
        this.roverColor = rovc;
        this.defStroke = ds;
        this.setStrokeWidth(ds);
        this.roverStroke = rs;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMainColor(Color mainColor) {
        this.setStroke(mainColor);
        this.mainColor = mainColor;
    }

    public void chooseMainColor() {
        this.setStroke(mainColor);
    }

    public void setRoverColor(Color roverColor) {
        this.setStroke(roverColor);
        this.roverColor = roverColor;
    }

    public void chooseRoverColor() {
        this.setStroke(roverColor);
    }

    public void setDefStroke(double defStroke) {
        this.defStroke = defStroke;
    }

    public void chooseDefStroke() {
        this.setStrokeWidth(this.defStroke);
    }

    public void setRoverStroke(double roverStroke) {
        this.roverStroke = roverStroke;
    }

    public void chooseRoverStroke() {
        this.setStrokeWidth(this.roverStroke);
    }
}
