// Đường dẫn đến tệp JSON
const addressFilePath = "./assets/data/Address.json";

// Hàm tải dữ liệu từ tệp JSON
async function fetchAddressData() {
    try {
        const response = await fetch(addressFilePath);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        populateProvinces(data);
    } catch (error) {
        console.error("Error fetching address data:", error);
    }
}

// Hàm hiển thị danh sách tỉnh/thành phố
function populateProvinces(data) {
    const provinceSelect = document.getElementById("province");
    for (const province in data) {
        const option = document.createElement("option");
        option.value = province;
        option.textContent = province;
        provinceSelect.appendChild(option);
    }
    // Lưu dữ liệu vào một biến toàn cục để sử dụng cho danh sách quận/huyện
    window.addressData = data;
}

// Hàm cập nhật danh sách quận/huyện dựa trên tỉnh/thành phố được chọn
function updateDistricts() {
    const provinceSelect = document.getElementById("province");
    const districtSelect = document.getElementById("district");

    // Xóa các quận/huyện cũ
    districtSelect.innerHTML = '<option value="">Select District</option>';

    const selectedProvince = provinceSelect.value;
    if (selectedProvince && window.addressData[selectedProvince]) {
        window.addressData[selectedProvince].forEach(district => {
            const option = document.createElement("option");
            option.value = district;
            option.textContent = district;
            districtSelect.appendChild(option);
        });
    }
}

// Gọi hàm fetch dữ liệu khi tải trang
document.addEventListener("DOMContentLoaded", fetchAddressData);
