package br.com.AutoStock.event;

import lombok.Getter;
import lombok.Setter;

import org.springframework.context.ApplicationEvent;

import br.com.AutoStock.model.User;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private  User user;
    private String confirmationUrl;
    
    public RegistrationCompleteEvent(User user, String confirmationUrl) {
        super(user);
        this.user = user;
        this.confirmationUrl = confirmationUrl;
    }
}