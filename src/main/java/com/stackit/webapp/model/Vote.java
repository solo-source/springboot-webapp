package com.stackit.webapp.model;

import java.time.OffsetDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.stackit.webapp.enums.VoteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="votes",
uniqueConstraints=@UniqueConstraint(
  name="uk_user_answer_vote",
  columnNames={"user_id","answer_id"}))
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VoteType type;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // --- Constructors ---
    public Vote() {
    }

    public Vote(Long id, Answer answer, User user, VoteType type, OffsetDateTime createdAt) {
        this.id = id;
        this.answer = answer;
        this.user = user;
        this.type = type;
        this.createdAt = createdAt;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public User getUser() {
        return user;
    }

    public VoteType getType() {
        return type;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setType(VoteType type) {
        this.type = type;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}