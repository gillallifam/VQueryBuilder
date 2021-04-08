package vquerybuilder;

import exception.DAOException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.DbControl;
import model.ItemTabela;
import model.Join;
import model.MLine;
import model.TbControl;
import model.Toast;
import model.Where;

/**
 *
 * @author Gill
 * @Alias tb é opçao de join
 * @Escolher join e where
 * @Usar textflow para cores no texto
 * @DbControl usar hashmap
 * @add bt limpar campos
 * @select where type on add
 * @make where groups
 * @I solved it is by registering a KeyHandler on the root node.
 */
public class VQueryBuilder extends Application {

    FXMLDocumentController controller;
    Scene scene;
    Parent mainPane;
    // Group root;
    Stage primaryStage;
    DbControl dbControl;
    List<ItemTabela> campos;
    List<String> chaves;
    List<String> relacionamentos;
    String relacionadas;
    TextArea qExit;
    String rltd = "%";
    private double initX;
    private double initY;
    private Point2D dragAnchor;
    boolean showRst = false;
    VBox tabResult;

    @Override
    public void start(Stage stg) throws Exception {
        this.primaryStage = stg;
        this.primaryStage.setTitle("Visual Query Builder");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        mainPane = loader.load();
        mainPane.setOnMouseClicked((MouseEvent me) -> {
            if (showRst) {
                try {
                    testarConsulta();
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        controller = (FXMLDocumentController) loader.getController();
        dbControl = controller.getDbControl();
        dbControl.carregarTabelas();
        // root = new Group();
        // dbControl.setRoot(root);
        createDbPane(dbControl.getDb(), controller.getBdTablesPane());
        scene = new Scene(mainPane);
        scene.setOnKeyPressed(event -> {//mudar para switch
            if (event.getCode() == KeyCode.F5) {
                try {
                    doReset();
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (event.getCode() == KeyCode.F6) {
                try {
                    testarConsulta();
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        resizing();
        primaryStage.maximizedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            Platform.runLater(() -> {
                resizing();
            });
        });
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> resizing();
        primaryStage.widthProperty().addListener(stageSizeListener);
        primaryStage.heightProperty().addListener(stageSizeListener);
        controller.getBtTestar().setOnMouseClicked((MouseEvent me) -> {
            try {
                testarConsulta();
            } catch (DAOException | SQLException ex) {
                Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        controller.getBtCopiar().setOnMouseClicked((MouseEvent me) -> {
            copyQuery();
        });
        controller.getBtResetar().setOnMouseClicked((MouseEvent me) -> {
            try {
                doReset();
            } catch (DAOException | SQLException ex) {
                Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        controller.getBtOrderBy().setOnMouseClicked((MouseEvent me) -> {
            if (dbControl.getQueryOrderBy().size() > 0) {
                dbControl.getQueryOrderBy().clear();
                //controller.getBtOrderBy().setStyle("-fx-background-color: #DCDCDC ; ");
            } else {
                //controller.getBtOrderBy().setStyle("-fx-background-color: #DC143C ; ");
                for (Node bx : controller.getBoxChoiceOrder().getChildren()) {
                    ChoiceBox ch = (ChoiceBox) bx;
                    if (!ch.getValue().equals("nenhum")) {
                        dbControl.getQueryOrderBy().add(ch.getValue().toString());
                    }
                }
            }
            controller.getqExit().setText(dbControl.generateQuery());
        });
        controller.getBtGroupBy().setOnMouseClicked((MouseEvent me) -> {
            if (dbControl.getQueryGroupBy().size() > 0) {
                dbControl.getQueryGroupBy().clear();
                //controller.getBtGroupBy().setStyle("-fx-background-color: #DCDCDC ; ");
            } else {
                //controller.getBtGroupBy().setStyle("-fx-background-color: #DC143C ; ");
                for (Node bx : controller.getBoxChoiceGroup().getChildren()) {
                    ChoiceBox ch = (ChoiceBox) bx;
                    if (!ch.getValue().equals("nenhum")) {
                        dbControl.getQueryGroupBy().add(ch.getValue().toString());
                    }
                }
            }
            controller.getqExit().setText(dbControl.generateQuery());
        });
    }

    public void resizing() {
        controller.getTxtArea().setPrefHeight(scene.getHeight() * 0.11);
        controller.getTableArea().setPrefHeight(scene.getHeight() * 0.91);
        btRresizing(controller.getBtResetar(), controller.getBtTestar(), controller.getBtCopiar());
    }

    public void btRresizing(Button... btns) {
        for (Button b : btns) {
            b.setPrefHeight(scene.getHeight() * 0.03);
            b.setPrefWidth(scene.getWidth() * 0.7);
        }
    }

    private TitledPane createDbPane(String titulo, TitledPane tpane) throws DAOException, SQLException {
        ListView lv = new ListView();
        tpane.setText(titulo);
        tpane.setContent(lv);
        tpane.setCursor(Cursor.HAND);
        dbControl.getTabelas().forEach((i) -> {
            lv.getItems().add(i.getNome());
        });
        lv.setPrefHeight(dbControl.getTabelas().size() * 29.00);
        lv.setId(titulo);
        lv.setPrefWidth(150);

        lv.setOnMouseClicked((MouseEvent me) -> {
            if (me.getButton().equals(MouseButton.PRIMARY) && me.getClickCount() == 2) {
                try {
                    String ttl = "" + lv.getSelectionModel().getSelectedItem();
                    boolean added = false;
                    boolean go = false;
                    boolean isAlias = false;
                    boolean joined = false;
                    if (dbControl.getControladores().isEmpty()) {// cria o primeiro controlador obrigatoriamente
                        TbControl tbc = showPane(ttl);
                        tbc.getPane().setText(ttl + " (1)");
                        dbControl.addControlador(tbc);
                        List<TbControl> controladores = dbControl.getControladores();
                        dbControl.setQueryFrom(ttl, "");
                        setHeightListener(tbc);
                    } else {
                        for (TbControl tc : dbControl.getControladores()) {
                            if (tc.getNome().equals(ttl)) {// tratar auto referenciamento aqui
                                if (tc.getReferencias().contains(ttl)
                                        && dbControl.getCtrlByName(ttl).getInstances() == 1) {
                                    isAlias = true;
                                    break;
                                }
                                added = true;
                                Toast.makeText(primaryStage, "Tabela já adicionada.", 2000, 500, 500);
                                break;
                            }
                        }
                        if (!added) {
                            if (!isAlias) {
                                for (TbControl tc : dbControl.getControladores()) {
                                    if (tc.getReferencias().contains(ttl)) {
                                        rltd = tc.getNome() + "#" + ttl;
                                        go = true;
                                        break;
                                    }
                                    if (tc.getRelatores().contains(ttl)) {
                                        rltd = ttl + "#" + tc.getNome();
                                        go = true;
                                        break;
                                    }
                                }
                            } else {
                                go = true;
                            }
                            if (go) {
                                TbControl tbc = showPane(ttl);
                                String subName;
                                if (isAlias) {
                                    TextInputDialog dialog = new TextInputDialog("");
                                    dialog.setTitle("Auto referencia detectada");
                                    dialog.setHeaderText("Entre com nome da tabela que auto referencia " + ttl);
                                    dialog.setContentText("Nome:");
                                    Optional<String> result = dialog.showAndWait();
                                    if (result.isPresent()) {
                                        subName = result.get();
                                        tbc.getPane().setText(subName);
                                        tbc.setNome(subName);
                                        tbc.setAlias(subName);
                                        tbc.getPane().setId(subName);
                                        tbc.getPane().getContent().setId(subName);
                                    } else {
                                        subName = ttl + " 2";
                                        tbc.getPane().setText(subName);
                                        tbc.setNome(subName);
                                        tbc.setAlias(subName);
                                        tbc.getPane().setId(subName);
                                        tbc.getPane().getContent().setId(subName);
                                    }
                                    dbControl.addControlador(tbc);
                                    tbc.getPane()
                                            .setText(tbc.getNome() + " (" + dbControl.getControladores().size() + ")");
                                    dbControl.getCtrlByName(ttl).setInstances(2);
                                    dbControl.getCtrlByName(subName).setInstances(2);
                                    MLine line = dbControl.addLine(ttl + "#" + subName);
                                    line.setMainColor(Color.YELLOW);
                                    controller.getTableArea().getChildren().add(line);
                                    String[] keyPair = dbControl.getKeyPairs(ttl, ttl).split("#");
                                    keyPair[0] = ttl + "." + keyPair[0];
                                    keyPair[1] = subName + "." + keyPair[1];
                                    dbControl.setQueryFrom(ttl + " as " + subName, keyPair[0] + "#" + keyPair[1]);
                                } else {
                                    dbControl.addControlador(tbc);
                                    tbc.getPane()
                                            .setText(tbc.getNome() + " (" + dbControl.getControladores().size() + ")");
                                    Join doJoin = chooseJoin(tbc);
                                    // dbControl.setQueryFrom(ttl, dbControl.getKeyPairs(doJoin, ttl));
                                    for (String str : tbc.getReferencias()) {
                                        if (dbControl.getCtrlByName(str) != null && !tbc.getNome().equals(str)) {
                                            // dbControl.setQueryFrom(ttl, dbControl.getKeyPairs(str, ttl));
                                            if (!joined) {
                                                String kp = dbControl.getKeyPairs(doJoin.getFromTable(),
                                                        doJoin.getToTable());
                                                String[] spt = kp.split("#");
                                                doJoin.setPriField(spt[0]);
                                                doJoin.setSecField(spt[1]);
                                                dbControl.setQueryFrom(doJoin);
                                                joined = true;
                                                MLine line = dbControl
                                                        .addLine(doJoin.getFromTable() + "#" + doJoin.getToTable());
                                                line.setMainColor(Color.BLUE);
                                                controller.getTableArea().getChildren().add(line);
                                            } else {
                                                // controller.getTableArea().getChildren().add(dbControl.addLine(str +
                                                // "#" + ttl));
                                            }
                                        }
                                    }
                                    for (String str : tbc.getRelatores()) {
                                        if (dbControl.getCtrlByName(str) != null && !tbc.getNome().equals(str)) {
                                            // dbControl.setQueryFrom(ttl, dbControl.getKeyPairs(ttl, str));
                                            if (!joined) {
                                                String kp = dbControl.getKeyPairs(doJoin.getFromTable(),
                                                        doJoin.getToTable());
                                                String[] spt = kp.split("#");
                                                doJoin.setPriField(spt[0]);
                                                doJoin.setSecField(spt[1]);
                                                dbControl.setQueryFrom(doJoin);
                                                joined = true;
                                                MLine line = dbControl
                                                        .addLine(doJoin.getToTable() + "#" + doJoin.getFromTable());
                                                line.setMainColor(Color.BLUE);
                                                controller.getTableArea().getChildren().add(line);
                                            } else {
                                                // controller.getTableArea().getChildren().add(dbControl.addLine(ttl +
                                                // "#" + str));
                                            }
                                        }
                                    }
                                }
                                setHeightListener(tbc);
                            } else {
                                Toast.makeText(primaryStage, "Tabela sem referência.", 2000, 500, 500);
                            }
                        }
                    }
                    controller.getqExit().setText(dbControl.generateQuery());
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        tpane.setOnMouseEntered((MouseEvent me) -> {
            tpane.toFront();
        });
        tpane.toFront();
        return tpane;
    }

    public Join chooseJoin(TbControl tbc) throws DAOException, SQLException {
        Join join = new Join();
        join.setFromTable(tbc.getNome());
        String selected = "cancelled.";
        List<String> list = new ArrayList();
        for (String str : tbc.getReferencias()) {
            if (dbControl.getCtrlByName(str) != null && !tbc.getNome().equals(str)) {
                list.add(str);
            }
        }
        for (String str : tbc.getRelatores()) {
            if (dbControl.getCtrlByName(str) != null && !tbc.getNome().equals(str)) {
                list.add(str);
            }
        }
        if (list.size() > 1) {
            ChoiceDialog dialog = new ChoiceDialog(list.get(0), list);
            dialog.setTitle("Atenção!");
            dialog.setHeaderText("Selecione a tabela para JUNTAR.");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                selected = result.get();
            }
        } else {
            selected = list.get(0);
        }
        join.setToTable(selected);
        join.setType("INNER");//deve vir da tabela
        System.out.println(selected);
        return join;
    }

    public void setHeightListener(TbControl tbc) {
        tbc.getPane().heightProperty().addListener((obs, oldVal, newVal) -> {
            try {
                // tbc.setAnchors();
                tbc.updateLines();

            } catch (DAOException | SQLException ex) {
                Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    public TbControl showPane(String ttl) throws DAOException, SQLException {
        TitledPane tp = createPane(ttl);
        tp.setId(ttl);
        TbControl tbControl = new TbControl(ttl);
        tbControl.setDbControl(dbControl);
        tbControl.loadInfoFromBd();// relations, relateds and keys
        tbControl.setPane(tp);
        // tbControl .setRoot(root );
        int rnd = -200;
        //int max = 200;
        rnd = -200 + (int) (Math.random() * ((200 - -200) + 1));
        //max = min + (int) (Math.random() * ((max - min) + 1));
        tp.setTranslateX(controller.getTableArea().getWidth() / 2 + rnd);
        tp.setTranslateY(controller.getTableArea().getHeight() / 2 + rnd);
        tp.toFront();
        controller.getTableArea().getChildren().add(tp);
        // root.getChildren().add(tp);
        return tbControl;
    }

    private void doReset() throws DAOException, SQLException {
        if (showRst) {
            testarConsulta();
        }
        controller.getqExit().setText("");
        dbControl.reset(controller.getTableArea());
    }

    private TitledPane createPane(String titulo) throws DAOException, SQLException {
        ListView lv = new ListView();
        ListView lv2 = new ListView();
        lv2.setEditable(true);
        lv2.setCellFactory(TextFieldListCell.forListView());
        lv2.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                if (t.getNewValue().equals("")) {
                    lv2.getItems().set(t.getIndex(), t.getNewValue());
                    dbControl.remWhere((String) lv.getItems().get(t.getIndex()));

                } else {
                    Where where = new Where();
                    where.setConnector("AND"); // dialog
                    where.setField((String) lv.getItems().get(t.getIndex()));
                    String lgc = t.getNewValue().replace("=", where.getField() + "=");
                    lgc = lgc.replace(">", where.getField() + ">");
                    lgc = lgc.replace("<", where.getField() + "<");
                    lgc = lgc.replace("between ", where.getField() + " BETWEEN ");
                    lgc = lgc.replace("in ", where.getField() + " IN ");
                    lgc = lgc.replace("like ", where.getField() + " LIKE ");
                    lgc = lgc.replace(" or ", " OR ");
                    lgc = lgc.replace(" and ", " AND ");
                    where.setLogic(lgc);
                    dbControl.addWhere(where);
                    lv2.getItems().set(t.getIndex(), t.getNewValue());
                }
                controller.getqExit().setText(dbControl.generateQuery());
            }
        });
        HBox dbPaneHbox = new HBox();
        dbPaneHbox.getChildren().addAll(lv, lv2);
        final TitledPane tp = new TitledPane(titulo, dbPaneHbox);
        tp.setCursor(Cursor.HAND);
        campos = dbControl.getFields(titulo);
        campos.forEach((i) -> {
            lv.getItems().add(i.getNome());
            lv2.getItems().add("");

        });
        lv.setPrefHeight(campos.size() * 32.100);
        lv2.setPrefHeight(campos.size() * 32.100);
        lv.setId(titulo);
        lv.setPrefWidth(150);
        tp.setPrefWidth(100);
        tp.setExpanded(false);
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Remover");
        mi1.setOnAction((ActionEvent event) -> {
            if (dbControl.getQueryFrom().equals(tp.getId())) {
                try {
                    doReset();
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                dbControl.removeJoin(tp.getId());
                // root.getChildren().remove(tp);
                dbControl.getCtrlByName(tp.getId()).getLines().forEach((l) -> {
                    // root.getChildren().remove(l);
                });
                dbControl.getControladores().remove(dbControl.getCtrlByName(tp.getId()));
                qExit.setText(dbControl.generateQuery());

            }
        });
        cm.getItems().add(mi1);

        tp.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent t) -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                cm.show(tp, t.getScreenX(), t.getScreenY());
            }
        });
        lv.setOnMouseClicked((MouseEvent me) -> {
            String cmp = null; // alias
            String tbName = dbControl.getCtrlByName(lv.getId()).getAlias();
            if (tbName == null) {
                tbName = lv.getId();
            }
            int instances = dbControl.getCtrlByName(lv.getId()).getInstances();
            try {
                cmp = "" + lv.getSelectionModel().getSelectedItem();
            } catch (NullPointerException e) {
            }
            if (cmp != null) {
                if (me.getButton().equals(MouseButton.PRIMARY) && me.getClickCount() == 2) {
                    if (!dbControl.getCmpAdicionados().contains(cmp)) {
                        if (instances > 1) {
                            dbControl.addCampo(tbName + "." + cmp);
                        } else {
                            dbControl.addCampo(cmp);
                        }
                    } else {
                        if (instances > 1) {
                            dbControl.remCampo(tbName + "." + cmp);
                        } else {
                            dbControl.remCampo(cmp);
                        }
                    }
                    controller.getBoxChoiceGroup().getChildren().clear();
                    controller.getBoxChoiceOrder().getChildren().clear();
                    for (int ch = 1; ch <= 5; ch++) {
                        ChoiceBox<String> choiceBox1 = new ChoiceBox();
                        ChoiceBox<String> choiceBox2 = new ChoiceBox();
                        choiceBox1.getItems().add("nenhum");
                        choiceBox1.setValue("nenhum");
                        choiceBox2.getItems().add("nenhum");
                        choiceBox2.setValue("nenhum");
                        for (String c : dbControl.getCmpAdicionados()) {
                            choiceBox1.getItems().add(c);
                            choiceBox2.getItems().add(c);
                        }
                        controller.getBoxChoiceGroup().getChildren().add(choiceBox1);
                        controller.getBoxChoiceOrder().getChildren().add(choiceBox2);
                    }
                }
                controller.getqExit().setText(dbControl.generateQuery());
            }
        });
        tp.expandedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (tp.isExpanded()) {
                tp.setPrefWidth(400);
            } else {
                tp.setPrefWidth(100);
            }
        });
        tp.setOnMouseDragged((MouseEvent me) -> {
            tp.setCollapsible(false);
            double dragX = me.getSceneX() - dragAnchor.getX();
            double dragY = me.getSceneY() - dragAnchor.getY();
            // calculate new position of the node
            double newXPosition = initX + dragX;
            double newYPosition = initY + dragY;
            // if new position do not exceeds borders of the rectangle, translate to this
            // position
            tp.setTranslateX(newXPosition);
            tp.setTranslateY(newYPosition);

            try {
                dbControl.getCtrlByName(tp.getId()).updateLines();
            } catch (DAOException | SQLException ex) {
                Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        tp.setOnMouseEntered((MouseEvent me) -> {
            if (tp.isExpanded()) {
                tp.setPrefWidth(450);
            }
            tp.toFront();

        });
        tp.setOnMouseExited((MouseEvent me) -> {
            if (tp.isExpanded()) {
                tp.setExpanded(false);
                tp.setPrefWidth(100);
            }
        });
        tp.setOnMousePressed((MouseEvent me) -> {
            // when mouse is pressed, store initial position
            initX = tp.getTranslateX();
            initY = tp.getTranslateY();
            dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
        });
        tp.setOnMouseReleased((MouseEvent me) -> {
            tp.setCollapsible(true);
        });
        return tp;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void testarConsulta() throws DAOException, SQLException {
        if (controller.getqExit().getText().equals("")) {
            Toast.makeText(primaryStage, "Consulta invalida!", 2000, 500, 500);
        } else {
            if (showRst) {
                showRst = false;
                controller.getTableArea().getChildren().remove(tabResult);
                controller.getBtTestar().setText("Executar");
            } else {
                showRst = true;
                controller.getBtTestar().setText("Ocultar");
                TableView<List<String>> table = new TableView<>();
                table.setEditable(false);
                List<String> fields = dbControl.getCmpAdicionados();
                if (fields.isEmpty()) {
                    List<TbControl> controladores = dbControl.getControladores();
                    for (TbControl tbc : controladores) {
                        for (String fld : tbc.getFields()) {
                            fields.add(fld);
                        }
                    }
                }
                for (int i = 0; i < fields.size(); i++) {
                    TableColumn<List<String>, String> column = new TableColumn<>(fields.get(i));
                    column.setStyle("-fx-alignment: CENTER;");
                    final int colIndex = i;
                    column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(colIndex)));
                    table.getColumns().add(column);
                }
                List<List<String>> dados;
                try {
                    dados = dbControl.testarConsulta(controller.getqExit().getText());
                    ObservableList<List<String>> inpData = FXCollections.observableArrayList(dados);
                    table.setItems(inpData);
                } catch (DAOException | SQLException ex) {
                    Logger.getLogger(VQueryBuilder.class.getName()).log(Level.SEVERE, null, ex);
                }
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                table.setTranslateX(160);
                table.setPrefWidth(controller.getTableArea().getWidth() - 300);
                tabResult = new VBox();
                tabResult.setSpacing(5);
                tabResult.setPadding(new Insets(10, 0, 0, 10));
                tabResult.getChildren().add(table);
                controller.getTableArea().getChildren().add(tabResult);
            }
        }
    }

    public void copyQuery() {
        StringSelection stringSelection = new StringSelection(controller.getqExit().getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        Toast.makeText(primaryStage, "Consulta copiada para area de transferência.", 2000, 500, 500);
    }

    public void doStore() {
        primaryStage.iconifiedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            System.out.println("minimized:" + t1);

        });
    }
}
