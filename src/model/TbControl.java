package model;

import dao.DbDAO;
import exception.DAOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;

/**
 *
 * @author Gill
 */
public class TbControl {

    String nome;
    String alias;
    Group root;//mudar
    DbDAO dbdao;//mudar para db control
    DbControl dbControl;
    TableView table;
    TitledPane pane;
    List<String> referencias;
    List<String> referenciadores;
    List<String> prikeys;
    List<String> fields = new ArrayList<>();
    List<Point2D> points;
    HashSet<MLine> lines = new HashSet<>();
    Map<String, Point2D> anchs = new HashMap<>();
    int instances = 1;

    public TbControl(String nome) {
        this.nome = nome;
        this.dbdao = new DbDAO("vqbuilder");//mudar
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getInstances() {
        return instances;
    }

    public TbControl setInstances(int instances) {
        this.instances = instances;
        return this;
    }

    public void loadInfoFromBd() throws DAOException, SQLException {
        this.referencias = dbdao.getRelations(nome);
        this.referenciadores = dbdao.getRelators(nome);
        this.prikeys = dbdao.getPriKeys(nome);
        for (ItemTabela item : dbControl.getFields(nome)) {
            this.fields.add(item.getNome());
        }
    }

    public List<String> getReferencias() throws DAOException, SQLException {
        return this.referencias;
    }

    public List<String> getRelatores() throws DAOException, SQLException {
        return this.referenciadores;
    }

    public List<String> getPriKey() throws DAOException, SQLException {
        return this.prikeys;
    }

    public void setRoot(Group root) {
        this.root = root;
    }

    public void setDbControl(DbControl dbControl) {
        this.dbControl = dbControl;
    }

    public void addLine(MLine l) {
        this.lines.add(l);
    }

    public HashSet<MLine> getLines() {
        return lines;
    }

    public void setLines(HashSet<MLine> lines) {
        this.lines = lines;
    }

    public Bounds getTableBounds() {
        return table.localToScene(table.getBoundsInLocal());
    }

    public Bounds getPaneBounds() {
        return pane.localToScene(pane.getBoundsInLocal());
    }

    public Point2D getAnchor(String name) {
        return anchs.get(name);
    }

    public void setAnchorsX() {
        Bounds bounds = getPaneBounds();
        anchs.put("top", new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY()));
        anchs.put("bottom", new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMaxY()));
        anchs.put("left", new Point2D(bounds.getMinX(), bounds.getMinY() + bounds.getHeight() / 2));
        anchs.put("right", new Point2D(bounds.getMaxX(), bounds.getMinY() + bounds.getHeight() / 2));
    }

    public void setLineAutoColors() throws DAOException, SQLException {
        lines.forEach((l) -> {
            l.setStroke(Color.CORAL);
        });
    }

    public void setLineColors() throws DAOException, SQLException {
        lines.forEach((l) -> {
            l.setStroke(Color.CORAL);
        });
    }

    public void updateLines() throws DAOException, SQLException {
        lines.forEach((l) -> {//separar começos de fins
            if (this.nome.equals(l.origin)) {
                Bounds bounds1 = dbControl.getCtrlByName(l.getOrigin()).getPaneBounds();
                l.setStartX(bounds1.getMinX() + bounds1.getWidth() / 2);
                l.setStartY(bounds1.getMinY() + bounds1.getHeight() / 2);
            } else {
                Bounds bounds2 = dbControl.getCtrlByName(l.getDestiny()).getPaneBounds();
                l.setEndX(bounds2.getMinX() + bounds2.getWidth() / 2);
                l.setEndY(bounds2.getMinY() + bounds2.getHeight() / 2);
            }
        });
    }

    public String getNome() {
        return nome;
    }

    public TableView getTabela() {
        return table;
    }

    public void setTabela(TableView tabela) {
        this.table = tabela;
    }

    public TitledPane getPane() {
        return pane;
    }

    public void setPane(TitledPane pane) {
        this.pane = pane;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<String> loadFields() throws DAOException, SQLException {
        if (this.fields.isEmpty()) {
            for (ItemTabela item : dbControl.getFields(this.nome)) {
                this.fields.add(item.getNome());
            }
        }
        return this.fields;
    }
}
