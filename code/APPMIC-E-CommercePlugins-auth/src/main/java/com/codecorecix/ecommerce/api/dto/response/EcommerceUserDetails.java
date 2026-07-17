package com.codecorecix.ecommerce.api.dto.response;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class EcommerceUserDetails implements UserDetails {


  private Long customerId;

  private String customerName;

  private Long employeeId;

  private String employeeName;

  private String username;

  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public EcommerceUserDetails(Long customerId, String customerName, Long employeeId, String employeeName, String username, String password,
                              Collection<? extends GrantedAuthority> authorities) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.employeeId = employeeId;
    this.employeeName = employeeName;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public Long getEmployeeId() {
    return employeeId;
  }

  public void setEmployeeId(Long employeeId) {
    this.employeeId = employeeId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getEmployeeName() {
    return employeeName;
  }

  public void setEmployeeName(String employeeName) {
    this.employeeName = employeeName;
  }
}
