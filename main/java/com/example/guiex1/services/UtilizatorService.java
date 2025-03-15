package com.example.guiex1.services;



import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.FriendshipDataBase;
import com.example.guiex1.utils.events.ChangeEventType;
import com.example.guiex1.utils.events.Event;
import com.example.guiex1.utils.events.FriendshipEntityChangeEvent;
import com.example.guiex1.utils.events.UtilizatorEntityChangeEvent;
import com.example.guiex1.utils.observer.Observable;
import com.example.guiex1.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UtilizatorService implements Observable<UtilizatorEntityChangeEvent> {
    private Repository<Long, Utilizator> repo;
    private Repository<Long, Friendship> repoFriendship;
    private Repository<Long, FriendshipRequest> repoRequests;
    private Repository<Long, Message> messageRepository;
    private List<Observer<UtilizatorEntityChangeEvent>> observers=new ArrayList<>();

    public UtilizatorService(Repository<Long, Utilizator> repo, Repository<Long, Friendship> repoFriendship, Repository<Long, FriendshipRequest> repoRequests, Repository<Long, Message> messageRepository) {
        this.repo = repo;
        this.repoFriendship = repoFriendship;
        this.repoRequests = repoRequests;
        this.messageRepository = messageRepository;
    }

    public Utilizator findByName(String fullName) {
        Iterable<Utilizator> allUsers = repo.findAll();
        return StreamSupport.stream(allUsers.spliterator(), false)
                .filter(user -> (user.getFirstName() + " " + user.getLastName()).equalsIgnoreCase(fullName))
                .findFirst()
                .orElse(null);
    }

    public Utilizator getUserByEmail(String email){
        Iterable<Utilizator> all = repo.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .filter(user -> (user.getUsername().equals(email)))
                .findFirst()
                .orElse(null);
    }

    public Utilizator addUtilizator(Utilizator user) {
        if(repo.save(user).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }

    public Utilizator findUser(long id) {
        return repo.findOne(id).orElseThrow(() -> new ValidationException("No user"));
    }

    public Iterable<Friendship> getFriendships() {
        return repoFriendship.findAll();
    }

    public List<Utilizator> getListFriends(Utilizator user){
        List<Utilizator> friends = new ArrayList<>();
        getFriendships().forEach(friendship -> {
            if(friendship.getIdUser1().equals(user.getId())){
                friends.add(findUser(friendship.getIdUser2()));
            }else if(friendship.getIdUser2().equals(user.getId())){
                friends.add(findUser(friendship.getIdUser1()));
            }
        });
        return friends;
    }

    public Utilizator removeFriendship(Long id1, Long id2){
        Utilizator user1 = repo.findOne(id1).orElseThrow(() -> new ValidationException("User with id " + id1 + "doesn't exist!"));
        Utilizator user2 = repo.findOne(id2).orElseThrow(() -> new ValidationException("User with id " + id2 + "doesn't exist!"));

        Long id = 0L;
        for(Friendship f: repoFriendship.findAll()){
            if((f.getIdUser1().equals(id1) && f.getIdUser2().equals(id2)) || (f.getIdUser1().equals(id2) && f.getIdUser2().equals(id1))) {
                id = f.getId();
            }
        }
        if(id == 0L)
            throw new IllegalArgumentException("Friendship not found");
        Optional<Friendship> friendship= repoFriendship.delete(id);

        if (friendship.isPresent()) {
            user1.removeFriend(user2);
            user2.removeFriend(user1);

            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, user2));
            return user2;
        }

        return null;
    }

    public Utilizator deleteUtilizator(Long id){
        Optional<Utilizator> user=repo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }

    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }



    @Override
    public void addObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UtilizatorEntityChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UtilizatorEntityChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }


    public Utilizator updateUtilizator(Utilizator u) {
        Optional<Utilizator> oldUser=repo.findOne(u.getId());
        if(oldUser.isPresent()) {
            Optional<Utilizator> newUser=repo.update(u);
            if (newUser.isEmpty())
                notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }

    public Iterable<FriendshipRequest> getRequests() {
        return repoRequests.findAll();
    }

    public List<Utilizator> getListRequests(Utilizator user){
        List<Utilizator> requests = new ArrayList<>();
        getRequests().forEach(request -> {
            if(request.getUser2().equals(user.getId())){
                requests.add(findUser(request.getUser1()));
            }
        });
        return requests;
    }

    public List<FriendshipRequest> getLRequests(Utilizator user){
        List<FriendshipRequest> requests = new ArrayList<>();
        getRequests().forEach(request -> {
            if(request.getUser2().equals(user.getId())){
                requests.add(request);
            }
        });
        return requests;
    }

    public void sendFriendRequest(Long senderId, Long receiverId){
        Iterable<FriendshipRequest> existingRequests = repoRequests.findAll();
        FriendshipRequest newrequest = new FriendshipRequest(senderId, receiverId, LocalDateTime.now(), FriendshipStatus.PENDING);
        repoRequests.save(newrequest);
        notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.ADAUGA, findUser(receiverId)));
    }

    public FriendshipRequest getRequest(Long idSender, Long idRecever){
        Iterable<FriendshipRequest> requests = repoRequests.findAll();
        return StreamSupport.stream(requests.spliterator(), false)
                .filter(request -> (request.getUser1().equals(idSender) && request.getUser2().equals(idRecever)))
                .findFirst()
                .orElse(null);
    }

    public FriendshipRequest updateStatus(FriendshipRequest request, FriendshipStatus status){
        Optional<FriendshipRequest> req = repoRequests.findOne(request.getId());
        request.setStatus(status);
        if(req.isPresent()) {
            Optional<FriendshipRequest> newRequest=repoRequests.update(request);
            return newRequest.orElse(null);
        }
        return req.orElse(null);
    }

    public Friendship addFriend(Friendship friendship){
        Utilizator user = findUser(friendship.getIdUser2());
        if(repoFriendship.save(friendship).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return friendship;
    }


    public List<String> searchByName(String name){
        Iterable<Utilizator> allUsers = repo.findAll();
        return StreamSupport.stream(allUsers.spliterator(), false)
                .filter(user -> user.getFirstName().toLowerCase().contains(name.toLowerCase()) ||
                        user.getLastName().toLowerCase().contains(name.toLowerCase()))
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .collect(Collectors.toList());
    }

    public ArrayList<Message> getAllMessages(Long id1, Long id2){
        Utilizator user1 = findUser(id1);
        Utilizator user2 = findUser(id2);

        Collection<Message> messages = (Collection<Message>) messageRepository.findAll();
        return messages.stream()
                .filter(msg -> ((msg.getFrom().getId().equals(id1)) && msg.getTo().get(0).getId().equals(id2)) ||
                        (msg.getFrom().getId().equals(id2) && msg.getTo().get(0).getId().equals(id1)))
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Iterable<Message> getMessages(){
        return messageRepository.findAll();
    }

    public void addMessage(Message message){
        messageRepository.save(message);
    }
}
