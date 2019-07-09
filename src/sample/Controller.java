package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    @FXML
    private TextField poidsText, gainText ,nbrText;
    @FXML
    private TextArea weightMax;
    @FXML
    private TableView<object> tableItem;
    @FXML
    private TableColumn<object, Integer> poidArea, gainArea , nbrArea;
    @FXML
    private Label sol ,objs;

    private int opt = 0;
    private int[] solObjects ;

    public ObservableList<object> data = FXCollections.observableArrayList(
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nbrArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Nbr"));
        gainArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Gain"));
        poidArea.setCellValueFactory(new PropertyValueFactory<object, Integer>("Poid"));
        tableItem.setItems(data);
    }

    /*
    Branch and Bound
    @FXML
    private void onCalcule(ActionEvent event) {
        if(!data.isEmpty() && weightMax.getText().length() > 0){
            object[] lots = new object[data.size()];
            for(int i = 0;i < data.size() ; i++)lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            for(int i = 0 ; i < lots.length;i++)
                for (int j = i ; j < lots.length ; j++)
                    if((lots[i].getGain()/lots[i].getPoid()) < (lots[j].getGain()/lots[j].getPoid()) )Permute (i,j,lots);
            for(int i = 0;i <lots.length;i++) lots[i].setNbr(weight/lots[i].getPoid());
            solObjects = new int[lots.length];
            Arrays.fill(solObjects,0);
            Test(0,lots,weight,0,new int[lots.length]);
            System.out.println(opt + "\n");
            sol.setText( " "+opt);
            for (int i = 0 ; i < solObjects.length ; i++)objs.setText(objs.getText()+lots[i].getPoid() + " " + lots[i].getGain()+ " " +solObjects[i]+" \n" );
        }
    }
    */

    @FXML
    private void onCalcule(ActionEvent event) {
        if(!data.isEmpty() && weightMax.getText().length() > 0){
            object[] lots = new object[data.size()];
            for(int i = 0;i < data.size() ; i++)lots[i] = data.get(i);
            int weight = Integer.parseInt(weightMax.getText());
            for(int i = 0 ; i < lots.length;i++)
                for (int j = i ; j < lots.length ; j++)
                    if((lots[i].getGain()/lots[i].getPoid()) < (lots[j].getGain()/lots[j].getPoid()) )Permute (i,j,lots);
            for(int i = 0;i <lots.length;i++) lots[i].setNbr(weight/lots[i].getPoid());
            solObjects = new int[lots.length];
            Arrays.fill(solObjects,0);
            for(int i = 0 ; i < lots.length ; i++){
                solObjects[i] += weight / lots[i].getPoid();
                opt += solObjects[i] * lots[i].getGain();
                weight -= solObjects[i] * lots[i].getPoid();
            }
            System.out.println(opt + "\n");
            sol.setText( " "+opt);
            for (int i = 0 ; i < solObjects.length ; i++)objs.setText(objs.getText()+lots[i].getPoid() + " " + lots[i].getGain()+ " " +solObjects[i]+" \n" );
        }
    }


    private void Permute(int i, int j, object[] lots) {
        object tmp = new object(0,lots[i].getPoid(),lots[i].getGain());
        lots[i].setPoid(lots[j].getPoid());
        lots[i].setGain(lots[j].getGain());
        lots[j].setPoid(tmp.getPoid());
        lots[j].setGain(tmp.getGain());

    }

    private void Test(int num,object[] lot, int poidsReste,int sol,int[] objects){
        if ( num < lot.length  ) {
            for(int i =  poidsReste/lot[num].getPoid() ; i >= 0  ; i--){
                //System.out.println(i + "opt " +opt+ "here  "+((float) sol + (((float) poidsReste/lot[num].getPoid()) * lot[num].getGain())));
                if( ((float)opt) < (float) sol + (((float) poidsReste/lot[num].getPoid()) * lot[num].getGain()) ){
                    objects[num] = i;
                    Test(num+1,lot,poidsReste - lot[num].getPoid() * i,sol+lot[num].getGain() * i,objects);
                } else {
                    break;
                }
            }
        } else{
            opt = sol;
            for (int i = 0 ; i < objects.length ; i++)solObjects[i] = objects[i];
        }
    }

    /*
    Programmation Dynamique
    @FXML
    private void onCalcule(ActionEvent event) {
        if(!data.isEmpty() && weightMax.getText().length() > 0){
            object[] lots = new object[data.size()];
            for(int i = 0;i < data.size() ; i++)lots[i] = data.get(i);
            int taille = 0;
            for(int i = 0 ; i < lots.length ; i++) taille += lots[i].getNbr();
            obj[] objects = new obj[taille];
            int count = 0;
            for(int i = 0 ; i < lots.length ; i ++)for(int j = 0 ; j < lots[i].getNbr() ; j ++){objects[count] = new obj(lots[i].getGain(),lots[i].getPoid());count++; }
            int w = Integer.parseInt(weightMax.getText());

            int[][] matrix = new int[objects.length][w+1];
            matrix[0][objects[0].getPoids()] = objects[0].getGain();
            for(int i = 1 ; i < objects.length ; i++)
            {
                for(int j = 1 ; j <= w ; j++)
                {
                    if (j >= objects[i].getPoids())
                    {

                        matrix[i][j] = Math.max(matrix[i-1][j],matrix[i-1][j-objects[i].getPoids()] + objects[i].getGain());
                    }
                    else{matrix[i][j] = matrix[i-1][j];}
                    if(matrix[i][j-1] > matrix[i][j])matrix[i][j] = matrix[i][j-1];
                }
            }
            sol.setText(" Solution: " + String.valueOf(matrix[objects.length - 1][w]));

            for(int i = 0 ; i < matrix.length ; i ++) {
                for (int j = 0; j < matrix[i].length; j++)
                    System.out.print(matrix[i][j]+ " ");
                System.out.println();
            }

            boolean[] isThere = new boolean[objects.length];
            Arrays.fill(isThere,false);
            int i = objects.length - 1;
            int j = w;
            while (matrix[i][j] != 0 && j > 0)
            {
                if(matrix[i][j] == matrix[i][j-1]){j--;continue;}
                if(i > 0)if(matrix[i][j] == matrix[i-1][j]){i--;continue;}
                j = j - objects[i].getPoids();
                isThere[i] = true;
                System.out.println(i);
                if(i > 0)i--;
            }
            objs.setText("objects");
            int[] many = new int[lots.length];
            Arrays.fill(many,0);
            for (int k = 0 ; k < objects.length ; k++)
            {
                if(isThere[k]){
                    for(int ii = 0 ; ii < lots.length ; ii++){
                        if( objects[k].equals(new obj(lots[ii].getGain(),lots[ii].getPoid())) && many[ii] < lots[ii].getNbr() ){
                            many[ii]++;
                            break;
                        }
                    }
                    //objs.setText(objs.getText() + " " + k);
                }
            }

            for(int k = 0 ; k < many.length ; k ++){
                if(k % 5 == 0) objs.setText(objs.getText() + "\n");
                objs.setText(objs.getText() + " " + k + "("+ many[k] +")");
            }
        }

    }
    */

    @FXML
    private void onAjouter(ActionEvent event) {
        data = tableItem.getItems();
        if ( poidsText.getText().length() > 0 && gainText.getText().length() > 0 && nbrText.getText().length() > 0 )
            data.add(
                    new object( Integer.parseInt(nbrText.getText()),Integer.parseInt(poidsText.getText()), Integer.parseInt(gainText.getText()))
            );
        System.out.println(data);
        tableItem.setItems(data);
        System.out.println(tableItem.getItems().toString());
        poidsText.setText("");
        gainText.setText("");
        nbrText.setText("");
    }


}
