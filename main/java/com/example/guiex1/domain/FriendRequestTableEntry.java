package com.example.guiex1.domain;
import com.example.guiex1.domain.FriendshipStatus;

import java.time.LocalDateTime;

public class FriendRequestTableEntry {
    private String firstName;
    private String lastName;
    private LocalDateTime since;
    private FriendshipStatus status;

    public FriendRequestTableEntry(String firstName, String lastName, LocalDateTime since, FriendshipStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.since = since;
        this.status = status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getSince() {
        return since;
    }

    public FriendshipStatus getStatus() {
        return status;
    }
}
