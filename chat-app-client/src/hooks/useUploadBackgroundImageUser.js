import { useMutation } from 'react-query';
import axios from 'axios';
import { message } from 'antd'; // Import message từ antd
import { updateUserBackgroundImageRoute } from "../utils/APIRoutes";

const updateUser = async (userData) => {
    await axios.post(updateUserBackgroundImageRoute + `/${userData?.id}`, userData?.url);
    return true;
};

const useUploadBackgroundImageUser = () => {
    return useMutation(
        (data) => updateUser(data),
        {
            onSuccess: (data) => {
                message.success("Cập nhật thành công");
            },
            onError: (error) => {
                message.error("Cập nhật thất bại");
            },
            onSettled: (data, error) => {
                // Không cần xử lý sau khi mutation kết thúc
            }
        }
    );
};

export { useUploadBackgroundImageUser };
