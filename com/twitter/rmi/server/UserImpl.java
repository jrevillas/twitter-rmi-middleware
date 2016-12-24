package com.twitter.rmi.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.twitter.rmi.common.Client;
import com.twitter.rmi.common.Status;
import com.twitter.rmi.common.User;
import com.twitter.rmi.database.Database;

import static com.twitter.rmi.server.ServerLauncher.*;

/**
 * Created by jruiz on 06/12/2016.
 */
public class UserImpl extends UnicastRemoteObject implements User {

    private String handle;
    private String password;
    private String bio;
    private boolean verified;

    public UserImpl() throws RemoteException {
        super();
    }

    @Override
    public String getBio() throws RemoteException {
        return bio;
    }

    @Override
    public void setBio(String bio) throws RemoteException {
        this.bio = bio;
        Database.addBio(this.handle, bio);
    }

    @Override
    public boolean getVerified() throws RemoteException{
        return verified;
    }

    public UserImpl setVerified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    @Override
    public String getHandle() throws RemoteException {
        return handle;
    }

    public UserImpl setHandle(String handle) throws RemoteException {
        this.handle = handle;
        return this;
    }

    public String getPassword() throws RemoteException {
        return this.password;
    }

    public UserImpl setPassword(String password) throws RemoteException {
        this.password = password;
        return this;
    }

    @Override
    public List<Status> getTimeline() throws RemoteException {
        List<Status> result = Database.getTimeline(this.getHandle());
        return result;
    }

    @Override
    public void submitStatus(String content) throws RemoteException {
        Database.submitStatus(this.handle, content);
        statusNotification(this.handle,content);
        return;
    }

    @Override
    public void updatePassword(String password) throws RemoteException {
        return;
    }

    @Override
    public void follow(String user) throws RemoteException {
        Database.follow(this, user);
        followNotificacion(user,handle);
        return;
    }

    @Override
    public void unfollow(String user) throws RemoteException {
        Database.unfollow(this, user);
        return;
    }

    @Override
    public List<String> getFollowers(String user) throws RemoteException {
        return Database.getFollowers(user);
    }

    @Override
    public List<String> getFollowing(String user) throws RemoteException {
        return Database.getFollowing(user);
    }

    @Override
    public List<User> getUsers() throws RemoteException {
        return Database.getUsers();
    }

    @Override
    public void registerForCallback(Client callbackClientObject) throws RemoteException {

        if(!callbackHashMap.containsKey(getHandle())){
            callbackHashMap.put(getHandle(),callbackClientObject);
            System.out.println("@" + getHandle() + " se ha registrado en el Callback");
        }
    }

    @Override
    public void unregisterForCallback(Client callbackClientObject) throws RemoteException {

        if(callbackHashMap.remove(getHandle(),callbackClientObject)){
            System.out.println("@" + getHandle() + " se ha desregistrado para el Callback");
        } else {
            System.out.println("Fallo: Cliente no registrado: " + getHandle());
        }
    }

    @Override
    public void submitPm(String content, String receiver) throws RemoteException {
        Database.submitPm(this.getHandle(), content, receiver);
    }

    @Override
    public List<PrivateMessage> getSentPM() throws RemoteException {
        return Database.getSentPM(this.getHandle());
    }

    @Override
    public List<PrivateMessage> getReceivedPM() throws RemoteException {
        return Database.getReceivedPM(this.getHandle());
    }
}
