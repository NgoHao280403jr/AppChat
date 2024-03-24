package com.example.ChatApp.Services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.ChatApp.Config.Utility;
import com.example.ChatApp.Models.Message_groups;
import com.example.ChatApp.Models.Users;
import com.example.ChatApp.Models.Submodels.MessageGroup_User;
import com.example.ChatApp.Repositories.MessageGroupsRepository;
import com.example.ChatApp.Repositories.UsersRepository;
import com.example.ChatApp.dto.MessageGroupUpdateDto;
import com.example.ChatApp.dto.UserGroupDto;
import com.mongodb.client.result.UpdateResult;

import io.jsonwebtoken.io.IOException;

@Service
public class MessageGroupService {
	@Autowired
	private UsersRepository usersRepository;

	@Autowired
	private MessageGroupsRepository messageGroupsRepository;
	@Autowired
	private MongoTemplate mongoTemplate;
	public String root = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\";

	public List<UserGroupDto> getListMessGroupByUserID(String userID) throws Exception {

		ObjectId id = new ObjectId(userID);
		Optional<Users> users = usersRepository.findById(id);

		List<UserGroupDto> rs = new ArrayList<>();

		if (users.isPresent()) {

			List<MessageGroup_User> listGroupMessIds = users.get().List_message_group;

			listGroupMessIds.forEach(user_messGroup -> {

				ObjectId msgId = new ObjectId(user_messGroup.messageGroupId);
				Optional<Message_groups> messageGroup = messageGroupsRepository.findById(msgId);

				if (messageGroup.isPresent()) {
					Message_groups msGroups = messageGroup.get();
					rs.add(new UserGroupDto(msGroups._id, msGroups.Message_group_name, msGroups.Message_group_image,
							msGroups.Last_message, user_messGroup.isRead, user_messGroup.role));

				}
			});
		}
		return rs;
	}

	public UpdateResult update_NAME_Message_groups(MessageGroupUpdateDto messageGroup) {
		ObjectId id = new ObjectId(messageGroup._id);
		Query query = new Query(Criteria.where("_id").is(id));
		Update update = new Update().set("Message_group_name", messageGroup.Message_group_name);

		UpdateResult result = mongoTemplate.updateFirst(query, update, Message_groups.class);

		return result;
	}

	public String uploadImageMessageGroup(String MessGR_ID, MultipartFile file)
			throws IOException, IllegalStateException, java.io.IOException {

		ObjectId id = new ObjectId(MessGR_ID);
		// lấy đường dẫn
		String folderPath = root + Utility.FilePath.GroupImagePath;
		// cần 1 chuỗi random, nếu không user đăng bức hình sau cùng tên với bức hình cũ sẽ lỗi
		String fileName = UUID.randomUUID() + file.getOriginalFilename();
		String filePath = folderPath + fileName;

		Optional<Message_groups> messgrs = messageGroupsRepository.findById(id);
		if (!messgrs.isPresent())
			return null;
		// xóa hình cũ, cập nhật hình cho user thì không cần
		String oldPath = folderPath + messgrs.get().Message_group_image;
		
		// cập nhật thì không xài save
		Query query = new Query(Criteria.where("_id").is(id));
		Update update = new Update().set("Message_group_image", fileName);

		UpdateResult result = mongoTemplate.updateFirst(query, update, Message_groups.class);
		
		if (result.wasAcknowledged()) {
			System.out.println(folderPath);
						Files.write(Paths.get(filePath), file.getBytes());
			File fileDeleted = new File(oldPath);
			if (fileDeleted.exists()) {
				if (fileDeleted.delete()) {
					System.out.println("File deleted successfully.");
				} else {
					System.out.println("Failed to delete the file.");
				}
			} else {
				System.out.println("File does not exist.");
			}
			return filePath;
		}

		return null;

	}
}
