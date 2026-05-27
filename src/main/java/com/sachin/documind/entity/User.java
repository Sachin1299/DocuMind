package com.sachin.documind.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;
    
    @OneToOne
    @JoinColumn(name = "chat_history_id")
    private ChatHistory chatHistory;

    public User() {}

    public User(String name, String username, String email, String password, ChatHistory chatHistory) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.chatHistory = chatHistory;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AuthProvider getProvider() { return provider; }
    public void setProvider(AuthProvider provider) { this.provider = provider; }

    public ChatHistory getChatHistory() {
		return chatHistory;
	}

	public void setChatHistory(ChatHistory chatHistory) {
		this.chatHistory = chatHistory;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
