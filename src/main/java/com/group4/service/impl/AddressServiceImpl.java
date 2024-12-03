package com.group4.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.entity.AddressEntity;
import com.group4.model.AddressModel;
import com.group4.repository.AddressRepository;
import com.group4.repository.PersonalInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class AddressServiceImpl {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PersonalInfoRepository personalInfoRepository;
    // Đọc file Address.json và ánh xạ vào AddressModel
    public AddressModel loadAddressFromJson() {
        try {
            // Đọc dữ liệu từ file Address.json
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("src/main/Address.json");

            // Ánh xạ JSON vào AddressModel
            AddressModel addressModel = objectMapper.readValue(file, AddressModel.class);

            return addressModel;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Xử lý lỗi nếu có
        }
    }

    public boolean updateAddressForUser(AddressModel addressModel, Long addressID) {
        try {
            // Tìm AddressEntity trong database theo AddressID
            AddressEntity addressEntity = addressRepository.findById(addressID)
                    .orElse(new AddressEntity()); // Tạo mới nếu không tìm thấy

            // Cập nhật thông tin từ AddressModel vào AddressEntity
            addressEntity.setCountry(addressModel.getCountry());
            addressEntity.setProvince(addressModel.getProvince());
            addressEntity.setDistrict(addressModel.getDistrict());
            addressEntity.setCommune(addressModel.getCommune());
            addressEntity.setOther(addressModel.getOther());

            // Lưu AddressEntity vào cơ sở dữ liệu
            addressRepository.save(addressEntity);

            return true; // Cập nhật thành công
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Cập nhật thất bại
        }
    }
}
