package org.example.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.example.entities.Ticket;
import org.example.entities.Train;
import org.example.entities.User;
import java.io.File;
import java.io.IOException;

import org.example.utils.UserServiceUtil;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.spi.ToolProvider.findFirst;

public class UserBookingService {
    private User user;
    private static final String USERS_PATH="app/src/main/java/org/example/localDB/users.json";
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<User>userList;
    public UserBookingService(User user) throws IOException {
        this.user=user;
        loadUsers();
    }
    public UserBookingService() throws IOException{
        loadUsers();
    }
    public void loadUsers() throws IOException{
        File users= new File(USERS_PATH);
        userList=objectMapper.readValue(users, new TypeReference<List<User>>() {
        });
    }
    public Boolean loginUser(){
        Optional<User> foundUser=userList.stream().filter(
                user1->user1.getName().equalsIgnoreCase(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword())
        ).findFirst();
        return foundUser.isPresent();
    }
    public Boolean signUpUser(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }
        catch (IOException e){
            return Boolean.FALSE;
        }
    }
    private void saveUserListToFile() throws IOException{
        File usersFile =new File(USERS_PATH);
        objectMapper.writeValue(usersFile,userList);
    }
    public void fetchBooking(){
        user.printTickets();
    }
    public Boolean cancelBooking(String ticketId){
        for(Ticket ticket:user.getTicketsBooked()){
            if(ticket.getTicketId().equals(ticketId)){
                user.getTicketsBooked().remove(ticket);
                try{
                    saveUserListToFile();
                    return Boolean.TRUE;
                }
                catch (IOException e){
                    e.printStackTrace();
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.FALSE;
    }
    public List<Train> getTrains(String src,String dest){
        try{
            TrainService trainService=new TrainService();
            return trainService.searchTrains(src,dest);
        }
        catch (IOException e){
            return new ArrayList<>();
        }
    }
    public List<List<Integer>>fetchSeats(Train train){
        return train.getSeats();
    }
    public Boolean bookTrainSeat(Train train,int row,int seat){
        try{
            TrainService trainService=new TrainService();
            List<List<Integer>>seats=train.getSeats();
            if(row>=0 && row<seats.size() && seat>=0 && seat<seats.get(row).size()){
                if(seats.get(row).get(seat)==0){
                    seats.get(row).set(seat,1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        catch (IOException e){
            return Boolean.FALSE;
        }
    }
}
