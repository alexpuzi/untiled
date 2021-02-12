package sample;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;


import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

//отсюда действия связаны с sample.fxml
public class Controller implements Initializable {

    @FXML
    ListView<FileInfo> filesList;
    @FXML
    TextField pathField;
    Path root;
    Path selectedFile;
    public void menuItemFileAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void menuItemFileAction1(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       filesList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>() {
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item == null || empty){
                            setText(null);
                            setStyle("");
                        }else{
                            String formattedFilename = String.format("%-30s", item.getFilename());
                            String formattedFileLength = String.format("%,d bytes", item.getSize());
                            if(item.getSize() == -1L){
                                formattedFileLength = String.format("%s", "[ dir ]");
                            }
                            if(item.getSize() == -2L){
                                formattedFileLength = String.format("%s","[dir]");
                            }
                            String text = String.format("%s %-20s", formattedFilename, formattedFileLength);
                            setText(text);
//                            setText(item.getFilename());
                        }
                    }
                };
            }
        });
       goToPath(Paths.get("1"));
    }

    public void goToPath(Path path){
        root = path;
        pathField.setText(root.toAbsolutePath().toString());
        filesList.getItems().clear();//для текстового поля очищаем путь потом выводим путь в след. строке
        filesList.getItems().add(new FileInfo("[вверх]", -2L));
        filesList.getItems().addAll(scanFiles(path));
        filesList.getItems().sort(new Comparator<FileInfo>() { //делаем сортировку
            @Override
            public int compare(FileInfo o1, FileInfo o2) {
                if(o1.getFilename().equals(FileInfo.UP_TOKEN)){
                    return -1;
                }
                if ((int) Math.signum(o1.getSize()) == (int) Math.signum(o2.getSize())) {
                    return o1.getFilename().compareTo(o2.getFilename());
                }
                    return new Long(o1.getSize() - o2.getSize()).intValue();  
                }
//            }
        });
    }

    public List<FileInfo> scanFiles(Path root){
        try {
            return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Files scan exception: "+ root);
        }
    }

    public void filesListClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
            if(fileInfo != null){
                if(fileInfo.isDerecrory()){
                    Path pathTo = root.resolve(fileInfo.getFilename());
                    goToPath(pathTo);
                }
                if(fileInfo.isUpElement()){
                    Path pathTo = root.toAbsolutePath().getParent();
                    goToPath(pathTo);
                }
            }
        }
    }
    public  void copyAction(ActionEvent actionEvent){
        FileInfo fileInfo = filesList.getSelectionModel().getSelectedItem();
        if(fileInfo == null || fileInfo.isDerecrory() || fileInfo.isUpElement()){
            return ;
        }
        if(((Button) actionEvent.getSource()).getText().equals("copy")){
            selectedFile = root.resolve(fileInfo.getFilename());
        }
    }

    public void create(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {
    }
}

//переход дерева каталогов