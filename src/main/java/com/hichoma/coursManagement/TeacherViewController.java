package com.hichoma.coursManagement;

import com.hichoma.coursManagement.models.Teacher;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class TeacherViewController implements Initializable {
    @FXML
    private Button teacherBtn;
    @FXML
    private Button sessionBtn;
    @FXML
    private Button classesBtn;


    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField contactField;

    @FXML
    private Button saveBtn;

    @FXML
    private TableView teacherTableView;

    @FXML
    private TableColumn teacherId;

    @FXML
    private TableColumn teacherName;

    @FXML
    private TableColumn teacherContact;

    private Connection conn = Database.getConn();

    private ObservableList<Teacher> data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        teacherBtn.setDisable(true);

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

        classesBtn.setOnAction(event -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("classView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 900, 580);
                Stage stage = (Stage) ((Node) event.getTarget()).getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Statement statement = null;
        data = FXCollections.observableArrayList();
        teacherTableView.setRowFactory( tv -> {
            TableRow<Teacher> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Teacher rowData = row.getItem();
                onRowClick(rowData);
            });
            return row;
        });
        teacherId.setCellValueFactory(new PropertyValueFactory<>("ID"));
        teacherName.setCellValueFactory(new PropertyValueFactory<>("Name"));
        teacherContact.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        try{
            statement = conn.createStatement();
            String query = "SELECT * FROM teacher";
            ResultSet result = statement.executeQuery(query);

            while(result.next()){
                //Iterate Row
                Teacher row = new Teacher();

                //Iterate Column
                row.setID(result.getString("ID"));
                row.setName(result.getString("Name"));
                row.setContact(result.getString("Contact"));

                data.add(row);
            }
            statement.close();
            teacherTableView.setItems(data);
        } catch (SQLException exception) {
            System.out.println("teacher find failed");
        }
    }

    @FXML
    private void navToSessionView() throws IOException {

    }

    @FXML
    private void navToStudentView() {

    }

    private void onRowClick(Teacher row) {
        saveBtn.setDisable(true);
        idField.setText(row.getID());
        idField.setDisable(true);
        nameField.setText(row.getName());
        contactField.setText(row.getContact());
    }

    @FXML
    private void onClearField() {
        saveBtn.setDisable(false);
        idField.setText("");
        idField.setDisable(false);
        nameField.setText("");
        contactField.setText("");
    }

    @FXML
    private void onTeacherInsert() {
        if ( !nameField.getText().isEmpty() && !contactField.getText().isEmpty() ) {
            Statement statement = null;
            try{
                statement = conn.createStatement();
                String query = String.format("INSERT INTO teacher ( name, contact ) VALUES ( '%s', '%s' ) ", nameField.getText(), contactField.getText());
                int insertResult = statement.executeUpdate(query);

                String selectQuery = "SELECT * FROM teacher";
                ResultSet result = statement.executeQuery(selectQuery);

                data = FXCollections.observableArrayList();
                while(result.next()){
                    //Iterate Row
                    Teacher row = new Teacher();

                    //Iterate Column
                    row.setID(result.getString("ID"));
                    row.setName(result.getString("Name"));
                    row.setContact(result.getString("Contact"));

                    data.add(row);
                }
                teacherTableView.setItems(data);
            } catch (SQLException exception){
                System.out.println("teacher create failed");
            }
        }
    }

    @FXML
    private void onIdSearch() {
        if ( !idField.getText().isEmpty() ) {
            Statement statement = null;
            try{
                statement = conn.createStatement();
                String query = String.format("SELECT * FROM teacher WHERE id = %s", idField.getText() );
                ResultSet result = statement.executeQuery(query);
                data = FXCollections.observableArrayList();
                while(result.next()){
                    //Iterate Row
                    Teacher row = new Teacher();

                    //Iterate Column
                    row.setID(result.getString("ID"));
                    row.setName(result.getString("Name"));
                    row.setContact(result.getString("Contact"));

                    data.add(row);
                }
                statement.close();
                teacherTableView.setItems(data);
            } catch (SQLException exception){
                System.out.println("find teacher by id failed");
            }
        }
    }

    @FXML
    private void onTeacherUpdate() {
        if ( !idField.getText().isEmpty() ) {
            Statement statement = null;
            try{
                statement = conn.createStatement();
                String query = String.format(
                        "UPDATE teacher SET name = '%s', contact = '%s' WHERE id = %s",
                        nameField.getText(),
                        contactField.getText(),
                        idField.getText());
                int result = statement.executeUpdate(query);

                data.forEach((row) -> {
                    if (Objects.equals(row.getID(), idField.getText())) {
                        row.setName(nameField.getText());
                        row.setContact(contactField.getText());
                        int i = data.indexOf(row);
                        data.set(i, row);
                    }
                });
                statement.close();
                teacherTableView.setItems(data);
            } catch (SQLException exception){
                System.out.println("update teacher failed");
            }
        }
    }

    @FXML
    private void onTeacherDelete() {
        if ( !idField.getText().isEmpty() ) {
            try{
                Statement statement = conn.createStatement();
                String query = String.format(
                        "DELETE FROM teacher WHERE id = %s",
                        idField.getText());
                statement.executeUpdate(query);
                statement.close();
                for ( int i = 0; i< data.size(); i++){
                    Teacher row = data.get(i);
                    if (Objects.equals(row.getID(), idField.getText())) {
                        data.remove(i);
                        break;
                    }
                }
                teacherTableView.setItems(data);
            } catch (SQLException exception){
                System.out.println("delete teacher failed");
            }
        }
    }
}
