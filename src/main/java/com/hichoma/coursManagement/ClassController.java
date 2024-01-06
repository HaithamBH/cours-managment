package com.hichoma.coursManagement;

import com.hichoma.coursManagement.models.TableResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ClassController implements Initializable {
    @FXML
    private Button teacherBtn;
    @FXML
    private Button sessionBtn;
    @FXML
    private Button classesBtn;

    @FXML
    private TextField classNameField;
    @FXML
    private ChoiceBox classIdChoiceBox;
    @FXML
    private ChoiceBox classNameChoiceBox;
    @FXML
    private ChoiceBox matterChoiceBox;
    @FXML
    private TableColumn idColumn;
    @FXML
    private TableColumn classColumn;
    @FXML
    private TableColumn matterColumn;
    @FXML
    private TableColumn dayColumn;
    @FXML
    private TableColumn hourColumn;
    @FXML
    private TableColumn teacherColumn;
    @FXML
    private TableColumn contactColumn;
    @FXML
    private TableView classTableView;

    private Connection conn = Database.getConn();
    private ObservableList<String> classNameData = FXCollections.observableArrayList();;
    private ObservableList<String> classIdData = FXCollections.observableArrayList();;
    private ObservableList<String> matterNameData = FXCollections.observableArrayList();;
    private ObservableList<TableResult> tableData = FXCollections.observableArrayList();;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        classesBtn.setDisable(true);

        teacherBtn.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("teacherView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 900, 580);
                Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        sessionBtn.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("sessionView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 900, 580);
                Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        idColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        classColumn.setCellValueFactory(new PropertyValueFactory<>("ClassName"));
        matterColumn.setCellValueFactory(new PropertyValueFactory<>("MatterName"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("Day"));
        hourColumn.setCellValueFactory(new PropertyValueFactory<>("Hour"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("Teacher"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("Contact"));

        Statement statement = null;
        try{
            statement = conn.createStatement();
            String classQuery = "SELECT * FROM class";
            ResultSet result = statement.executeQuery(classQuery);

            while( result.next() ){
                classIdData.add(result.getString("id"));
                classNameData.add(result.getString("name"));
            }
            classIdChoiceBox.setItems(classIdData);
            classNameChoiceBox.setItems(classNameData);

            String matterQuery = "SELECT name FROM matter";
            ResultSet matterResult = statement.executeQuery(matterQuery);

            while (matterResult.next()) {
                matterNameData.add(matterResult.getString("name"));
            }
            matterChoiceBox.setItems(matterNameData);

            String query = """
                    SELECT session.id as id, class.name as className, matter.name as matterName, day, hour, teacher.name as teacher, teacher.contact as contact
                    FROM (((session 
                    INNER JOIN class ON session.classId = class.id)
                    INNER JOIN matter ON session.matterId = matter.id)
                    INNER JOIN teacher ON session.teacherId = teacher.id);
                    """;
            ResultSet tableResult = statement.executeQuery(query);

            while(tableResult.next()){
                TableResult row = new TableResult();
                row.setID(tableResult.getString("id"));
                row.setClassName(tableResult.getString("className"));
                row.setMatterName(tableResult.getString("matterName"));
                row.setDay(tableResult.getString("day"));
                row.setHour(tableResult.getString("hour"));
                row.setTeacher(tableResult.getString("teacher"));
                row.setContact(tableResult.getString("contact"));
                tableData.add(row);
            }
            classTableView.setItems(tableData);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onCreate() throws SQLException {
        String nameValue = classNameField.getText();
        if ( !nameValue.isEmpty() ){
            Statement statement = conn.createStatement();
            String query = String.format("INSERT INTO class ( name ) VALUES ( '%s' )", nameValue);
            statement.executeUpdate(query);
            String classQuery = "SELECT * FROM class";
            ResultSet result = statement.executeQuery(classQuery);

            classIdData = FXCollections.observableArrayList();
            classNameData = FXCollections.observableArrayList();
            while( result.next() ){
                classIdData.add(result.getString("id"));
                classNameData.add(result.getString("name"));
            }
            classIdChoiceBox.setItems(classIdData);
            classNameChoiceBox.setItems(classNameData);

        }
    }

    @FXML
    private void onSearch(){
        String classValue = (String) classNameChoiceBox.getValue();
        String matterValue = (String) matterChoiceBox.getValue();

        if (classValue != null || matterValue != null){
            try {
                Statement statement = conn.createStatement();
                String query = """
                        SELECT session.id as id, class.name as className, matter.name as matterName, day, hour, teacher.name as teacher, teacher.contact as contact
                        FROM (((session
                        INNER JOIN class ON session.classId = class.id)
                        INNER JOIN matter ON session.matterId = matter.id)
                        INNER JOIN teacher ON session.teacherId = teacher.id)
                        WHERE session.id = session.id"""
                        + (classValue == null ? "" : String.format(" AND class.name = '%s'", classValue))
                        + (matterValue == null ? ";" : String.format(" AND matter.name = '%s';", matterValue));
                ResultSet tableResult = statement.executeQuery(query);
                tableData = FXCollections.observableArrayList();
                while (tableResult.next()) {
                    TableResult row = new TableResult();
                    row.setID(tableResult.getString("id"));
                    row.setClassName(tableResult.getString("className"));
                    row.setMatterName(tableResult.getString("matterName"));
                    row.setDay(tableResult.getString("day"));
                    row.setHour(tableResult.getString("hour"));
                    row.setTeacher(tableResult.getString("teacher"));
                    row.setContact(tableResult.getString("contact"));
                    tableData.add(row);
                }
                classTableView.setItems(tableData);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void onDelete() throws SQLException {
        String classIdValue = (String) classIdChoiceBox.getValue();
        if ( classIdValue != null ) {
            Statement statement = conn.createStatement();
            String query = String.format("DELETE FROM class WHERE id = %s", classIdValue);
            statement.executeUpdate(query);

            String classQuery = "SELECT * FROM class";
            ResultSet result = statement.executeQuery(classQuery);

            classIdData = FXCollections.observableArrayList();
            classNameData = FXCollections.observableArrayList();
            while( result.next() ){
                classIdData.add(result.getString("id"));
                classNameData.add(result.getString("name"));
            }
            classIdChoiceBox.setItems(classIdData);
            classNameChoiceBox.setItems(classNameData);
        }
    }
}
