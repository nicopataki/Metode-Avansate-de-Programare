package com.example.guiex1.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Friendship extends com.example.guiex1.domain.Entity<Long> {
    private LocalDateTime date;
    private Long user1;
    private Long user2;
    //private FriendshipStatus status;

    public Friendship(Long user1, Long user2, LocalDateTime date) {
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getIdUser1() {
        return user1;
    }

    public void setUser1(Long user1) {
        this.user1 = user1;
    }

    public Long getIdUser2() {
        return user2;
    }

    public void setUser2(Long user2) {
        this.user2 = user2;
    }

    private String formatter(LocalDateTime time){
        String time_print;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        time_print=time.format(formatter);
        return time_print;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(date, that.date) && Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, user1, user2);
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + date +
                ", user1=" + user1 +
                ", user2=" + user2 +
                '}';
    }
}
