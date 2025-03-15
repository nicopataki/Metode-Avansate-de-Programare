package com.example.guiex1.domain;

import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
//import org.example.repository.memory.InMemoryRepository;

import java.util.Optional;

public class FriendshipValidator implements Validator<Friendship> {
    private Repository<Long, Utilizator> repo;
    public FriendshipValidator(Repository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    /**
     * Validates a friendship between two users
     * @param friendship the friendship to be validated
     * @throws ValidationException if the id does not exist
     */
    @Override
    public void validate(Friendship friendship) throws ValidationException {
        Optional<Utilizator> u1 = repo.findOne(friendship.getIdUser1());
        Optional<Utilizator> u2 = repo.findOne(friendship.getIdUser2());

        if(friendship.getIdUser1() == null || friendship.getIdUser2() == null) {
            throw new ValidationException(friendship.getIdUser1() + " " + friendship.getIdUser2() + " is not valid");
        }
        if(u1.isEmpty() || u2.isEmpty()) {
            throw new ValidationException("This id does not exist!");
        }
    }
}
