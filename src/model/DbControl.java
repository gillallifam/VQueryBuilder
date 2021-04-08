package model;

import dao.DbDAO;
import exception.DAOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 *
 * @author Gill
 */
public class DbControl {

    private final DbDAO dbdao;
    //private Group root;
    TableView tabela;
    List<ItemTabela> campos;
    List<TbControl> controladores = new ArrayList<>();
    Map<String, TbControl> ctrls = new HashMap<>();
    List<String> cmpsSelecionados = new ArrayList<>();
    List<Where> wheres = new ArrayList<>();
    String queryInit = "SELECT";
    String queryFields = "*";
    String queryFrom = "";
    List<Join> queryJoins = new ArrayList<>();
    String queryWhere = "";
    //String queryGroupBy = "";
    Set<String> queryGroupBy = new HashSet();
    //String queryOrderBy = "";
    Set<String> queryOrderBy = new HashSet();
    List<Line> lines = new ArrayList<>();

    public MLine addLine(String parts) {
        String[] panes = parts.split("#");
        TbControl ctrl1 = getCtrlByName(panes[0]);
        TbControl ctrl2 = getCtrlByName(panes[1]);
        TitledPane t1 = ctrl1.getPane();
        TitledPane t2 = ctrl2.getPane();
        Bounds bounds1 = t1.localToScene(t1.getBoundsInLocal());
        Bounds bounds2 = t2.localToScene(t2.getBoundsInLocal());
        MLine line = new MLine(bounds1.getMinX() + bounds1.getWidth() / 2, bounds1.getMinY() + bounds1.getHeight() / 2,
                bounds2.getMinX() + bounds2.getWidth() / 2, bounds2.getMinY() + bounds2.getHeight() / 2);
        line.configLine(Color.CORNSILK, Color.CRIMSON, 3f, 5f);
        line.getStrokeDashArray().addAll(10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d);
        line.setOrigin(panes[0]);
        line.setDestiny(panes[1]);
        line.setCursor(Cursor.HAND);
        ctrl1.addLine(line);
        ctrl2.addLine(line);
        //root.getChildren().add(line);
        Tooltip t = new Tooltip(panes[1] + " referencia " + panes[0]);
        Tooltip.install(line, t);
        line.setOnMouseEntered((MouseEvent me) -> {
            line.chooseRoverColor();
            line.chooseRoverStroke();
        });
        line.setOnMouseExited((MouseEvent me) -> {
            line.chooseMainColor();
            line.chooseDefStroke();
            //line.getStrokeDashArray().addAll(10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d, 10d);
        });
        addLineAnimation(line);
        Platform.runLater(() -> {
            line.toBack();
        });
        return line;
    }

    public void defaultLine(Line line) {//
        line.setStroke(Color.BLUE);
        line.setStrokeWidth(3f);
    }

    public List<Where> getWheres() {
        return wheres;
    }

    public void setWheres(List<Where> wheres) {
        this.wheres = wheres;
    }

    public void addWhere(Where where) {
        boolean exists = false;
        int index = -1;
        for (Where w : wheres) {//for?
            index++;
            if (w.getField().equals(where.getField())) {
                exists = true;
                wheres.set(index, where);
                break;
            }
        }
        if (!exists) {
            this.wheres.add(where);
        }
    }

    public void remWhere(String where) {
        Where tw = null;
        for (Where w : wheres) {
            if (w.field.equals(where)) {
                tw = w;
                break;
            }
        }
        this.wheres.remove(tw);
        if (!wheres.isEmpty()) {
            wheres.get(0).setConnector("");
        }
    }

    public void addLineAnimation(Line line) {
        double maxOffset
                = line.getStrokeDashArray().stream()
                        .reduce(
                                0d,
                                (a, b) -> a + b
                        );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(5),
                        new KeyValue(
                                line.strokeDashOffsetProperty(),
                                maxOffset,
                                Interpolator.LINEAR
                        )
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public String getQueryInit() {
        return queryInit;
    }

    public void setQueryInit(String queryInit) {
        this.queryInit = queryInit;
    }

    public String getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String queryFields) {
        this.queryFields = queryFields;
    }

    public String getQueryFrom() {
        return queryFrom;
    }

    public void setQueryFrom(Join j) {
        if (this.queryFrom.equals("")) {
            this.queryFrom = j.getFromTable();
        } else {
            this.queryJoins.add(j);
        }
    }

    public void setQueryFrom(String part, String fields) {
        String[] keys = fields.split("#");
        if (this.queryFrom.equals("")) {
            this.queryFrom = part;
        } else {
            //this.queryJoins.add(new Join("INNER", part, keys[0], keys[1]));
        }
    }

    public List<Join> getQueryJoin() {
        return queryJoins;
    }

    public String getQueryWhere() {
        return queryWhere;
    }

    public void setQueryWhere(String queryWhere) {
        this.queryWhere = queryWhere;
    }

    public Set<String> getQueryGroupBy() {
        return queryGroupBy;
    }

    public void setQueryGroupBy(Set<String> queryGroupBy) {
        this.queryGroupBy = queryGroupBy;
    }

    public Set<String> getQueryOrderBy() {
        return queryOrderBy;
    }

    public void setQueryOrderBy(Set<String> queryOrderBy) {
        this.queryOrderBy = queryOrderBy;
    }

    public void addCampo(String cmp) {
        this.cmpsSelecionados.add(cmp);
    }

    public void remCampo(String cmp) {
        this.cmpsSelecionados.remove(cmp);
    }

    public List<String> getCmpAdicionados() {
        return cmpsSelecionados;
    }

    public void setCmpAdicionados(List cmpAdicionados) {
        this.cmpsSelecionados = cmpAdicionados;
    }

    public void setRoot(Group root) {
        //this.root = root;
    }

    public DbControl(String nome) {
        dbdao = new DbDAO(nome);
    }

    public void setDb(String nome) {
        dbdao.setDb(nome);//sera usado
    }

    public void carregarTabelas() throws DAOException, SQLException {
        campos = dbdao.getTables();
    }

    public List<ItemTabela> getTabelas() throws DAOException, SQLException {
        return campos;
    }

    public List<TbControl> getControladores() {
        return controladores;
    }

    public void addControlador(TbControl ctrl) {
        this.controladores.add(ctrl);
    }

    public void addControladorH(TbControl ctrl) {
        this.ctrls.put(ctrl.nome, ctrl);
    }

    public TbControl getCtrlWithName(String find) {
        return ctrls.get(find);
    }

    public TbControl getCtrlByName(String find) {
        for (TbControl ctl : this.controladores) {
            if (ctl.nome.equals(find)) {
                return ctl;
            }
        }
        return null;
    }

    public void setQueryJoins(List<Join> queryJoins) {
        this.queryJoins = queryJoins;
    }

    public void removeJoin(String join) {
        Join r = null;
        for (Join j : this.queryJoins) {
            if (j.toTable.equals(join)) {
                r = j;
            }
        }
        this.queryJoins.remove(r);
    }

    public String generateQuery() {
        if (cmpsSelecionados.isEmpty()) {
            setQueryFields("*");
        } else {
            String flds = String.join(", ", cmpsSelecionados);
            setQueryFields(flds);
        }
        String joined = queryInit + " " + queryFields + " FROM " + queryFrom;

        for (Join j : this.queryJoins) {
            joined = joined + " " + j.getType() + " JOIN " + j.getFromTable() + " ON " + j.priField + "=" + j.secField;
        }

        if (!wheres.isEmpty()) {
            joined += " WHERE";
            for (int w = 0; w < wheres.size(); w++) {
                if (w == 0) {
                    joined += " " + wheres.get(w).getLogic();
                } else {
                    joined += " " + wheres.get(w).connector + " " + wheres.get(w).getLogic();
                }
            }
        }

        if (!queryGroupBy.isEmpty()) {
            joined += " GROUP BY";
            int cnt = 0;
            for (Iterator<String> it = this.queryGroupBy.iterator(); it.hasNext();) {
                if (it.hasNext()) {
                    if (++cnt == this.queryGroupBy.size()) {
                        joined += " " + it.next();
                    } else {
                        joined += " " + it.next() + ",";
                    }
                }
            }
        }
        
        //having

        if (!queryOrderBy.isEmpty()) {
            joined += " ORDER BY";
            int cnt = 0;
            for (Iterator<String> it = this.queryOrderBy.iterator(); it.hasNext();) {
                if (it.hasNext()) {
                    if (++cnt == this.queryOrderBy.size()) {
                        joined += " " + it.next();
                    } else {
                        joined += " " + it.next() + ",";
                    }
                }
            }
        }
        return joined + ";";
    }

    public void reset(AnchorPane root) {
        this.cmpsSelecionados = new ArrayList();
        this.queryInit = "SELECT";
        this.queryFields = "*";
        this.queryFrom = "";
        this.queryJoins = new ArrayList();
        this.wheres = new ArrayList();
        this.queryWhere = "";
        this.queryGroupBy = new HashSet<>();
        this.queryOrderBy = new HashSet<>();
        this.controladores.forEach((t) -> {
            root.getChildren().remove(t.getPane());
            t.getLines().forEach((l) -> {
                root.getChildren().remove(l);
            });
        });
        this.controladores = new ArrayList<>();
    }

    public String getDb() {
        return dbdao.getDb();
    }

    public List<ItemTabela> getFields(String table) throws DAOException, SQLException {
        return dbdao.getFields(table);
    }

    public List<String> getTableNames(String table) throws DAOException, SQLException {
        return dbdao.getTableNames(table);
    }

    public List<ItemTabela> getDataBases() throws DAOException, SQLException {
        return dbdao.getDataBases();
    }

    public String getKeyPairs(String t1, String t2) throws DAOException, SQLException {
        return dbdao.getKeyPairs(t1, t2);
    }

    public List<List<String>> testarConsulta(String cslt) throws DAOException, SQLException {
        return dbdao.testarConsulta(cslt);
    }

}
