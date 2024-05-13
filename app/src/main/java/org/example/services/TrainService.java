package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.entities.Train;

import javax.crypto.spec.PSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private static final String TRAIN_PATH="app/src/main/java/org/example/localDB/trains.json";

    private ObjectMapper objectMapper=new ObjectMapper();

    private List<Train>trainList;

    public TrainService() throws IOException{
        loadTrains();
    }

    public void loadTrains() throws IOException {
        File trains=new File(TRAIN_PATH);
        trainList=objectMapper.readValue(trains, new TypeReference<List<Train>>() {
        });
    }
    public List<Train>searchTrains(String src,String dest){
        return trainList.stream().filter(train->validTrain(train,src,dest)).collect(Collectors.toList());
    }
    private Boolean validTrain(Train train,String src,String dest){
        List<String>stationOrder= train.getStations();
        int srcind=stationOrder.indexOf(src.toLowerCase());
        int destind=stationOrder.indexOf(dest.toLowerCase());
        return srcind!=-1 && destind!=-1 && srcind<destind;
    }
    public void addTrain(Train train){
        Optional<Train>existingTrain=trainList.stream().filter(t->t.getTrainId().equals(train.getTrainId())).findFirst();
        if(existingTrain.isPresent()){
            updateTrain(train);
        }
        else{
            trainList.add(train);
            saveTrainListToFile();
        }
    }
    public void updateTrain(Train train){
        int index=-1;
        for(int i=0;i< trainList.size();i++){
            if(trainList.get(i).getTrainId().equalsIgnoreCase(train.getTrainId())){
                index=i;
                break;
            }
        }
        if(index!=-1){
            trainList.set(index,train);
            saveTrainListToFile();
        }
        else{
            addTrain(train);
        }
    }
    private void saveTrainListToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception based on your application's requirements
        }
    }
}
