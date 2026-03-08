package br.com.AutoStock.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	private Date expirationTime;
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
	
	public VerificationToken(String token, User user) {
		this.token = token;
		this.user = user;
		this.expirationTime = getAccountVerificationExpirationTime();
	}

	public static Date getPasswordResetExpirationTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5);
		return calendar.getTime();
	}

	public static Date getAccountVerificationExpirationTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 5); 
		return calendar.getTime();
	}
}
