package com.example.guiex1.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    private Long id;
    private Utilizator from;
    private String message;
    private LocalDateTime data;
    private List<Utilizator> to;
    private Message reply;

    public Message(Utilizator from, List<Utilizator> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;

        this.reply = null;
    }

    public Message(Utilizator from, List<Utilizator> to, String message) {
        this.to = to;
        this.from = from;
        this.message = message;
        this.data = LocalDateTime.now();
        this.reply = null;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Utilizator getFrom() {
        return from;
    }

    public void setFrom(Utilizator from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public void setTo(List<Utilizator> to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", to=" + to +
                ", reply=" + reply +
                '}';
    }
}
