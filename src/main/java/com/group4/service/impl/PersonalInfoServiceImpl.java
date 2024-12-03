package com.group4.service.impl;

import com.group4.entity.AddressEntity;
import com.group4.entity.UserEntity;
import com.group4.model.AddressModel;
import com.group4.model.UserModel;
import com.group4.repository.AddressRepository;
import com.group4.repository.PersonalInfoRepository;
import com.group4.service.IPersonalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonalInfoServiceImpl implements IPersonalInfoService {

    @Autowired
    private PersonalInfoRepository repository;
    @Autowired
    private AddressRepository addressRepository;

    // Lấy thông tin cá nhân từ cơ sở dữ liệu
    public UserModel fetchPersonalInfo(Long userID) {
        Optional<UserEntity> userEntity = repository.findById(userID);
        if (userEntity != null) {
            // Đã có mối quan hệ OneToOne nên không cần gọi AddressRepository
            AddressEntity addressEntity = userEntity.get().getAddress(); // Địa chỉ sẽ được lấy trực tiếp từ userEntity
            AddressModel addressModel = null;
            if (addressEntity != null) {
                addressModel = new AddressModel(
                        addressEntity.getAddressID(),
                        addressEntity.getCountry(),
                        addressEntity.getProvince(),
                        addressEntity.getDistrict(),
                        addressEntity.getCommune(),
                        addressEntity.getOther()
                );
            }
            return new UserModel(
                    userEntity.get().getUserID(),
                    userEntity.get().getName(),
                    userEntity.get().getEmail(),
                    userEntity.get().getPassword(),
                    userEntity.get().getGender(),
                    userEntity.get().getPhone(),
                    userEntity.get().getRoleName(),
                    userEntity.get().isActive(),
                    addressModel
            );
        }
        return null;
    }

    // Lưu thông tin cá nhân vào cơ sở dữ liệu
    public boolean savePersonalInfo(UserModel userModel, Long userID) {
        try {
            if (userID == null) {
                // Nếu userID không tồn tại, không thể lưu
                return false;
            }

            // Lấy thông tin người dùng hiện tại từ DB
            Optional<UserEntity> existingUserEntity = repository.findById(userID);
            if (existingUserEntity == null) {
                // Nếu không tìm thấy người dùng, không thể lưu
                return false;
            }

            // Lấy các thông tin cần thiết
            String password = userModel.getPassword();
            if (password == null || password.isEmpty()) {
                password = existingUserEntity.get().getPassword(); // Giữ nguyên mật khẩu hiện tại
            }
            boolean isActive = existingUserEntity.get().isActive();
            String role = existingUserEntity.get().getRoleName();
            AddressModel addressModel = userModel.getAddress();
            AddressEntity addressEntity = existingUserEntity.get().getAddress();
            if (addressModel != null) {
                // Nếu có địa chỉ mới, cập nhật địa chỉ
                addressEntity = new AddressEntity(
                        addressEntity != null ? addressEntity.getAddressID() : null,
                        addressModel.getCountry(),
                        addressModel.getProvince(),
                        addressModel.getDistrict(),
                        addressModel.getCommune(),
                        addressModel.getOther()
                );
                addressRepository.save(addressEntity); // Lưu địa chỉ
            }

            // Tạo UserEntity từ UserModel
            UserEntity userEntity = repository.findById(userID).get();
            userEntity.setPassword(password);
            userEntity.setRoleName(role);
            userEntity.setEmail(userModel.getEmail());
            userEntity.setPhone(userModel.getPhone());
            userEntity.setAddress(addressEntity);
            userEntity.setName(userModel.getName());

            System.out.println("Test1" + userEntity);
            // Lưu hoặc cập nhật thông tin người dùng
            repository.save(userEntity);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public Optional<UserEntity> findUserById(Long userID) {
        return repository.findById(userID); // Directly return the user
    }
}
