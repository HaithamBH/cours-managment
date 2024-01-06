package com.hichoma.coursManagement;

import com.hichoma.coursManagement.models.Session;
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
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class SessionViewController implements Initializable {
    @FXML
    private Button teacherBtn;
    @FXML
    private Button sessionBtn;
    @FXML
    private Button classesBtn;
    @FXML
    private ChoiceBox classChoiceBox;
    @FXML
    private ChoiceBox dayChoiceBox;
    @FXML
    private ChoiceBox hourChoiceBox;
    @FXML
    private ChoiceBox matterChoiceBox;
    @FXML
    private ChoiceBox teacherIDChoiceBox;
    @FXML
    private TableView sessionTableView;
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

    private Connection conn = Database.getConn();

    private ObservableList<Session> data;
    private ArrayList<option> classesData;
    private ArrayList<option> mattersData;
    class option {
        private String ID;
        private String Name;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sessionBtn.setDisable(true);

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
        // setup on row click
        sessionTableView.setRowFactory( tv -> {
            TableRow<Session> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                Session rowData = row.getItem();
                onRowClick(rowData);
            });
            return row;
        });

        // column relate with class value
        classColumn.setCellValueFactory(new PropertyValueFactory<>("ClassName"));
        matterColumn.setCellValueFactory(new PropertyValueFactory<>("MatterName"));
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("Day"));
        hourColumn.setCellValueFactory(new PropertyValueFactory<>("Hour"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<>("TeacherID"));

        // set days options
        ObservableList<String> days = FXCollections.observableArrayList
                ("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
        dayChoiceBox.setItems(days);

        ObservableList<String> hours = FXCollections.observableArrayList
                ("1st Hour", "2nd Hour", "3rd Hour", "4th Hour", "5th Hour", "6th Hour", "8th Hour"
                        , "9th Hour","1-2nd hours", "3-4th hours", "5-6th hours", "7-8th hours");
        hourChoiceBox.setItems(hours);

        Statement statement = null;

        // get teacher id list
        ObservableList<String> teacherIdList = FXCollections.observableArrayList();
        try{
            statement = conn.createStatement();
            String query = "SELECT id FROM teacher";
            ResultSet result = statement.executeQuery(query);
            while (result.next()){
                teacherIdList.add(result.getString("id"));
            }
            teacherIDChoiceBox.setItems(teacherIdList);
        } catch (SQLException exception) {
            System.out.println("fetching teachers failed");
        }

        // fetch classes names list
        ObservableList<String> classesList = FXCollections.observableArrayList();
        classesData = new ArrayList<>();
        try{
            statement = conn.createStatement();
            String query = "SELECT * FROM class";
            ResultSet result = statement.executeQuery(query);
            while (result.next()){
                classesList.add(result.getString("name"));
                option option = new option();
                option.setID(result.getString("id"));
                option.setName(result.getString("name"));

                classesData.add(option);
            }
            classChoiceBox.setItems(classesList);
        } catch (SQLException exception) {
            System.out.println("fetching teachers failed");
        }

        // fetch matters list
        ObservableList<String> mattersList = FXCollections.observableArrayList();
        mattersData = new ArrayList<>();
        try{
            statement = conn.createStatement();
            String query = "SELECT * FROM matter";
            ResultSet result = statement.executeQuery(query);
            while (result.next()){
                mattersList.add(result.getString("name"));
                option option = new option();
                option.setID(result.getString("id"));
                option.setName(result.getString("name"));

                mattersData.add(option);
            }
            matterChoiceBox.setItems(mattersList);
        } catch (SQLException exception) {
            System.out.println("fetching teachers failed");
        }

        // fetch table view data
        data = FXCollections.observableArrayList();
        try{
            statement = conn.createStatement();
            String query = """
                    SELECT class.name as className, matter.name as matterName, day, hour, teacher.id as teacherId
                    FROM (((session 
                    INNER JOIN class ON session.classId = class.id)
                    INNER JOIN matter ON session.matterId = matter.id)
                    INNER JOIN teacher ON session.teacherId = teacher.id);
                    """;
            ResultSet result = statement.executeQuery(query);

            while(result.next()){
                //Iterate Row
                Session row = new Session();
                row.setClassName(result.getString("className"));
                row.setMatterName(result.getString("matterName"));
                row.setDay(result.getString("day"));
                row.setHour(result.getString("hour"));
                row.setTeacherID(result.getString("teacherId"));

                //Iterate Column

                data.add(row);
            }
            statement.close();
            sessionTableView.setItems(data);
        } catch (SQLException exception) {
            System.out.println("session find failed");
        }
    }

    private void onRowClick(Session session) {
        classChoiceBox.setValue(session.getClassName());
        matterChoiceBox.setValue(session.getMatterName());
        dayChoiceBox.setValue(session.getDay());
        hourChoiceBox.setValue(session.getHour());
        teacherIDChoiceBox.setValue(session.getTeacherID());
    }

    @FXML
    private void onCreate() {
        String classValue = (String) classChoiceBox.getValue();
        String dayValue = (String) dayChoiceBox.getValue();
        String hourValue = (String) hourChoiceBox.getValue();
        String matterValue = (String) matterChoiceBox.getValue();
        String teacherIdValue = (String) teacherIDChoiceBox.getValue();

        if (classValue != null && dayValue != null && hourValue != null && matterValue != null && teacherIdValue != null) {
            Optional<option> classObj = classesData.stream().filter(c -> Objects.equals(c.Name, classValue)).findFirst();
            Optional<option> matterObj = mattersData.stream().filter(c -> Objects.equals(c.Name, matterValue)).findFirst();
            Statement statement = null;
            try{
                statement = conn.createStatement();
                String query = String.format(
                        "INSERT INTO session ( day, hour, teacherId, matterId, classId ) VALUES ( '%s', '%s', %s, %s, %s )"
                        , dayValue, hourValue, teacherIdValue, matterObj.get().ID, classObj.get().ID);
                statement.executeUpdate(query);

                Session session = new Session();
                session.setClassName(classValue);
                session.setMatterName(matterValue);
                session.setDay(dayValue);
                session.setHour(hourValue);
                session.setTeacherID(teacherIdValue);
                data.add(session);
                sessionTableView.setItems(data);
            } catch (SQLException exception){
                System.out.println("session create failed");
            }
        }
    }

    @FXML
    private void onSearch() {
        String classValue = (String) classChoiceBox.getValue();
        String dayValue = (String) dayChoiceBox.getValue();
        String hourValue = (String) hourChoiceBox.getValue();
        String matterValue = (String) matterChoiceBox.getValue();
        String teacherIdValue = (String) teacherIDChoiceBox.getValue();
        try{
            Statement statement = conn.createStatement();
            String query = """
                    SELECT class.name as className, matter.name as matterName, day, hour, teacher.id as teacherId
                    FROM (((session 
                    INNER JOIN class ON session.classId = class.id)
                    INNER JOIN matter ON session.matterId = matter.id)
                    INNER JOIN teacher ON session.teacherId = teacher.id)
                    WHERE class.name = class.name"""
                    + (classValue == null ? "" : String.format(" AND class.name = '%s'", classValue))
                    + (dayValue == null ? "" : String.format(" AND day = '%s'", dayValue))
                    + (hourValue == null ? "" : String.format(" AND hour = '%s'", hourValue))
                    + (matterValue == null ? "" : String.format(" AND matter.name = '%s'", matterValue))
                    + (teacherIdValue == null ? ";" : String.format(" AND teacher.id = %s;", teacherIdValue));
            ResultSet result = statement.executeQuery(query);
            data = FXCollections.observableArrayList();
            while(result.next()){
                //Iterate Row
                Session row = new Session();
                row.setClassName(result.getString("className"));
                row.setMatterName(result.getString("matterName"));
                row.setDay(result.getString("day"));
                row.setHour(result.getString("hour"));
                row.setTeacherID(result.getString("teacherId"));


                //Iterate Column

                data.add(row);
            }
            statement.close();
            sessionTableView.setItems(data);
        } catch (SQLException exception) {
            System.out.println("teacher find failed");
        }
    }

    @FXML
    private void onDelete() {
        String classValue = (String) classChoiceBox.getValue();
        String dayValue = (String) dayChoiceBox.getValue();
        String hourValue = (String) hourChoiceBox.getValue();
        String matterValue = (String) matterChoiceBox.getValue();
        String teacherIdValue = (String) teacherIDChoiceBox.getValue();

        if (classValue != null && dayValue != null && hourValue != null && matterValue != null && teacherIdValue != null) {
            Optional<option> classObj = classesData.stream().filter(c -> Objects.equals(c.Name, classValue)).findFirst();
            Optional<option> matterObj = mattersData.stream().filter(c -> Objects.equals(c.Name, matterValue)).findFirst();

            try {
                Statement statement = conn.createStatement();
                String query = "DELETE FROM session WHERE session.id = session.id"
                        + (String.format(" AND classid = %s", classObj.get().ID))
                        + (String.format(" AND day = '%s'", dayValue))
                        + (String.format(" AND hour = '%s'", hourValue))
                        + (matterObj.isEmpty() ? "" : String.format(" AND matterid = %s", matterObj.get().ID))
                        + (String.format(" AND teacherid = %s;", teacherIdValue));

                statement.executeUpdate(query);
                statement.close();
                for (int i = 0 ; i< data.size(); i++ ){
                    Session row = data.get(i);
                    if (
                            Objects.equals(dayValue, row.getDay())
                                    && Objects.equals(classValue, row.getClassName())
                                    && Objects.equals(hourValue, row.getHour())
                                    && Objects.equals(matterValue, row.getMatterName())
                                    && Objects.equals(teacherIdValue, row.getTeacherID())
                    ) {
                        data.remove(i);
                        break;
                    }
                }
                sessionTableView.setItems(data);
            } catch (SQLException exception) {
                System.out.println("delete session failed");
            }
        }
    }
}
