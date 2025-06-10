package com.agentbuilder.security.models;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.agentbuilder.security.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
	@Id
	private String id;
	private String username;
	private String password; // BCrypt-hashed
	private Set<Role> roles;

	/* UserDetails contract */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name())).toList();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
