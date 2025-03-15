package com.example.guiex1.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendshipRequest extends Entity<Long>{
    private Long user2;
    private Long user1;
    FriendshipStatus status;
    private LocalDateTime date;
    public FriendshipRequest(Long user1, Long user2, LocalDateTime date, FriendshipStatus status) {
        this.user1=user1;
        this.user2=user2;
        this.date = date;
        this.status=status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipRequest that = (FriendshipRequest) o;
        return Objects.equals(user2, that.user2) && Objects.equals(user1, that.user1) && status == that.status && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user2, user1, status, date);
    }

    @Override
    public String toString() {
        return "FriendshipRequest{" +
                "user2=" + user2 +
                ", user1=" + user1 +
                ", status=" + status +
                ", date=" + date +
                '}';
    }

    public Long getUser2() {
        return user2;
    }

    public void setUser2(Long user2) {
        this.user2 = user2;
    }

    public Long getUser1() {
        return user1;
    }

    public void setUser1(Long user1) {
        this.user1 = user1;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
