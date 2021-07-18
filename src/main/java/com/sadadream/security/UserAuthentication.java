package com.sadadream.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.sadadream.domain.Role;

public class UserAuthentication extends AbstractAuthenticationToken {
	private final Long userId;
	public UserAuthentication(Long userId, List<Role> roles) {
		super(authorities(userId, roles));
		this.userId = userId;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.userId;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	public Long getUserId() {
		return this.userId;
	}

	@Override
	public String toString() {
		return "Authentication: userID -> (" + userId + ")";
	}

	private static List<GrantedAuthority> authorities(Long userId, List<Role> roles) {
		return roles.stream()
			.map(role -> new SimpleGrantedAuthority(role.getRole()))
			.collect(Collectors.toList());
	}
}
