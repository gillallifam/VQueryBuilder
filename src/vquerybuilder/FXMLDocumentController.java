/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vquerybuilder;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.DbControl;

/**
 *
 * @author Gill
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private TitledPane bdTablesPane;
    @FXML
    private Button btResetar;
    @FXML
    private Button btTestar;
    @FXML
    private Button btCopiar;
    @FXML
    private AnchorPane tableArea;
    @FXML
    private AnchorPane txtArea;
    @FXML
    private TextArea qExit;
    @FXML
    private HBox boxOptions;
    @FXML
    private VBox optBtVbox;
    @FXML
    private HBox boxChoiceGroup;
    @FXML
    private HBox boxChoiceOrder;
    @FXML
    private Button btOrderBy;
    @FXML
    private Button btGroupBy;

    private DbControl dbControl;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(boxOptions);
        System.out.println(optBtVbox);
        System.out.println(btOrderBy);
        System.out.println(btGroupBy);
        dbControl = new DbControl("vqbuilder");
        qExit.setWrapText(true);
        qExit.setFont(Font.font(22));
        /*ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().add("aaaa");
        choiceBox.getItems().add("bbbb");
        choiceBox.getItems().add("cccc");
        choiceBox.getItems().add("dddd");
        choiceBox.setValue("aaaa");
        boxOptions.getChildren().add(choiceBox);
        System.out.println(choiceBox.getValue());*/
    }

    public HBox getBoxChoiceGroup() {
        return boxChoiceGroup;
    }

    public void setBoxChoiceGroup(HBox boxChoiceGroup) {
        this.boxChoiceGroup = boxChoiceGroup;
    }

    public HBox getBoxChoiceOrder() {
        return boxChoiceOrder;
    }

    public void setBoxChoiceOrder(HBox boxChoiceOrder) {
        this.boxChoiceOrder = boxChoiceOrder;
    }

    public DbControl getDbControl() {
        return dbControl;
    }

    public TitledPane getBdTablesPane() {
        return bdTablesPane;
    }

    public void setBdTablesPane(TitledPane bdTablesPane) {
        this.bdTablesPane = bdTablesPane;
    }

    public TextArea getqExit() {
        return qExit;
    }

    public void setqExit(TextArea qExit) {
        this.qExit = qExit;
    }

    public Button getBtResetar() {
        return btResetar;
    }

    public void setBtResetar(Button btResetar) {
        this.btResetar = btResetar;
    }

    public Button getBtTestar() {
        return btTestar;
    }

    public void setBtTestar(Button btTestar) {
        this.btTestar = btTestar;
    }

    public Button getBtCopiar() {
        return btCopiar;
    }

    public void setBtCopiar(Button btCopiar) {
        this.btCopiar = btCopiar;
    }

    public AnchorPane getTableArea() {
        return tableArea;
    }

    public void setTableArea(AnchorPane tableArea) {
        this.tableArea = tableArea;
    }

    public HBox getBoxOptions() {
        return boxOptions;
    }

    public void setBoxOptions(HBox boxOptions) {
        this.boxOptions = boxOptions;
    }

    public VBox getOptBtVbox() {
        return optBtVbox;
    }

    public void setOptBtVbox(VBox optBtVbox) {
        this.optBtVbox = optBtVbox;
    }

    public Button getBtOrderBy() {
        return btOrderBy;
    }

    public void setBtOrderBy(Button btOrderBy) {
        this.btOrderBy = btOrderBy;
    }

    public Button getBtGroupBy() {
        return btGroupBy;
    }

    public void setBtGroupBy(Button btGroupBy) {
        this.btGroupBy = btGroupBy;
    }

    public AnchorPane getTxtArea() {
        return txtArea;
    }

    public void setTxtArea(AnchorPane txtArea) {
        this.txtArea = txtArea;
    }

}
