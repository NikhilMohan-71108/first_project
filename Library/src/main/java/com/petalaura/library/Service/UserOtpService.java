package com.petalaura.library.Service;

import com.petalaura.library.model.UserOtp;

import java.util.List;

public interface UserOtpService {
 public void saveOrUpdate(UserOtp userOTP);
 public boolean existsByEmail(String email);
 public UserOtp findByEmail(String email);
}
