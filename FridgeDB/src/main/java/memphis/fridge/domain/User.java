package memphis.fridge.domain;

import java.math.BigDecimal;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 29/09/12
 */
@Entity(name = "users")
public class User {

	@Id
	@NotNull
	@Size(max = 20)
	@Column(name = "username", length = 20, nullable = false)
	private String username;

	@NotNull
	@Size(max = 35)
	@Column(name = "password_md5", length = 35, nullable = false)
	private String password;

	@Nullable
	@Size(max = 60)
	@Column(name = "real_name", length = 60)
	private String realName;

	@Nullable
	@Size(max = 60)
	@Column(name = "email_address", length = 60, nullable = false)
	private String email;

	@NotNull
	@Column(name = "balance", precision = 9, scale = 2)
	private BigDecimal balance;

	@NotNull
	@Column(name = "isadmin", nullable = false)
	private boolean isAdmin;

	@NotNull
	@Column(name = "isgrad", nullable = false)
	private boolean isGrad;

	@Nullable
	@Size(max = 20)
	@Column(name = "sponsor", length = 20, nullable = true)
	private String sponsor;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@NotNull
	@Column(name = "isinterfridge", nullable = false)
	private boolean isInterfridge;

	@Nullable
	@Size(max = 20)
	@Column(name = "interfridge_password", length = 20, nullable = true)
	private String interfridgePassword;

	@Nullable
	@Column(name = "interfridge_endpoint", nullable = true)
	private String interfridgeEndpoint;

	@Nullable
	@Column(name = "fridgeserver_endpoint", nullable = true)
	private String fridgeServerEndpoint;

	User() {
	}

	public User(String username, String password, String realName, String email) {
		this.username = username;
		this.password = password;
		this.realName = realName;
		this.email = email;
		this.enabled = true;
		this.balance = BigDecimal.ZERO;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Nullable
	public String getRealName() {
		return realName;
	}

	public void setRealName(@Nullable String realName) {
		this.realName = realName;
	}

	@Nullable
	public String getEmail() {
		return email;
	}

	public void setEmail(@Nullable String email) {
		this.email = email;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public boolean isGrad() {
		return isGrad;
	}

	public void setGrad(boolean grad) {
		isGrad = grad;
	}

	public String getSponsor() {
		return sponsor;
	}

	public void setSponsor(String sponsor) {
		this.sponsor = sponsor;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInterfridge() {
		return isInterfridge;
	}

	public void setInterfridge(boolean interfridge) {
		isInterfridge = interfridge;
	}

	public String getInterfridgePassword() {
		return interfridgePassword;
	}

	public void setInterfridgePassword(String interfridgePassword) {
		this.interfridgePassword = interfridgePassword;
	}

	@Nullable
	public String getInterfridgeEndpoint() {
		return interfridgeEndpoint;
	}

	public void setInterfridgeEndpoint(@Nullable String interfridgeEndpoint) {
		this.interfridgeEndpoint = interfridgeEndpoint;
	}

	@Nullable
	public String getFridgeServerEndpoint() {
		return fridgeServerEndpoint;
	}

	public void setFridgeServerEndpoint(@Nullable String fridgeServerEndpoint) {
		this.fridgeServerEndpoint = fridgeServerEndpoint;
	}
}
