package com.sadadream.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {
	private final Long userId;
	public UserAuthentication(Long userId) {
		super(authorities());
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

	@Override
	public String toString() {
		return "Authentication: userID -> (" + userId + ")";
	}

	private static List<GrantedAuthority> authorities() {
		List<GrantedAuthority> authorities = new ArrayList<>();
		// 권한 처리를 할 수 있다.
		authorities.add(new SimpleGrantedAuthority("USER"));
		return authorities;
	}
}
